package com.example.SCM.serviceImp;

import com.example.SCM.dto.response.ProductResponseDTO;
import com.example.SCM.dto.mapper.ProductMapper;
import com.example.SCM.dto.request.ProductRequestDTO;
import com.example.SCM.entity.Category;
import com.example.SCM.entity.Product;
import com.example.SCM.enumClass.ActionStatus;
import com.example.SCM.repository.CategoryRepository;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.service.ActivityLogService;
import com.example.SCM.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ActivityLogService activityLogService;
    private final HttpServletRequest request;


    private String resolveCurrentUserId() {
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isBlank()) {
            return userId;                          // ← still returns here first
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return "UNKNOWN_USER";
    }

    // application.properties থেকে আপলোড ডিরেক্টরি পাথ লোড হবে
    @Value("${image.upload.dir}")
    private String uploadDir;


    @Transactional
    @Override
    public ProductResponseDTO save(ProductRequestDTO dto, MultipartFile image) {
        if (dto == null) {
            throw new IllegalArgumentException("Product request data cannot be null");
        }

        // প্রোডাক্ট কোড ডুপ্লিকেট কি না চেক
        if (dto.getProductCode() != null && !dto.getProductCode().trim().isEmpty()) {
            Optional<Product> existingProduct = productRepository.findByProductCode(dto.getProductCode());
            if (existingProduct.isPresent()) {
                throw new RuntimeException("Product code '" + dto.getProductCode() + "' already exists!");
            }
        }

        // ডাটাবেজ থেকে রিলেশনাল ক্যাটাগরি খুঁজে বের করা
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        // ইমেজ ফাইল হ্যান্ডেল করা (যদি ফ্রন্টএন্ড থেকে ফাইল পাঠানো হয়)
        if (image != null && !image.isEmpty()) {
            String uploadedFileName = uploadProductImage(image, dto.getName());
            dto.setImage(uploadedFileName); // জেনারেটেড ফাইল নেম বা পাথটি ডিটিও-তে ইনজেক্ট করা হলো
        }

        //Mapper দিয়ে Entity-তে রূপান্তর এবং ডাটাবেজে সেভ (weight সহ)
        Product product = productMapper.toEntity(dto, category);
        Product savedProduct = productRepository.save(product);

       //Activity log
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "CREATE",
                "PRODUCT",
                savedProduct.getId().toString(),
                "New Product successfully created. Code: "
                        + savedProduct.getProductCode() + ", Name: " + savedProduct.getName(),
                null,
                "{\"productCode\":\"" + savedProduct.getProductCode() + "\", \"name\":\"" + savedProduct.getName() + "\"}",
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        return productMapper.convertTOResponseDTO(savedProduct);
    }


    @Transactional
    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto, MultipartFile image) {
        if (dto == null) {
            throw new IllegalArgumentException("Product request data cannot be null");
        }

        //এক্সিস্টিং প্রোডাক্ট খুঁজে বের করা
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        //ওল্ড ভ্যালু ট্র্যাকিং (for log)
        String oldName = product.getName();
        String oldCode = product.getProductCode();
        Long oldCategoryId = product.getCategory() != null ? product.getCategory().getId() : null;

        //প্রোডাক্ট কোড ইউনিক কি না ভেরিফাই করা (কোড চেইঞ্জ করা হলে)
        if (dto.getProductCode() != null && !dto.getProductCode().equals(product.getProductCode())) {
            Optional<Product> duplicateCheck = productRepository.findByProductCode(dto.getProductCode());
            if (duplicateCheck.isPresent()) {
                throw new RuntimeException("Product code '" + dto.getProductCode() + "' is already taken!");
            }
        }

        //ক্যাটাগরি পরিবর্তন করা হয়ে থাকলে নতুন ক্যাটাগরি লোড করা
        Category category = product.getCategory();
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(category.getId())) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("New Category not found with ID: " + dto.getCategoryId()));
        }

        //নতুন ইমেজ আপলোড দিলে আগের ইমেজ ওভাররাইট/নতুন পাথ ডিটিও-তে সেট করা
        if (image != null && !image.isEmpty()) {
            String newImageName = uploadProductImage(image, dto.getName());
            dto.setImage(newImageName);
        } else {
            // যদি নতুন কোনো ছবি আপলোড না করা হয়, তবে ডাটাবেজে আগের যে ছবি ছিল সেটাই বহাল থাকবে
            dto.setImage(product.getImage());
        }

        // ্যাপারের মাধ্যমে অবজেক্ট ডেটা আপডেট করা (weight অটো সিঙ্ক হবে)
        productMapper.updateEntity(dto, product, category);
        Product updatedProduct = productRepository.save(product);

        //Activity log
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "UPDATE",
                "PRODUCT",
                updatedProduct.getId().toString(),
                "Product metadata updated for Code: " + updatedProduct.getProductCode(),
                "{\"name\":\"" + oldName + "\", \"code\":\"" + oldCode + "\", \"categoryId\":" + oldCategoryId + "}",
                "{\"name\":\"" + updatedProduct.getName() + "\", \"code\":\"" + updatedProduct.getProductCode() + "\", \"categoryId\":" + updatedProduct.getCategory().getId() + "}",
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        return productMapper.convertTOResponseDTO(updatedProduct);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::convertTOResponseDTO);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        String deletedProductCode = product.getProductCode();
        String deletedProductName = product.getName();

        productRepository.delete(product);

        //Activity log
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "DELETE",
                "PRODUCT",
                id.toString(),
                "Product permanently deleted from inventory. Code was: " + deletedProductCode + ", Name: " + deletedProductName,
                "{\"productCode\":\"" + deletedProductCode + "\", \"name\":\"" + deletedProductName + "\"}",
                null,
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );
    }


    private String uploadProductImage(MultipartFile file, String productName) {
        try {
            // uploads/product ফোল্ডার পাথ তৈরি করা
            Path path = Paths.get(uploadDir, "product");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // ফাইলের এক্সটেনশন (.png, .jpg ইত্যাদি) এক্সট্রাক্ট করা
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // ফাইলের নাম স্যানিটাইজ করা এবং UUID টোকেন যুক্ত করা (ফাইলনেম কনফ্লিক্ট এড়াতে)
            String cleanedName = (productName != null ? productName : "product").trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            // ফাইলটি ডিরেক্টরিতে কপি করা (with website sefty macanigom)
            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Product profile image upload failed: " + e.getMessage());
        }
    }
}