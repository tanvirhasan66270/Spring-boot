package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.PurchaseRequisitionMapper;
import com.example.SCM.dto.request.PurchaseRequisitionRequestDTO;
import com.example.SCM.dto.response.PurchaseRequisitionResponseDTO;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Supplier;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.ActionStatus;
import com.example.SCM.enumClass.PurchaseRequisitionStatus;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.repository.PurchaseRequisitionRepository;
import com.example.SCM.repository.SupplierRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.service.ActivityLogService;
import com.example.SCM.service.PurchaseRequisitionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder; // 🌟 ইনপোর্ট করা হলো
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseRequisitionServiceImp implements PurchaseRequisitionService {

    private final PurchaseRequisitionRepository requisitionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PurchaseRequisitionMapper requisitionMapper;
    private final MailService mailService;
    private final ActivityLogService activityLogService;
    private final HttpServletRequest request;

    @Override
    @Transactional
    public PurchaseRequisitionResponseDTO save(PurchaseRequisitionRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Requisition data cannot be null");
        }

        List<Product> products = null;
        if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
            products = productRepository.findAllById(dto.getProductIds());
            if (products.size() != dto.getProductIds().size()) {
                throw new RuntimeException("One or more Product IDs are invalid!");
            }
        }

        List<Supplier> suppliers = null;
        if (dto.getSupplierIds() != null && !dto.getSupplierIds().isEmpty()) {
            suppliers = supplierRepository.findAllById(dto.getSupplierIds());
            if (suppliers.size() != dto.getSupplierIds().size()) {
                throw new RuntimeException("One or more Supplier IDs are invalid!");
            }
        }

        PurchaseRequisition pr = requisitionMapper.toEntity(dto, products, suppliers);

        pr.setApprovalStatus(PurchaseRequisitionStatus.PENDING);
        PurchaseRequisition savedPr = requisitionRepository.save(pr);

        return requisitionMapper.convertTOResponseDTO(savedPr);
    }

    // Manager Approval Node (Auto-Tracks Manager Context & Returns DTO)
    @Transactional
    @Override
    public PurchaseRequisitionResponseDTO approveRequisition(Long id) {
        PurchaseRequisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Requisition missing for ID: " + id));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long managerId = null;
        String managerEmail = "system@scm.com";

        if (principal instanceof User) {
            User currentManager = (User) principal;
            managerId = currentManager.getId();
            managerEmail = currentManager.getEmail();
        } else {
            throw new RuntimeException("Unauthorized transaction: Active corporate session not found!");
        }

        // স্ট্যাটাস এবং অনুমোদনকারীর আইডি সেট করা হচ্ছে
        requisition.setApprovalStatus(PurchaseRequisitionStatus.APPROVED);
        requisition.setApprovedBy(managerId); // 🔄 এনটিটিতে অটো-সেট হয়ে গেল

        PurchaseRequisition savedRequisition = requisitionRepository.save(requisition);

        // অ্যাক্টিভিটি লগ সিস্টেমে ম্যানেজারের ডেটা ট্র্যাক করা হচ্ছে
        activityLogService.log(
                managerId.toString(),
                managerEmail,
                "APPROVE",
                "PURCHASE_REQUISITION",
                savedRequisition.getId().toString(),
                "Manager approved requisition for " + savedRequisition.getSuppliers().size() + " suppliers.",
                "PENDING",
                "APPROVED",
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        // সাপ্লায়ারদের কাছে নোটিশ ডিসপ্যাচ
        if (savedRequisition.getSuppliers() != null && !savedRequisition.getSuppliers().isEmpty()) {
            sendRequisitionEmailToSuppliers(savedRequisition);
        }

        return requisitionMapper.convertTOResponseDTO(savedRequisition);
    }

    // Manager Disapproval Matrix Node (Auto-Tracks Manager Context & Returns DTO)
    @Transactional
    @Override
    public PurchaseRequisitionResponseDTO rejectOrCancelRequisition(Long id, String actionType) {
        PurchaseRequisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Requisition missing for ID: " + id));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long managerId = null;
        if (principal instanceof User) {
            managerId = ((User) principal).getId();
        }

        String oldStatus = requisition.getApprovalStatus().name();
        if ("REJECT".equalsIgnoreCase(actionType)) {
            requisition.setApprovalStatus(PurchaseRequisitionStatus.REJECTED);
        } else if ("CANCEL".equalsIgnoreCase(actionType)) {
            requisition.setApprovalStatus(PurchaseRequisitionStatus.CANCELLED);
        } else {
            throw new IllegalArgumentException("Invalid routing parameters for action status");
        }

        requisition.setApprovedBy(managerId); // কে ডিসিশন নিয়েছে তার আইডি ট্র্যাক করা হলো

        PurchaseRequisition savedRequisition = requisitionRepository.save(requisition);

        // ক্রিয়েটর প্রকিউরমেন্ট অফিসারের কাছে অ্যালার্ট মেইল পাঠানো
        if (savedRequisition.getRequestedBy() != null) {
            userRepository.findById(savedRequisition.getRequestedBy())
                    .ifPresent(officer -> sendAlertEmailToProcurement(savedRequisition, officer));
        }

        return requisitionMapper.convertTOResponseDTO(savedRequisition);
    }

    @Override
    @Transactional
    public PurchaseRequisitionResponseDTO update(Long id, PurchaseRequisitionRequestDTO dto) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Requisition not found with ID: " + id));

        // প্রজেক্ট রুলস অনুযায়ী লক থাকা অবস্থায় এডিট ব্লক করা হলো
        if (pr.getApprovalStatus() == PurchaseRequisitionStatus.PENDING ||
                pr.getApprovalStatus() == PurchaseRequisitionStatus.APPROVED ||
                pr.getApprovalStatus() == PurchaseRequisitionStatus.REJECTED ||
                pr.getApprovalStatus() == PurchaseRequisitionStatus.CANCELLED) {
            throw new RuntimeException("Locked! Requisitions in " + pr.getApprovalStatus() + " state cannot be modified.");
        }

        return requisitionMapper.convertTOResponseDTO(pr);
    }

    private void sendRequisitionEmailToSuppliers(PurchaseRequisition pr) {
        if (pr.getSuppliers() == null || pr.getSuppliers().isEmpty()) return;

        StringBuilder productRows = new StringBuilder();
        if (pr.getProducts() != null) {
            for (Product product : pr.getProducts()) {
                productRows.append("<tr>")
                        .append("<td style='padding:10px; border-bottom:1px solid #edf2f7;'>").append(product.getId()).append("</td>")
                        .append("<td style='padding:10px; border-bottom:1px solid #edf2f7; font-weight:bold;'>").append(product.getName()).append("</td>")
                        .append("</tr>");
            }
        }

        String urgency = (pr.getUrgencyLevel() != null) ? pr.getUrgencyLevel().name() : "NORMAL";
        String subject = "SCM RFQ [" + urgency + "]: Authorized Procurement Order Alert - ID #" + pr.getId();

        for (Supplier supplier : pr.getSuppliers()) {
            if (supplier.getEmail() == null || supplier.getEmail().isBlank()) continue;

            String mailContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #2D3748; background-color: #f7fafc; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #E2E8F0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { background-color: #2F855A; color: white; padding: 25px; text-align: center; }
                    .content { padding: 30px; }
                    .info-table { width: 100%%; margin-top: 15px; border-collapse: collapse; }
                    .info-table td { padding: 10px; border-bottom: 1px solid #edf2f7; font-size: 14px; }
                    .info-table td.label { font-weight: bold; color: #4A5568; width: 40%%; }
                    .product-table { width: 100%%; margin-top: 20px; border-collapse: collapse; text-align: left; }
                    .product-table th { background-color: #EDF2F7; padding: 10px; font-size: 14px; color: #2D3748; }
                    .remarks-box { background-color: #F7FAFC; border-left: 4px solid #2F855A; padding: 15px; margin-top: 15px; font-style: italic; }
                    .footer { background-color: #F7FAFC; padding: 15px; text-align: center; font-size: 12px; color: #718096; border-top: 1px solid #E2E8F0; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'>
                        <h2 style='margin:0;'>SCM Approved Requisition</h2>
                        <span style='background:#FFF; color:#2F855A; padding:3px 10px; font-weight:bold; border-radius:12px; font-size:12px;'>STATUS: APPROVED</span>
                    </div>
                    <div class='content'>
                        <p>Dear <b>%s</b>,</p>
                        <p>A new purchase requisition has been officially audited and <b>APPROVED</b> by SCM Board Management. You have been authorized to review the requirement matrix:</p>
                        
                        <table class='info-table'>
                            <tr><td class='label'>Requisition Reference:</td><td><b>#%s</b></td></tr>
                            <tr><td class='label'>Required Quantity:</td><td><span style='color:#2F855A; font-weight:bold;'>%d Units</span></td></tr>
                            <tr><td class='label'>Target Timeline Date:</td><td><b>%s</b></td></tr>
                            <tr><td class='label'>Preferred Currency:</td><td><b>%s</b></td></tr>
                            <tr><td class='label'>Urgency Level:</td><td><span style='color:#C53030; font-weight:bold;'>%s</span></td></tr>
                        </table>

                        <h3 style='margin-top:25px; color:#2F855A; border-bottom:2px solid #EDF2F7; padding-bottom:5px;'>Requested Inventory Specifications:</h3>
                        <table class='product-table'>
                            <thead><tr><th style='width:30%%;'>Product ID</th><th>Product nomenclature</th></tr></thead>
                            <tbody>%s</tbody>
                        </table>

                        <h4 style='margin-bottom:5px; color:#4A5568;'>Sourcing Directives:</h4>
                        <div class='remarks-box'>"%s"</div>
                    </div>
                    <div class='footer'>&copy; 2026 SCM Logistics Operations Gateway.</div>
                </div>
            </body>
            </html>
            """.formatted(
                    supplier.getName(), pr.getId().toString(), pr.getQuantityRequired(),
                    pr.getRequiredByDate().toString(), pr.getCurrency(), pr.getUrgencyLevel().name(),
                    productRows.toString(), pr.getRemarks() != null ? pr.getRemarks() : "No additional specifications."
            );

            try {
                mailService.senderGeneralMail(supplier.getEmail(), subject, mailContent);
            } catch (Exception e) {
                System.err.println("Failed to route notice: " + supplier.getEmail());
            }
        }
    }

    private void sendAlertEmailToProcurement(PurchaseRequisition requisition, User procurementOfficer) {
        String officerEmail = procurementOfficer.getEmail();
        String currentStatus = requisition.getApprovalStatus().name();
        String subject = "⚠️ SCM Operations Alert: Requisition Status Locked - " + currentStatus;

        String mailContent = """
        <!DOCTYPE html><html><head><style>body{font-family:'Segoe UI',Arial,sans-serif;line-height:1.6;color:#2D3748;padding:20px}.container{max-width:600px;margin:0 auto;background:#fff;border:1px solid #E2E8F0;border-radius:8px;overflow:hidden}.header-alert{background-color:#C53030;color:#fff;padding:25px;text-align:center}.content{padding:30px}.alert-box{background-color:#FFF5F5;border-left:4px solid #C53030;padding:15px;margin:15px 0;font-weight:700;color:#9B2C2C}.footer{background-color:#F7FAFC;padding:15px;text-align:center;font-size:12px;color:#718096;border-top:1px solid #E2E8F0}</style></head>
        <body><div class='container'><div class='header-alert'><h2 style='margin:0;'>Requisition Tracking Node</h2></div><div class='content'><p>Dear SCM Procurement Node (<b>%s</b>),</p><p>Your generated purchase requisition profile has been locked with the following parameter matrix:</p><div class='alert-box'>Requisition ID: #%d<br>Evaluation State: %s</div><p>Please check your warehouse catalog guidelines before initiating any new pipeline requests.</p></div><div class='footer'>&copy; 2026 SCM Sourcing Engine.</div></div></body></html>
        """.formatted(procurementOfficer.getName(), requisition.getId(), currentStatus);

        try { mailService.senderGeneralMail(officerEmail, subject, mailContent); } catch (Exception e) { System.err.println("Back routing alert mail failed."); }
    }

    @Override @Transactional(readOnly = true) public List<PurchaseRequisitionResponseDTO> findAll() { return requisitionRepository.findAll().stream().map(requisitionMapper::convertTOResponseDTO).collect(Collectors.toList()); }
    @Override @Transactional(readOnly = true) public Optional<PurchaseRequisitionResponseDTO> getById(Long id) { return requisitionRepository.findById(id).map(requisitionMapper::convertTOResponseDTO); }

    @Override
    @Transactional
    public void delete(Long id) {
        throw new RuntimeException("Hard-Locked! Executed Procurement Requisitions cannot be deleted from matrix index!");
    }
}