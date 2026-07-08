package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.QCInspectionMapper;
import com.example.SCM.dto.request.QCInspectionRequestDTO;
import com.example.SCM.dto.request.QCChecklistRequestDTO;
import com.example.SCM.dto.response.QCInspectionResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.repository.*;
import com.example.SCM.service.QCInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QCInspectionServiceImp implements QCInspectionService {

    private final QCInspectionRepository qcInspectionRepository;
    private final GoodsReceivedNoteRepository goodsReceivedNoteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final QCInspectionMapper qcInspectionMapper;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public QCInspectionResponseDTO save(QCInspectionRequestDTO dto, MultipartFile file) {
        GoodsReceivedNote grn = goodsReceivedNoteRepository.findById(dto.getGrnId()).orElseThrow(() -> new RuntimeException("GRN not found"));
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        User inspector = userRepository.findById(dto.getInspectedBy()).orElseThrow(() -> new RuntimeException("Inspector not found"));

        QCInspection inspection = qcInspectionMapper.toEntity(dto, grn, product, inspector);

        // 🎯 ফিক্স: POST রিকোয়েস্টের সময় চাইল্ড চেকলিস্ট কালেকশন চেইন বাইন্ডিং
        if (dto.getChecklists() != null && !dto.getChecklists().isEmpty()) {
            for (QCChecklistRequestDTO cDto : dto.getChecklists()) {
                QCChecklist chk = new QCChecklist();
                chk.setCheckpointName(cDto.getCheckpointName());
                chk.setPassed(cDto.isPassed());
                chk.setRemarks(cDto.getRemarks());

                // 🔗 ইনভার্স রিলেশনশিপ ম্যাপ (Cascade Type ALL কে সক্রিয় করার জন্য)
                chk.setQcInspection(inspection);
                inspection.getChecklists().add(chk);
            }
        }

        if (file != null && !file.isEmpty()) {
            inspection.setLabTestReport(uploadLabReport(file, dto.getInspectionType()));
        }

        // এখন ফার্স্ট শটেই চাইল্ড ডাটা সহ সেভ হবে
        QCInspection saved = qcInspectionRepository.saveAndFlush(inspection);
        return qcInspectionMapper.convertTOResponseDTO(saved);
    }
    @Override
    @Transactional
    public QCInspectionResponseDTO update(Long id, QCInspectionRequestDTO dto, MultipartFile file) {
        QCInspection inspection = qcInspectionRepository.findById(id).orElseThrow(() -> new RuntimeException("QC Record not found"));

        GoodsReceivedNote grn = goodsReceivedNoteRepository.findById(dto.getGrnId()).orElse(inspection.getGoodsReceivedNote());
        Product product = productRepository.findById(dto.getProductId()).orElse(inspection.getProduct());
        User inspector = userRepository.findById(dto.getInspectedBy()).orElse(inspection.getInspectedBy());

        qcInspectionMapper.updateEntity(dto, inspection, grn, product, inspector);

        // চাইল্ড অবজেক্ট আপডেট হ্যান্ডেলিং (OrphanRemoval এনফোর্সমেন্ট)
        if (dto.getChecklists() != null) {
            inspection.getChecklists().clear();
            for (QCChecklistRequestDTO cDto : dto.getChecklists()) {
                QCChecklist chk = new QCChecklist();
                chk.setCheckpointName(cDto.getCheckpointName());
                chk.setPassed(cDto.isPassed());
                chk.setRemarks(cDto.getRemarks());
                chk.setQcInspection(inspection);
                inspection.getChecklists().add(chk);
            }
        }

        if (file != null && !file.isEmpty()) {
            inspection.setLabTestReport(uploadLabReport(file, dto.getInspectionType()));
        }

        // 🎯 ফিক্স: saveAndFlush() এর মাধ্যমে ট্রানজেকশন বা বাউন্ডারি শেষ হওয়ার আগেই চাইল্ড এন্টিরির লাইফসাইকেল টাইম জেনারেট করানো হলো
        QCInspection updated = qcInspectionRepository.saveAndFlush(inspection);

        return qcInspectionMapper.convertTOResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QCInspectionResponseDTO> findAll() {
        return qcInspectionRepository.findAllInspectionsWithDetails().stream()
                .map(qcInspectionMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QCInspectionResponseDTO> getById(Long id) {
        return qcInspectionRepository.findByIdWithDetails(id).map(qcInspectionMapper::convertTOResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!qcInspectionRepository.existsById(id)) throw new RuntimeException("QC record not found");
        qcInspectionRepository.deleteById(id);
    }

    private String uploadLabReport(MultipartFile file, String inspectionType) {
        try {
            Path path = Paths.get(uploadDir, "qc");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String cleanedName = inspectionType.trim().replaceAll("\\s+", "_");
            String fileName = "QC_" + cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}