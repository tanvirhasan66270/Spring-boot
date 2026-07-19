package com.example.SCM.serviceImp;

import com.example.SCM.dto.request.QuotationRequestDTO;
import com.example.SCM.dto.response.QuotationResponseDTO;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Quotation;
import com.example.SCM.dto.mapper.QuotationMapper;
import com.example.SCM.repository.QuotationRepository;
import com.example.SCM.repository.PurchaseRequisitionRepository; // পিআর রিপোজিটরি ইমপোর্ট করুন
import com.example.SCM.service.QuotationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationServiceImp implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final PurchaseRequisitionRepository requisitionRepository;
    private final QuotationMapper quotationMapper;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public QuotationResponseDTO save(QuotationRequestDTO dto, MultipartFile image) {
        try {
            System.out.println("Processing Quotation for PR ID: " + dto.getPurchaseRequisitionId());

            Quotation quotation = quotationMapper.toEntity(dto);

            if (dto.getPurchaseRequisitionId() != null) {
                PurchaseRequisition pr = requisitionRepository.findById(dto.getPurchaseRequisitionId())
                        .orElseThrow(() -> new EntityNotFoundException("Purchase Requisition record missing at ID: " + dto.getPurchaseRequisitionId()));

                if (dto.getQuantity() > pr.getQuantityRequired()) {
                    throw new IllegalArgumentException("Quantity limit exceeded! Your quantity limit is: " + pr.getQuantityRequired() + ".");
                }

                if (pr.getProducts() != null && !pr.getProducts().isEmpty()) {
                    com.example.SCM.entity.Product firstProduct = pr.getProducts().stream()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("No product linked to PR"));

                    quotation.setProduct(firstProduct);
                    String combinedProductNames = pr.getProducts().stream()
                            .map(product -> product.getName())
                            .collect(Collectors.joining(", "));
                    quotation.setProductName(combinedProductNames);
                }
            }

            if (image != null && !image.isEmpty()) {
                String fallBackName = (quotation.getProductName() != null) ? quotation.getProductName() : "quotation_sheet";
                String fileUrl = saveImageFile(image, fallBackName);
                quotation.setAttachmentUrl(fileUrl);
            }

            if (quotation.getQuotationNumber() == null) {
                quotation.setQuotationNumber("QTN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }

            Quotation savedQuotation = quotationRepository.save(quotation);
            return quotationMapper.toResponseDTO(savedQuotation);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    @Transactional
    public QuotationResponseDTO update(Long id, QuotationRequestDTO dto) {
        Quotation existingQuotation = quotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quotation not found with id: " + id));

        PurchaseRequisition pr = null;
        if (dto.getPurchaseRequisitionId() != null) {
            pr = requisitionRepository.findById(dto.getPurchaseRequisitionId())
                    .orElseThrow(() -> new EntityNotFoundException("Requisition node failed to resolve."));
        } else if (existingQuotation.getPurchaseRequisition() != null) {
            pr = existingQuotation.getPurchaseRequisition();
        }

        if (pr != null && dto.getQuantity() > pr.getQuantityRequired()) {
            throw new IllegalArgumentException("Quantity limit exceeded! Your quantity limit is: " + pr.getQuantityRequired() + ".");
        }

        quotationMapper.updateEntityFromDTO(dto, existingQuotation);

        if (dto.getPurchaseRequisitionId() != null) {
            PurchaseRequisition pur = requisitionRepository.findById(dto.getPurchaseRequisitionId())
                    .orElseThrow(() -> new EntityNotFoundException("Requisition node failed to resolve."));

            if (pur.getProducts() != null && !pur.getProducts().isEmpty()) {
                String combinedProductNames = pur.getProducts().stream()
                        .map(product -> product.getName())
                        .collect(Collectors.joining(", "));

                existingQuotation.setProductName(combinedProductNames);
            }
        }

        Quotation updatedQuotation = quotationRepository.save(existingQuotation);
        return quotationMapper.toResponseDTO(updatedQuotation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuotationResponseDTO> findAll() {
        return quotationRepository.findAllWithDetails()
                .stream()
                .map(quotationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuotationResponseDTO> getById(Long id) {
        return quotationRepository.findByIdWithDetails(id)
                .map(quotationMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!quotationRepository.existsById(id)) {
            throw new EntityNotFoundException("Quotation not found with id: " + id);
        }
        quotationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public QuotationResponseDTO updateStatus(Long id, String status) {
        // ১. ডাটাবেজ থেকে কোটেশনটি খুঁজে বের করুন
        Quotation existingQuotation = quotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quotation not found with id: " + id));

        // ২. স্ট্যাটাসটি আপডেট করুন (Enum-এর সাথে মিলিয়ে)
        // নিশ্চিত করুন আপনার QuotationStatus Enum-এ এই নামগুলো আছে
        try {
            com.example.SCM.enumClass.QuotationStatus newStatus =
                    com.example.SCM.enumClass.QuotationStatus.valueOf(status.toUpperCase());
            existingQuotation.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status provided: " + status);
        }

        // ৩. সেভ করুন
        Quotation updatedQuotation = quotationRepository.save(existingQuotation);

        // ৪. রেসপন্স রিটার্ন করুন
        return quotationMapper.toResponseDTO(updatedQuotation);
    }

    private String saveImageFile(MultipartFile file, String productName) {
        try {
            Path uploadPath = Paths.get(uploadDir, "quotation");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String cleanedName = productName.trim()
                    .replaceAll("[\\\\/:*?\"<>|]", "_")
                    .replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not store the quotation document. Error: " + e.getMessage());
        }
    }
}