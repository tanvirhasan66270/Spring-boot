package com.example.SCM.dto.mapper;

import com.example.SCM.dto.response.ProductResponseDTO;
import com.example.SCM.dto.request.ProductRequestDTO;

import com.example.SCM.entity.Category;
import com.example.SCM.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    // ProductRequestDTO এবং অ্যাসোসিয়েটেড Category থেকে Product Entity-তে রূপান্তর (Create Operation)

    public Product toEntity(ProductRequestDTO dto, Category category) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setProductCode(dto.getProductCode());
        product.setName(dto.getName());
        product.setUnit(dto.getUnit());
        product.setReorderPoint(dto.getReorderPoint());
        product.setUnitCost(dto.getUnitCost());
        product.setQuantity(dto.getQuantity());
        product.setSellingPrice(dto.getSellingPrice());
        product.setHasExpiryDate(dto.getHasExpiryDate());
        product.setAvailability(dto.getAvailability());
        product.setImage(dto.getImage()); // Base64 String সরাসরি সেট হবে

        // রিলেশনাল ক্যাটাগরি অবজেক্ট ইনজেক্ট করা
        product.setCategory(category);
        product.setActive(true); // নতুন প্রোডাক্ট বাই-ডিফল্ট একটিভ থাকবে

        return product;
    }

    // Product Entity থেকে ProductResponseDTO-তে রূপান্তর (Read Operations / Flattening Relation)

    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setName(product.getName());
        dto.setUnit(product.getUnit());
        dto.setReorderPoint(product.getReorderPoint());
        dto.setUnitCost(product.getUnitCost());
        dto.setQuantity(product.getQuantity());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setHasExpiryDate(product.getHasExpiryDate());
        dto.setActive(product.isActive());
        dto.setAvailability(product.getAvailability());
        dto.setImage(product.getImage()); // Base64 String ফ্রন্টএন্ডে ব্যাক যাবে

        // ক্যাটাগরি অবজেক্ট থেকে আইডি এবং নাম ফ্ল্যাট করে DTO-তে সেট করা
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getCategoryName());
        }

        return dto;
    }

    // এক্সিস্টিং Product Entity-কে Request DTO এবং নতুন Category দিয়ে আপডেট করা (Update Operation)

    public void updateEntity(ProductRequestDTO dto, Product product, Category category) {
        if (dto == null || product == null) {
            return;
        }

        if (dto.getProductCode() != null) product.setProductCode(dto.getProductCode());
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getUnit() != null) product.setUnit(dto.getUnit());

        product.setReorderPoint(dto.getReorderPoint());
        product.setUnitCost(dto.getUnitCost());
        product.setQuantity(dto.getQuantity());
        product.setSellingPrice(dto.getSellingPrice());

        if (dto.getHasExpiryDate() != null) product.setHasExpiryDate(dto.getHasExpiryDate());
        if (dto.getAvailability() != null) product.setAvailability(dto.getAvailability());

        // নতুন ছবি পাঠালে সেটি আপডেট হবে, না পাঠালে আগের ছবিই বহাল থাকবে
        if (dto.getImage() != null && !dto.getImage().trim().isEmpty()) {
            product.setImage(dto.getImage());
        }

        // ক্যাটাগরি চেঞ্জ হয়ে থাকলে নতুন ক্যাটাগরি সেট করা
        if (category != null) {
            product.setCategory(category);
        }
    }
}