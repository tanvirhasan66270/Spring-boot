package com.example.SCM.serviceImp;

import com.example.SCM.dto.request.QuotationRequestDTO;
import com.example.SCM.dto.response.QuotationResponseDTO;
import com.example.SCM.entity.Quotation;
import com.example.SCM.dto.mapper.QuotationMapper;
import com.example.SCM.repository.QuotationRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class QuotationServiceImp implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationMapper quotationMapper;

    @Value("${image.upload.dir}")
    private String uploadDir;


    @Override
    @Transactional
    public QuotationResponseDTO save(QuotationRequestDTO dto, MultipartFile image) {
        //  DTO থেকে Entity-তে কনভার্ট করা
        Quotation quotation = quotationMapper.toEntity(dto);

        //  ইমেজ বা ফাইল হ্যান্ডলিং
        if (image != null && !image.isEmpty()) {
            String fileUrl = saveImageFile(image,dto.getProductName());
            quotation.setAttachmentUrl(fileUrl); // attachmentUrl ফিল্ডে ফাইলের পাথ সেট করা হচ্ছে
        }

        //  ডেটাবেসে সেভ করা
        Quotation savedQuotation = quotationRepository.save(quotation);

        //  Response DTO-তে কনভার্ট করে রিটার্ন করা
        return quotationMapper.toResponseDTO(savedQuotation);
    }

    @Override
    @Transactional
    public QuotationResponseDTO update(Long id, QuotationRequestDTO dto) {
        //  আইডি দিয়ে এক্সিসটিং কোটেশন খুঁজে বের করা
        Quotation existingQuotation = quotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quotation not found with id: " + id));

        //  ম্যাপার ব্যবহার করে পুরাতন ডাটার উপর নতুন ডাটা আপডেট করা
        quotationMapper.updateEntityFromDTO(dto, existingQuotation);

        // আপডেট হওয়া ডাটা সেভ করা
        Quotation updatedQuotation = quotationRepository.save(existingQuotation);

        return quotationMapper.toResponseDTO(updatedQuotation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuotationResponseDTO> findAll() {
        return quotationRepository.findAll()
                .stream()
                .map(quotationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuotationResponseDTO> getById(Long id) {
        return quotationRepository.findById(id)
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


    private String saveImageFile(MultipartFile file,String productName) {
        try {
            // ডিরেক্টরি না থাকলে তৈরি করবে
            Path uploadPath = Paths.get(uploadDir , "quotation");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // ফাইলের এক্সটেনশন (.png, .jpg ইত্যাদি) এক্সট্রাক্ট করা
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // ফাইলের নাম স্যানিটাইজ করা এবং UUID টোকেন যুক্ত করা (ফাইলনেম কনফ্লিক্ট এড়াতে)
            String cleanedName = productName.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            // ফাইলটি নির্দিষ্ট লোকাল ডিরেক্টরিতে সেভ করা
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));

            // ডাটাবেজে রাখার জন্য ফাইলের ইউনিক নাম বা রিলেটিভ পাথ রিটার্ন করা হলো
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}