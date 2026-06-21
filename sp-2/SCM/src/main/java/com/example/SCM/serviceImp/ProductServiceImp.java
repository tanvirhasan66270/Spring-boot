package com.example.SCM.serviceImp;

import com.example.SCM.dto.response.ProductResponseDTO;
import com.example.SCM.dto.mapper.ProductMapper;
import com.example.SCM.dto.request.ProductRequestDTO;
import com.example.SCM.entity.Category;
import com.example.SCM.entity.Product;
import com.example.SCM.repository.CategoryRepository;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    // application.properties থেকে আপলোড ডিরেক্টরি পাথ লোড হবে
    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    /**
     * 1. Save New Product with Multipart Image Upload & Weight Matrix
     */
    @Transactional
    @Override
    public ProductResponseDTO save(ProductRequestDTO dto, MultipartFile image) {
        if (dto == null) {
            throw new IllegalArgumentException("Product request data cannot be null");
        }

        // 🔍 প্রোডাক্ট কোড ডুপ্লিকেট কি না চেক
        if (dto.getProductCode() != null && !dto.getProductCode().trim().isEmpty()) {
            Optional<Product> existingProduct = productRepository.findByProductCode(dto.getProductCode());
            if (existingProduct.isPresent()) {
                throw new RuntimeException("Product code '" + dto.getProductCode() + "' already exists!");
            }
        }

        // 📂 ডাটাবেজ থেকে রিলেশনাল ক্যাটাগরি খুঁজে বের করা
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + dto.getCategoryId()));

        // 🖼️ ইমেজ ফাইল হ্যান্ডেল করা (যদি ফ্রন্টএন্ড থেকে ফাইল পাঠানো হয়)
        if (image != null && !image.isEmpty()) {
            String uploadedFileName = uploadProductImage(image, dto.getName());
            dto.setImage(uploadedFileName); // জেনারেটেড ফাইল নেম বা পাথটি ডিটিও-তে ইনজেক্ট করা হলো
        }

        // 🔄 Mapper দিয়ে Entity-তে রূপান্তর এবং ডাটাবেজে সেভ (weight সহ)
        Product product = productMapper.toEntity(dto, category);
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponseDTO(savedProduct);
    }

    /**
     * 2. Update Existing Product with Optional Multipart Image Update
     */
    @Transactional
    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto, MultipartFile image) {
        if (dto == null) {
            throw new IllegalArgumentException("Product request data cannot be null");
        }

        // 🔍 এক্সিস্টিং প্রোডাক্ট খুঁজে বের করা
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // 🔒 প্রোডাক্ট কোড ইউনিক কি না ভেরিফাই করা (কোড চেইঞ্জ করা হলে)
        if (dto.getProductCode() != null && !dto.getProductCode().equals(product.getProductCode())) {
            Optional<Product> duplicateCheck = productRepository.findByProductCode(dto.getProductCode());
            if (duplicateCheck.isPresent()) {
                throw new RuntimeException("Product code '" + dto.getProductCode() + "' is already taken!");
            }
        }

        // 📂 ক্যাটাগরি পরিবর্তন করা হয়ে থাকলে নতুন ক্যাটাগরি লোড করা
        Category category = product.getCategory();
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(category.getId())) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("New Category not found with ID: " + dto.getCategoryId()));
        }

        // 🔄 নতুন ইমেজ আপলোড দিলে আগের ইমেজ ওভাররাইট/নতুন পাথ ডিটিও-তে সেট করা
        if (image != null && !image.isEmpty()) {
            String newImageName = uploadProductImage(image, dto.getName());
            dto.setImage(newImageName);
        } else {
            // যদি নতুন কোনো ছবি আপলোড না করা হয়, তবে ডাটাবেজে আগের যে ছবি ছিল সেটাই বহাল থাকবে
            dto.setImage(product.getImage());
        }

        // 🛠️ ম্যাপারের মাধ্যমে অবজেক্ট ডেটা আপডেট করা (weight অটো সিঙ্ক হবে)
        productMapper.updateEntity(dto, product, category);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(updatedProduct);
    }

    /**
     * 3. Find All Products (Optimized Read)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 4. Find Product By ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponseDTO);
    }

    /**
     * 5. Delete Product Node From Matrix
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        productRepository.delete(product);
    }

    /**
     * 🖼️ লোকাল প্রজেক্ট ডিরেক্টরিতে ইমেজ সেভ করার অপ্টিমাইজড হেল্পার মেথড
     */
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

            // ফাইলের নাম স্যানিটাইজ করা এবং UUID টোকেন যুক্ত করা (ফাইলনেম কনф্লিক্ট এড়াতে)
            String cleanedName = (productName != null ? productName : "product").trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            // 💡 ফাইলটি ডিরেক্টরিতে কপি করা (ওভাররাইট সেফটি মেকানিজম সহ)
            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Product profile image upload failed: " + e.getMessage());
        }
    }
}