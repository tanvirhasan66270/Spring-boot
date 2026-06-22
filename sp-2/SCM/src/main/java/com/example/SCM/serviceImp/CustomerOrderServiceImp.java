package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.CustomerOrderMapper;
import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.enumClass.ServiceType;
import com.example.SCM.repository.*;
import com.example.SCM.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerOrderServiceImp implements CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderMapper orderMapper;
    private final MailService mailService;

    /**
     * 🛒 1. Save / Place a New Multi-Item Customer Order
     */
    @Override
    @Transactional
    public CustomerOrderResponseDTO save(CustomerOrderRequestDTO dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order request matrix or cart items cannot be empty");
        }

        // কাস্টমার প্রোফাইল নোড ভেরিফাই করা (সরাসরি মেইন ইউজার টেবিল থেকে)
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Target customer node missing mapping profile"));

        // ম্যাপার দিয়ে মাস্টার ও চাইল্ড রিলেশন তৈরি
        CustomerOrder order = orderMapper.toEntity(dto, customer);

        // এনটিটির ইন্টারনাল ক্যালকুলেশন মেথড রান করা
        order.executeCalculations();

        // CascadeType.ALL এবং @PrePersist হুকের মাধ্যমে ডাটাবেজে রাইট হবে
        CustomerOrder savedOrder = orderRepository.save(order);

        // অর্ডারের নিজস্ব ক্যাশড ডাটা দিয়ে সেফ ইমেইল ডিসপ্যাচ করা হলো
        sendOrderConfirmationEmail(savedOrder);

        return orderMapper.toResponseDTO(savedOrder);
    }

    /**
     * 🔄 2. Update Existing Order Metadata & Recalculate Items
     */
    @Override
    @Transactional
    public CustomerOrderResponseDTO update(Long id, CustomerOrderRequestDTO dto) {
        // ১. এক্সিস্টিং মাস্টার অর্ডার নোড লোড করা
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer order matrix row missing for ID: " + id));

        // 🔗 কাস্টমার আইডি চেঞ্জ হলে মেইন ইউজার টেবিল থেকে প্রোফাইল, নাম ও মেইল রি-সিঙ্ক গেটওয়ে
        if (dto.getCustomerId() != null && !dto.getCustomerId().equals(order.getCustomer().getId())) {
            User newCustomer = userRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("New target customer profile not found."));
            order.setCustomer(newCustomer);
            order.setCustomerName(newCustomer.getName());
            order.setCustomerEmail(newCustomer.getEmail()); // 🔥 কাস্টমার চেঞ্জ হলেও মেইল সিঙ্ক থাকবে
        }

        // ২. বেসিক মেটাডাটা আপডেট
        if (dto.getDeliveryAddress() != null) order.setDeliveryAddress(dto.getDeliveryAddress());
        if (dto.getEstimatedDelivery() != null) order.setEstimatedDelivery(LocalDate.parse(dto.getEstimatedDelivery()));
        if (dto.getServiceType() != null) order.setServiceType(ServiceType.valueOf(dto.getServiceType().toUpperCase()));
        order.setCodAmount(dto.getCodAmount());

        // ৩. চাইল্ড আইটেম আপডেট (যদি নতুন আইটেম লিস্ট পাঠানো হয়)
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            // orphanRemoval = true ডাটাবেজ থেকে পুরনো আইটেম রোগুলো মুছে দেবে
            order.getLineItems().clear();

            // নতুন আইটেমগুলো লুপ চালিয়ে পুনরায় মাস্টারে বাইন্ড করা
            dto.getItems().forEach(itemDto -> {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product master record missing for ID: " + itemDto.getProductId()));

                OrderLineItem item = new OrderLineItem();
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());

                if (itemDto.getUnitPrice() > 0) {
                    item.setUnitPrice(itemDto.getUnitPrice());
                } else {
                    item.setUnitPrice(product.getSellingPrice());
                }

                item.setLineTotal(item.getQuantity() * item.getUnitPrice());
                item.setItemWeightTotal(product.getWeight() * itemDto.getQuantity());
                item.setRemarks(itemDto.getRemarks());

                order.addLineItem(item); // হেল্পার মেথড দিয়ে রিলেশন তৈরি
            });
        }

        // ৪. ডাটাবেজে আপডেট সেভ (এখানে @PreUpdate হুক থেকে গ্র্যান্ড টোটাল রি-ক্যালকুলেট হবে)
        CustomerOrder updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    /**
     * 📋 3. Find All Customer Orders with Fetch Join Optimization
     */
    @Override
    @Transactional(readOnly = true)
    public List<CustomerOrderResponseDTO> findAll() {
        return orderRepository.findAllOrdersWithDetails().stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔍 4. Get Single Order Details By ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerOrderResponseDTO> getById(Long id) {
        return orderRepository.findByIdWithDetails(id).map(orderMapper::toResponseDTO);
    }

    /**
     * ❌ 5. Delete / Purge Order from Matrix Cache
     */
    @Override
    @Transactional
    public void delete(Long id) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target customer order row missing mapping index"));
        orderRepository.delete(order);
    }

    /**
     * 📦 6. Live Track Order Status by Unique Track Number
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerOrderResponseDTO> trackOrder(String orderNumber) {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking/Order number cannot be empty");
        }
        return orderRepository.findByOrderNumberWithDetails(orderNumber.trim())
                .map(orderMapper::toResponseDTO);
    }

    /**
     * 📧 Rich HTML Order Confirmation Invoice Email Dispatcher
     */
    private void sendOrderConfirmationEmail(CustomerOrder order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().contains("no-email")) return;

        String subject = "Your SCM Order is Confirmed! Invoice #" + order.getOrderNumber();
        StringBuilder itemRows = new StringBuilder();

        if (order.getLineItems() != null) {
            for (OrderLineItem item : order.getLineItems()) {
                itemRows.append(String.format("""
                    <tr>
                        <td><b>%s</b></td>
                        <td style='text-align: center;'>%d</td>
                        <td style='text-align: right;'>%s %.2f</td>
                    </tr>
                """, item.getProduct().getName(), item.getQuantity(), order.getCurrency(), item.getLineTotal()));
            }
        }

        String mailText = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
                .header { background-color: #2E7D32; color: white; padding: 30px; text-align: center; }
                .header h2 { margin: 0; font-size: 26px; font-weight: 600; }
                .content { padding: 30px; }
                .tracking-box { background-color: #E8F5E9; border-left: 5px solid #2E7D32; padding: 15px; margin: 20px 0; border-radius: 4px; }
                .tracking-code { font-family: 'Courier New', Courier, monospace; font-size: 18px; font-weight: bold; color: #1B5E20; letter-spacing: 1px; }
                .invoice-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                .invoice-table th { background-color: #f8fafc; text-align: left; padding: 10px; font-size: 13px; color: #64748b; border-bottom: 2px solid #e2e8f0; }
                .invoice-table td { padding: 12px 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
                .total-row { font-weight: bold; color: #2E7D32; font-size: 16px; background-color: #f8fafc; }
                .btn-container { text-align: center; margin: 30px 0; }
                .btn { background-color: #2E7D32; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; }
                .footer { font-size: 0.85em; color: #64748b; padding: 20px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0; }
            </style>
        </head>
        <body>
            <div class='container'>
                <div class='header'>
                    <h2>Order Confirmed Successfully</h2>
                </div>
                <div class='content'>
                    <p>Dear <b>%s</b>,</p>
                    <p>Thank you for shopping with us! Your multi-item purchase order has been successfully logged into our logistics cluster nodes.</p>
                    
                    <div class='tracking-box'>
                        <p style='margin: 0 0 5px 0; font-size: 13px;'>YOUR UNIQUE LIVE TRACKING CODE:</p>
                        <span class='tracking-code'>%s</span>
                    </div>

                    <table class='invoice-table'>
                        <thead>
                            <tr>
                                <th>Product Specifications</th>
                                <th style='text-align: center;'>Qty</th>
                                <th style='text-align: right;'>Total Bill</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                            <tr>
                                <td colspan='2' style='text-align: right; color:#64748b;'>Cart Item Subtotal:</td>
                                <td style='text-align: right;'>%s %.2f</td>
                            </tr>
                            <tr>
                                <td colspan='2' style='text-align: right; color:#64748b;'>Logistics Charge (%s):</td>
                                <td style='text-align: right;'>%s %.2f</td>
                            </tr>
                            <tr class='total-row'>
                                <td colspan='2' style='text-align: right;'>Net Aggregate Amount:</td>
                                <td style='text-align: right;'>%s %.2f</td>
                            </tr>
                        </tbody>
                    </table>

                    <p><b>Consignment Shipping Node:</b><br>%s</p>

                    <div class='btn-container'>
                        <a href='http://localhost:4200/track-order?code=%s' class='btn'>Track Package Live</a>
                    </div>
                </div>
                <div class='footer'>
                    &copy; %d SCM Global Logistics Network. All rights reserved.
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                order.getCustomerName(),
                order.getOrderNumber(),
                itemRows.toString(),
                order.getCurrency(), order.getItemSubtotal(),
                order.getServiceType().name(),
                order.getCurrency(), order.getDeliveryCharge(),
                order.getCurrency(), order.getTotalAmount(),
                order.getDeliveryAddress(),
                order.getOrderNumber(),
                java.time.Year.now().getValue()
        );

        try {
            mailService.SenderGeneralMail(order.getCustomerEmail(), subject, mailText);
        } catch (Exception e) {
            System.err.println("Invoice Notification Cluster Mail delivery failed: " + e.getMessage());
        }
    }
}