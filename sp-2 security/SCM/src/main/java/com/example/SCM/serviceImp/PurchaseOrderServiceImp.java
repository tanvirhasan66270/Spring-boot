package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService; // 💡 মেইলিং ইঞ্জিন ইম্পোর্ট
import com.example.SCM.dto.mapper.PurchaseOrderMapper;
import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.enumClass.PurchaseOrderStatus;
import com.example.SCM.repository.PurchaseOrderRepository;
import com.example.SCM.repository.PurchaseOrderTokenRepository;
import com.example.SCM.repository.QuotationRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PurchaseOrderServiceImp implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final QuotationRepository quotationRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final MailService mailService;
    private final PurchaseOrderTokenRepository tokenRepository;

    private final UserRepository userRepository;


    @Override
    @Transactional
    public PurchaseOrderResponseDTO save(PurchaseOrderRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Purchase Order data cannot be null");
        }

        Quotation quotation = quotationRepository.findById(dto.getQuotationId())
                .orElseThrow(() -> new RuntimeException("Quotation not found with ID: " + dto.getQuotationId()));

        Supplier supplier = quotation.getSupplier();
        if (supplier == null) {
            throw new RuntimeException("No Supplier is linked with the selected Quotation!");
        }

        PurchaseRequisition purchaseRequisition = quotation.getPurchaseRequisition();
        if (purchaseRequisition == null) {
            throw new RuntimeException("No Purchase Requisition is linked with the selected Quotation!");
        }

        PurchaseOrder po = purchaseOrderMapper.toEntity(dto, quotation, supplier, purchaseRequisition);

        // প্রকিউরমেন্ট টিম যখন ডাটা সেভ/সেন্ড করবে প্রাথমিক স্টেট সবসময় DRAFT থাকবে
        po.setStatus(PurchaseOrderStatus.DRAFT);
        PurchaseOrder savedPo = purchaseOrderRepository.save(po);
        PurchaseOrderToken token = new PurchaseOrderToken();

        token.setToken(UUID.randomUUID().toString());

        token.setActive(true);

        token.setExpiryDate(savedPo.getExpectedDeliveryDate());

        token.setPurchaseOrderId(savedPo.getId());

        token.setPoNumber(savedPo.getPoNumber());

        token.setIssuedBy(savedPo.getIssuedBy());

        token.setQuantity(savedPo.getQuantity());

        token.setTotalAmount(savedPo.getTotalAmount());

        token.setCurrency(savedPo.getCurrency());

        token.setExpectedDeliveryDate(savedPo.getExpectedDeliveryDate());

        token.setStatus(savedPo.getStatus());

        token.setSupplierId(savedPo.getSupplier().getId());

        token.setPurchaseRequisitionId(savedPo.getPurchaseRequisition().getId());

        token.setQuotationId(savedPo.getQuotation().getId());

        token.setPurchaseCreatedAt(savedPo.getCreatedAt());

        token.setPurchaseUpdatedAt(savedPo.getUpdatedAt());

      tokenRepository.save(token);

        //ম্যানেজারকে ইমেইলে ওয়ান-ক্লিক ISSUED গেটওয়ে রিকোয়েস্ট পাঠানো
        sendPoApprovalMailToManager(savedPo);

        return purchaseOrderMapper.convertTOResponseDTO(savedPo);
    }

    //  Manager One-Click Gateway: Status -> ISSUED & Mail to Supplier

    @Override
    public PurchaseOrderResponseDTO managerIssuedOrder(Long id, Long managerId) {

        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order node missing at ID: " + id));

        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new RuntimeException("Order has already been processed or Issued!");
        }


        po.setStatus(PurchaseOrderStatus.ISSUED);
        PurchaseOrder issuedPo = purchaseOrderRepository.save(po);

        //সাপ্লায়ারের জিমেইলে অফিশিয়াল ডাইনামিক PO নোটিফিকেশন পাঠানো
        sendPoIssuedMailToSupplier(issuedPo);

        return purchaseOrderMapper.convertTOResponseDTO(issuedPo);
    }

      //Supplier Dashboard / One-Click Gateway: Status -> RECEIVED

    @Override
    public PurchaseOrderResponseDTO supplierReceivedOrder(Long id) {

        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order node missing at ID: " + id));

        if (po.getStatus() != PurchaseOrderStatus.ISSUED) {
            throw new RuntimeException("Only ISSUED orders can be acknowledged or Received by Supplier!");
        }

        po.setStatus(PurchaseOrderStatus.RECEIVED);
        PurchaseOrder receivedPo = purchaseOrderRepository.save(po);

        return purchaseOrderMapper.convertTOResponseDTO(receivedPo);
    }

      //Quantities Match -> PARTIALLY_RECEIVED Trigger

    @Transactional
    public PurchaseOrderResponseDTO updateShipmentQuantityCheck(Long id, int shippedQuantity) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order missing for Cargo Update at ID: " + id));

        // শিপমেন্ট কোয়ান্টিটি যদি অরিজিনাল অর্ডারের তুলনায় কম হয়, তবে স্ট্যাটাস অটোমেটিক আপডেট হবে
        if (shippedQuantity < po.getQuantity()) {
            po.setStatus(PurchaseOrderStatus.PARTIALLY_RECEIVED);
        } else {
            po.setStatus(PurchaseOrderStatus.RECEIVED);
        }

        return purchaseOrderMapper.convertTOResponseDTO(purchaseOrderRepository.save(po));
    }


    private void sendPoApprovalMailToManager(PurchaseOrder po) {

        List<User> managers = userRepository.findByRole(Role.MANAGER);




        //প্রজেক্টে কোম্পানির ম্যানেজারের অফিশিয়াল মেইল বসে যাবে
        String managerEmail = "manager@company.com";
        String subject = "SCM Approval Request: Authorize Purchase Order #" + po.getPoNumber();

        String issueUrl = "http://localhost:8085/api/purchase-orders/email-issue?id=" + po.getId() + "&managerId=1";

        String mailContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; color: #2D3748; }
                .container { max-width: 600px; margin: 20px auto; border: 1px solid #E2E8F0; border-radius: 8px; overflow: hidden; }
                .header { background-color: #2B6CB0; color: white; padding: 20px; text-align: center; }
                .btn { background-color: #3182CE; color: white !important; padding: 12px 30px; font-weight: bold; text-decoration: none; border-radius: 5px; display: inline-block; }
            </style>
        </head>
        <body>
            <div class='container'>
                <div class='header'><h2>Purchase Order Review Authorization</h2></div>
                <div style='padding: 25px;'>
                    <p>Dear Manager,</p>
                    <p>A new purchase procurement track has been compiled. Please review the financial outline below:</p>
                    <ul>
                        <li><b>PO Number:</b> %s</li>
                        <li><b>Supplier:</b> %s</li>
                        <li><b>Total Volume:</b> %d Units</li>
                        <li><b>Financial Aggregate:</b> %.2f %s</li>
                        <li><b>Expected Delivery:</b> %s</li>
                    </ul>
                    <div style='text-align: center; margin: 30px 0;'>
                        <a href='%s' class='btn'>✔ ISSUE THIS ORDER NOW</a>
                    </div>
                </div>
            </div>
        </body>
        </html>
        """.formatted(po.getPoNumber(), po.getSupplier().getName(), po.getQuantity(),
                po.getTotalAmount(), po.getCurrency(), po.getExpectedDeliveryDate().toString(), issueUrl);

        try {


            for (User manager : managers) {
                mailService.senderGeneralMail(
                        manager.getEmail(),
                        subject,
                        mailContent
                );
            }


            mailService.senderGeneralMail(managerEmail, subject, mailContent);
        } catch (Exception e) {
            System.err.println("Manager PO Dispatch Pipeline Failed: " + e.getMessage());
        }
    }

    private void sendPoIssuedMailToSupplier(PurchaseOrder po) {
        if (po.getSupplier() == null || po.getSupplier().getEmail() == null) return;

        String supplierEmail = po.getSupplier().getEmail();
        String subject = "SCM Commercial Dispatch: Official Purchase Order #" + po.getPoNumber();

        String receiveUrl = "http://localhost:8085/api/purchase-orders/email-receive?id=" + po.getId();

        String mailContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; color: #2D3748; }
                .container { max-width: 600px; margin: 20px auto; border: 1px solid #E2E8F0; border-radius: 8px; overflow: hidden; }
                .header { background-color: #1A202C; color: white; padding: 20px; text-align: center; }
                .btn { background-color: #38A169; color: white !important; padding: 12px 30px; font-weight: bold; text-decoration: none; border-radius: 5px; display: inline-block; }
            </style>
        </head>
        <body>
            <div class='container'>
                <div class='header'><h2>Official Purchase Order Dispatched</h2></div>
                <div style='padding: 25px;'>
                    <p>Dear <b>%s</b>,</p>
                    <p>We are pleased to place an official firm corporate order with your production facility. Commercial parameters are enclosed below:</p>
                    <ul>
                        <li><b>Purchase Order Id:</b> #%s</li>
                        <li><b>Supply Quota Volume:</b> %d Units</li>
                        <li><b>Commercial Settlement:</b> %.2f %s</li>
                        <li><b>Latest Required Shipment Date:</b> %s</li>
                    </ul>
                    <p>Please click down under to instantly acknowledge receipt and confirm your delivery routing window:</p>
                    <div style='text-align: center; margin: 30px 0;'>
                        <a href='%s' class='btn'>🤝 ACKNOWLEDGE & RECEIVE ORDER</a>
                    </div>
                </div>
            </div>
        </body>
        </html>
        """.formatted(po.getSupplier().getName(), po.getPoNumber(), po.getQuantity(),
                po.getTotalAmount(), po.getCurrency(), po.getExpectedDeliveryDate().toString(), receiveUrl);

        try {
            mailService.senderGeneralMail(supplierEmail, subject, mailContent);
        } catch (Exception e) {
            System.err.println("Supplier PO Dispatch Pipeline Failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDTO update(Long id, PurchaseOrderRequestDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        PurchaseOrder po = purchaseOrderRepository.findByIdWithDetails(id)
                .orElseThrow(() ->
                        new RuntimeException("PO not found with ID: " + id));

        Quotation quotation = po.getQuotation();
        Supplier supplier = po.getSupplier();
        PurchaseRequisition pr = po.getPurchaseRequisition();

        // Quotation Change হলে Supplier ও PR Auto Update
        if (dto.getQuotationId() != null &&
                !dto.getQuotationId().equals(quotation.getId())) {

            quotation = quotationRepository.findById(dto.getQuotationId())
                    .orElseThrow(() ->
                            new RuntimeException("Quotation not found with ID: "
                                    + dto.getQuotationId()));

            supplier = quotation.getSupplier();
            pr = quotation.getPurchaseRequisition();
        }

        // Update Purchase Order
        purchaseOrderMapper.updateEntity(dto, po, quotation, supplier, pr);

        PurchaseOrder updated = purchaseOrderRepository.save(po);

        // ===========================
        // Update Purchase Order Token
        // ===========================

        PurchaseOrderToken token =
                tokenRepository
                        .findByPurchaseOrderId(updated.getId())
                        .orElse(null);

        if (token != null) {

            token.setPoNumber(updated.getPoNumber());

            token.setIssuedBy(updated.getIssuedBy());

            token.setQuantity(updated.getQuantity());

            token.setTotalAmount(updated.getTotalAmount());

            token.setCurrency(updated.getCurrency());

            token.setExpectedDeliveryDate(updated.getExpectedDeliveryDate());

            token.setStatus(updated.getStatus());

            token.setSupplierId(updated.getSupplier().getId());

            token.setPurchaseRequisitionId(
                    updated.getPurchaseRequisition().getId());

            token.setQuotationId(updated.getQuotation().getId());

            token.setPurchaseUpdatedAt(updated.getUpdatedAt());

            tokenRepository.save(token);
        }

        return purchaseOrderMapper.convertTOResponseDTO(updated);
    }



    @Override @Transactional(readOnly = true) public List<PurchaseOrderResponseDTO> findAll() { return purchaseOrderRepository.findAllPurchaseOrders().stream().map(purchaseOrderMapper::convertTOResponseDTO).collect(Collectors.toList()); }
    @Override @Transactional(readOnly = true) public Optional<PurchaseOrderResponseDTO> getById(Long id) { return purchaseOrderRepository.findByIdWithDetails(id).map(purchaseOrderMapper::convertTOResponseDTO); }
    @Override @Transactional public void delete(Long id) { if (!purchaseOrderRepository.existsById(id)) throw new RuntimeException("PO not found with ID: " + id); purchaseOrderRepository.deleteById(id); }



}