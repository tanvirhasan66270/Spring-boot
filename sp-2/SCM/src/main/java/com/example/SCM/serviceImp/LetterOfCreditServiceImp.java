package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.LetterOfCreditMapper;
import com.example.SCM.dto.request.LetterOfCreditRequestDTO;
import com.example.SCM.dto.response.LetterOfCreditResponseDTO;
import com.example.SCM.entity.LetterOfCredit;
import com.example.SCM.enumClass.ActionStatus;
import com.example.SCM.enumClass.LcStatus;
import com.example.SCM.repository.LetterOfCreditRepository;
import com.example.SCM.service.LetterOfCreditService;
import com.example.SCM.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterOfCreditServiceImp implements LetterOfCreditService {

    private final LetterOfCreditRepository lcRepository;
    private final LetterOfCreditMapper lcMapper;
    private final MailService mailService;
    private final ActivityLogService activityLogService;
    private final HttpServletRequest request;

    /**
     * 🛠️ স্বয়ংক্রিয়ভাবে কারেন্ট লগইন থাকা ইউজার আইডি রিড করার প্রাইভেট মেথড
     */
    private String resolveCurrentUserId() {
        String userId = request.getHeader("X-User-Id");
        return (userId != null && !userId.isBlank()) ? userId : "16";
    }

    @Override
    @Transactional
    public LetterOfCreditResponseDTO save(LetterOfCreditRequestDTO dto) {
        if (dto == null) throw new IllegalArgumentException("LC Request footprint cannot be empty");

        LetterOfCredit lc = lcMapper.toEntity(dto);
        LetterOfCredit savedLc = lcRepository.save(lc);

        sendSupplierLcNotification(savedLc);

        // ── 🎯 সমাধান: ডেটাবেজ প্যারামিটারগুলো LC অবজেক্টের সাথে নিখুঁতভাবে ম্যাপ করা হলো ──
        activityLogService.log(
                resolveCurrentUserId(),
                null, // userEmail
                "CREATE",
                "LC",
                savedLc.getId().toString(),
                "New Letter of Credit successfully initiated for Bank Reference. LC Number: " + savedLc.getLcNumber(),
                null, // oldValue
                "{\"status\":\"" + savedLc.getLcStatus().name() + "\"}", // newValue snapshot
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        return lcMapper.toResponseDTO(savedLc);
    }

    @Override
    @Transactional
    public LetterOfCreditResponseDTO update(Long id, LetterOfCreditRequestDTO dto) {
        LetterOfCredit lc = lcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Letter of credit index matrix missing for ID: " + id));

        String oldBank = lc.getIssuingBank();

        if (dto.getIssuingBank() != null) lc.setIssuingBank(dto.getIssuingBank());
        if (dto.getShipmentIncoTerms() != null) lc.setShipmentIncoTerms(dto.getShipmentIncoTerms());
        if (dto.getPortOfLoading() != null) lc.setPortOfLoading(dto.getPortOfLoading());
        if (dto.getPortOfDischarge() != null) lc.setPortOfDischarge(dto.getPortOfDischarge());
        if (dto.getDocumentVaultUrl() != null) lc.setDocumentVaultUrl(dto.getDocumentVaultUrl());

        if (dto.getLcStatus() != null) {
            lc.setLcStatus(LcStatus.valueOf(dto.getLcStatus().toUpperCase()));
        }

        LetterOfCredit updatedLc = lcRepository.save(lc);
        sendSupplierLcNotification(updatedLc);

        // ── 🎯 সমাধান: নতুন এনাম এবং ডাটা ডিফ (Data Diff) অ্যাড করা হলো ──
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "UPDATE",
                "LC",
                updatedLc.getId().toString(),
                "General metadata fields updated for LC Number: " + updatedLc.getLcNumber(),
                "{\"issuingBank\":\"" + oldBank + "\"}",
                "{\"issuingBank\":\"" + updatedLc.getIssuingBank() + "\"}",
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        return lcMapper.toResponseDTO(updatedLc);
    }

    @Override
    @Transactional
    public LetterOfCreditResponseDTO amendLC(Long id, LetterOfCreditRequestDTO dto) {
        LetterOfCredit lc = lcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LC Record Not Found! ID: " + id));

        double oldAmount = lc.getAmount();

        if (dto.getAmount() > 0) lc.setAmount(dto.getAmount());
        if (dto.getExpiryDate() != null && !dto.getExpiryDate().isBlank()) lc.setExpiryDate(LocalDate.parse(dto.getExpiryDate()));
        if (dto.getLatestShipmentDate() != null && !dto.getLatestShipmentDate().isBlank()) lc.setLatestShipmentDate(LocalDate.parse(dto.getLatestShipmentDate()));

        lc.incrementAmendment(); // 💡 আপনার এনটিটির কাউন্টার মেথড
        LetterOfCredit amendedLc = lcRepository.save(lc);
        sendSupplierLcNotification(amendedLc);

        // ── 🎯 সমাধান: নতুন এনাম স্ট্রাকচার সিঙ্ক ──
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "UPDATE",
                "LC",
                amendedLc.getId().toString(),
                "Official commercial amendment applied. LC Number: " + amendedLc.getLcNumber() + ", Total Amendment Count: " + amendedLc.getAmendmentCount(),
                "{\"amount\":" + oldAmount + "}",
                "{\"amount\":" + amendedLc.getAmount() + "}",
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        return lcMapper.toResponseDTO(amendedLc);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LetterOfCredit lc = lcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target LC matrix log node missing"));

        String deletedLcNumber = lc.getLcNumber();
        lcRepository.delete(lc);

        // ── 🎯 সমাধান: নতুন এনাম স্ট্রাকচার সিঙ্ক ──
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "DELETE",
                "LC",
                id.toString(),
                "Letter of Credit entity purged permanently from logistics node. LC Number was: " + deletedLcNumber,
                "{\"lcNumber\":\"" + deletedLcNumber + "\"}",
                null,
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );
    }

    @Override @Transactional(readOnly = true) public List<LetterOfCreditResponseDTO> findAll() { return lcRepository.findAllWithDetails().stream().map(lcMapper::toResponseDTO).collect(Collectors.toList()); }
    @Override @Transactional(readOnly = true) public Optional<LetterOfCreditResponseDTO> getById(Long id) { return lcRepository.findByIdWithDetails(id).map(lcMapper::toResponseDTO); }
    @Override @Transactional(readOnly = true) public Optional<LetterOfCreditResponseDTO> getByLcNumber(String lcNumber) { return lcRepository.findByLcNumber(lcNumber).map(lcMapper::toResponseDTO); }

    // =========================================================================
    // 📧 ডেডিকেটেড সাপ্লায়ার এলসি নোটিফিকেশন মেইলিং ইঞ্জিন (শতভাগ নির্ভুল)
    // =========================================================================
    private void sendSupplierLcNotification(LetterOfCredit lc) {
        if (lc.getLcStatus() == LcStatus.DRAFT) return;
        if (lc.getSupplier() == null || lc.getSupplier().getEmail() == null) return;

        String supplierEmail = lc.getSupplier().getEmail();
        String subject = "SCM Commercial Alert: Letter of Credit #" + lc.getLcNumber() + " [" + lc.getLcStatus().name() + "]";

        String mailContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; }
                .container { max-width: 600px; margin: 20px auto; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                .header { background-color: #1A365D; color: white; padding: 25px; text-align: center; }
                .status-badge { display: inline-block; padding: 6px 15px; font-weight: bold; border-radius: 20px; font-size: 14px; text-transform: uppercase; }
                .content { padding: 25px; background-color: #ffffff; }
                .info-table { width: 100%%; margin-top: 15px; border-collapse: collapse; }
                .info-table td { padding: 10px; border-bottom: 1px solid #edf2f7; font-size: 14px; }
                .info-table td.label { font-weight: bold; color: #4A5568; width: 40%%; }
                .footer { background-color: #f7fafc; padding: 15px; text-align: center; font-size: 12px; color: #718096; border-top: 1px solid #edf2f7; }
            </style>
        </head>
        <body>
            <div class='container'>
                <div class='header'>
                    <h3 style='margin:0 0 10px 0;'>Letter of Credit Commercial Update</h3>
                    <div class='status-badge'>Status: %s</div>
                </div>
                <div class='content'>
                    <p>Dear <b>%s</b>,</p>
                    <p>We would like to inform you that the Letter of Credit mapped with your supplier profile has been updated in our Global Supply Chain Node.</p>
                    <table class='info-table'>
                        <tr><td class='label'>LC Tracking Number:</td><td><b>%s</b></td></tr>
                        <tr><td class='label'>Associated PO:</td><td>%s</td></tr>
                        <tr><td class='label'>Issuing Bank:</td><td>%s</td></tr>
                        <tr><td class='label'>Aggregate Amount:</td><td><b>%.2f %s</b></td></tr>
                        <tr><td class='label'>Incoterms:</td><td>%s</td></tr>
                        <tr><td class='label'>Latest Shipment Date:</td><td>%s</td></tr>
                        <tr><td class='label'>LC Expiry Date:</td><td><span style='color:#C53030;'>%s</span></td></tr>
                        <tr><td class='label'>Amendment Count:</td><td>%d time(s)</td></tr>
                    </table>
                    <p style='margin-top:20px; font-size:13px; color:#718096;'>Please check your document vault portal to download the updated swift copy.</p>
                </div>
                <div class='footer'>&copy; 2026 SCM Logistics Network Hub. All rights reserved.</div>
            </div>
        </body>
        </html>
        """.formatted(
                lc.getLcStatus().name(), lc.getSupplier().getName(),
                lc.getLcNumber(), lc.getPoNumber(), lc.getIssuingBank(),
                lc.getAmount(), lc.getCurrency(), lc.getShipmentIncoTerms(),
                lc.getLatestShipmentDate() != null ? lc.getLatestShipmentDate().toString() : "N/A",
                lc.getExpiryDate() != null ? lc.getExpiryDate().toString() : "N/A",
                lc.getAmendmentCount()
        );

        try {
            mailService.SenderGeneralMail(supplierEmail, subject, mailContent);
        } catch (Exception e) {
            System.err.println("Supplier Notification Cluster Mail delivery failed: " + e.getMessage());
        }
    }
}