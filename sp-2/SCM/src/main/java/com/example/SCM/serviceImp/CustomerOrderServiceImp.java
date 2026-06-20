package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.CustomerOrderMapper;
import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.repository.*;
import com.example.SCM.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerOrderServiceImp implements CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository; // ধরে নেওয়া হচ্ছে আপনার UserRepository আছে
    private final ProductRepository productRepository; // ধরে নেওয়া হচ্ছে আপনার ProductRepository আছে
    private final CustomerOrderMapper orderMapper;
    private final MailService mailService;

    @Override
    @Transactional
    public CustomerOrderResponseDTO save(CustomerOrderRequestDTO dto) {
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Target customer node missing mapping profile"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product master record missing"));

        CustomerOrder order = orderMapper.toEntity(dto, customer, product);
        // Save করার সাথে সাথে @PrePersist হুক স্বয়ংক্রিয়ভাবে এক্সিকিউট হবে এবং চার্জ ক্যালকুলেট করবে
        CustomerOrder savedOrder = orderRepository.save(order);

        sendOrderConfirmationEmail(customer, savedOrder);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    @Transactional
    public CustomerOrderResponseDTO update(Long id, CustomerOrderRequestDTO dto) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer order tracking map record index missing"));

        User customer = userRepository.findById(dto.getCustomerId()).orElse(order.getCustomer());
        Product product = productRepository.findById(dto.getProductId()).orElse(order.getProduct());

        orderMapper.updateEntity(dto, order, customer, product);
        // Save করার সাথে সাথে @PreUpdate হুক স্বয়ংক্রিয়ভাবে এক্সিকিউট হবে এবং নতুন করে হিসাব রি-ক্যালকুলেট করবে
        return orderMapper.toResponseDTO(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerOrderResponseDTO> findAll() {
        return orderRepository.findAllOrdersWithDetails().stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerOrderResponseDTO> getById(Long id) {
        return orderRepository.findByIdWithDetails(id).map(orderMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target order row index missing"));
        order.setStatus(status.toUpperCase());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order map footprint index clean error");
        }
        orderRepository.deleteById(id);
    }

    /**
     * 📧 কাস্টমারের জন্য কাস্টমাইজড প্রফেশনাল Order Confirmation & Tracking HTML ইমেইল টেমপ্লেট
     */
    private void sendOrderConfirmationEmail(User customer, CustomerOrder order) {
        if (customer == null || customer.getEmail() == null) return;

        String subject = "Your SCM Order is Confirmed! Invoice #" + order.getOrderNumber();
        String mailText = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
                .header { background-color: #2E7D32; color: white; padding: 30px; text-align: center; }
                .header h2 { margin: 0; font-size: 26px; font-weight: 600; }
                .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 14px; }
                .content { padding: 30px; }
                .tracking-box { background-color: #E8F5E9; border-left: 5px solid #2E7D32; padding: 15px; margin: 20px 0; border-radius: 4px; }
                .tracking-code { font-family: 'Courier New', Courier, monospace; font-size: 18px; font-weight: bold; color: #1B5E20; letter-spacing: 1px; }
                .invoice-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                .invoice-table th { background-color: #f8fafc; text-align: left; padding: 10px; font-size: 13px; color: #64748b; border-bottom: 2px solid #e2e8f0; }
                .invoice-table td { padding: 12px 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
                .total-row { font-weight: bold; color: #2E7D32; font-size: 16px; background-color: #f8fafc; }
                .btn-container { text-align: center; margin: 30px 0; }
                .btn { background-color: #2E7D32; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                .footer { font-size: 0.85em; color: #64748b; padding: 20px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0; }
            </style>
        </head>
        <body>
            <div class='container'>
                <div class='header'>
                    <h2>Order Confirmed Successfully</h2>
                    <p>Thank you for choosing SCM Enterprise Cluster</p>
                </div>
                <div class='content'>
                    <p>Dear <b>%s</b>,</p>
                    <p>We are excited to inform you that your purchase order has been logged into our matrix and is currently being processed by our logistics unit.</p>
                    
                    <div class='tracking-box'>
                        <p style='margin: 0 0 5px 0; font-size: 13px; color: #475569;'>YOUR UNIQUE LIVE TRACKING CODE:</p>
                        <span class='tracking-code'>%s</span>
                        <p style='margin: 5px 0 0 0; font-size: 12px; color: #64748b;'>Use this code in your customer portal console to map your dispatch lifecycle.</p>
                    </div>

                    <p><b>Order Invoice Specification Matrix:</b></p>
                    <table class='invoice-table'>
                        <thead>
                            <tr>
                                <th>Product Specifications</th>
                                <th style='text-align: center;'>Qty</th>
                                <th style='text-align: right;'>Total Bill</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><b>%s</b><br><span style='font-size:12px; color:#64748b;'>Rate: %s %.2f / Service: %s</span></td>
                                <td style='text-align: center;'>%d</td>
                                <td style='text-align: right;'>%s %.2f</td>
                            </tr>
                            <tr>
                                <td colspan='2' style='text-align: right; color:#64748b;'>Line Item Total:</td>
                                <td style='text-align: right;'>%s %.2f</td>
                            </tr>
                            <tr>
                                <td colspan='2' style='text-align: right; color:#64748b;'>SCM Delivery Charge (%s):</td>
                                <td style='text-align: right;'>%s %.2f</td>
                            </tr>
                            <tr class='total-row'>
                                <td colspan='2' style='text-align: right;'>Net Aggregate Amount:</td>
                                <td style='text-align: right;'>%s %.2f</td>
                            </tr>
                        </tbody>
                    </table>

                    <p><b>Consignment Shipping Node:</b><br>
                    <span style='color:#475569;'>%s</span></p>

                    <div class='btn-container'>
                        <a href='http://localhost:4200/track-order?code=%s' class='btn'>Track Your Package Live</a>
                    </div>

                    <p>If you have any queries regarding this manifest deployment, please contact our network operations desk.</p>
                    <p>Best regards,<br><b>SCM Enterprise Logistics Hub</b></p>
                </div>
                <div class='footer'>
                    &copy; %d SCM Global Logistics Network. All rights reserved.
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                customer.getName(),                         // %s -> কাস্টমারের নাম
                order.getOrderNumber(),                     // %s -> ইউনিক ট্র্যাকিং কোড (ORD-xxxx)
                order.getProduct().getName(),               // %s -> প্রোডাক্টের নাম
                order.getCurrency(), order.getUnitPrice(),  // %s %.2f -> কারেন্সি এবং একক মূল্য
                order.getServiceType().name(),              // %s -> সার্ভিসের ধরন (যেমন: EXPRESS)
                order.getQuantity(),                        // %d -> অর্ডারের পরিমাণ
                order.getCurrency(), order.getLineTotal(),   // %s %.2f -> লাইন টেন টোটাল
                order.getCurrency(), order.getLineTotal(),   // %s %.2f -> সাবটোটাল
                order.getServiceType().name(),              // %s -> ডেলিভারি টাইপ নাম
                order.getCurrency(), order.getDeliveryCharge(), // %s %.2f -> ডেলিভারি চার্জ
                order.getCurrency(), order.getTotalAmount(), // %s %.2f -> সর্বমোট বিল
                order.getDeliveryAddress(),                 // %s -> ডেলিভারি ঠিকানা
                order.getOrderNumber(),                     // %s -> এঙ্গুলার ট্র্যাকিং লিংকের প্যারামিটার কোড
                java.time.Year.now().getValue()             // %d -> কারেন্ট ইয়ার ডাইনামিক ফুটার
        );

        try {
            mailService.SenderGeneralMail(customer.getEmail(), subject, mailText);
            System.out.println("Customer Invoice Email successfully dispatched to node: " + customer.getEmail());
        } catch (Exception e) {
            System.err.println("Customer Invoice Email failed to execute: " + e.getMessage());
        }
    }
}
