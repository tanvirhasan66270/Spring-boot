package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.OrderLineItemRequestDTO;
import com.example.SCM.dto.response.OrderLineItemResponseDTO;
import com.example.SCM.entity.OrderLineItem;
import com.example.SCM.entity.Product;
import com.example.SCM.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderLineItemMapper {

    private final ProductRepository productRepository;

   //  Request DTO থেকে Entity-তে কনভার্ট (আইটেম লেভেল বিল্ডার)

    public OrderLineItem toEntity(OrderLineItemRequestDTO dto) {

        OrderLineItem item = new OrderLineItem();
        item.setQuantity(dto.getQuantity());
        item.setRemarks(dto.getRemarks());


        // প্রোডাক্ট নোড অ্যাসোসিয়েশন এবং প্রাইস প্রোটেকশন গেটওয়ে
        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product instance not found for ID: " + dto.getProductId()));
            item.setProduct(product);
            item.setUnitPrice(product.getSellingPrice()); 

//            // ফ্রন্টএন্ড থেকে unitPrice না আসলে প্রোডাক্ট মাস্টার টেবিল থেকে প্রাইস লক হবে
//            if (dto.getUnitPrice() > 0) {
//                item.setUnitPrice(dto.getUnitPrice());
//            } else {
//                item.setUnitPrice(product.getSellingPrice());
//            }

            // সাব-টোটাল এবং ওজনের ক্যালকুলেশন সিঙ্ক
            item.setLineTotal(item.getQuantity() * item.getUnitPrice());
            item.setItemWeightTotal(product.getWeight() * item.getQuantity());
        }

        return item;
    }

   //  Entity থেকে Response DTO-তে কনভার্ট (ক্লিন জেসন আউটপুট জেনারেটর)

    public OrderLineItemResponseDTO convertTOResponseDTO(OrderLineItem entity) {

        OrderLineItemResponseDTO dto = new OrderLineItemResponseDTO();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setLineTotal(entity.getLineTotal());
        dto.setItemWeightTotal(entity.getItemWeightTotal());
        dto.setRemarks(entity.getRemarks());

        // সেফটি গেটওয়ে: অবজেক্ট গ্রাফ থেকে প্রোডাক্ট মেটাডেটা এক্সট্র্যাক্ট করা
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
            dto.setProductName(entity.getProduct().getName());
            dto.setProductCode(entity.getProduct().getProductCode());
        }

        return dto;
    }
}