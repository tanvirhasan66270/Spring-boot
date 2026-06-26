package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.CustomerOrderMapper;
import com.example.SCM.dto.mapper.OrderLineItemMapper;
import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.enumClass.ActionStatus;
import com.example.SCM.enumClass.ServiceType;
import com.example.SCM.repository.*;
import com.example.SCM.service.CustomerOrderService;
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
public class CustomerOrderServiceImp implements CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CustomerOrderMapper orderMapper;
    private final OrderLineItemMapper lineItemMapper;
    private final MailService mailService;
    private final ActivityLogService activityLogService; // 1. ActivityLogService ইনজেক্ট করা হয়েছে
    private final HttpServletRequest request;            // 2. HttpServletRequest ইনজেক্ট করা হয়েছে

    private String resolveCurrentUserId() {
        String userId = request.getHeader("X-User-Id");
        return (userId != null && !userId.isBlank()) ? userId : "16";
    }

    /**
     * 🛒 1. Save / Place a New Multi-Item Customer Order
     */
    @Transactional
    @Override
    public CustomerOrderResponseDTO save(CustomerOrderRequestDTO dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order request matrix or cart items cannot be empty");
        }

        User customer = userRepository.findCustomerById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Target profile missing or the user is not a valid CUSTOMER! ID: " + dto.getCustomerId()));

        CustomerOrder order = orderMapper.toEntity(dto, customer);
        order.executeCalculations();

        CustomerOrder savedOrder = orderRepository.save(order);
        sendOrderConfirmationEmail(savedOrder);

        // ✅ অ্যাক্টিভিটি লগ (CREATE ORDER)
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "CREATE",
                "CUSTOMER_ORDER",
                savedOrder.getId().toString(),
                "New multi-item customer order placed. Order Number: " + savedOrder.getOrderNumber() + ", Total Amount: " + savedOrder.getTotalAmount() + " " + savedOrder.getCurrency(),
                null,
                "{\"orderNumber\":\"" + savedOrder.getOrderNumber() + "\", \"totalAmount\":" + savedOrder.getTotalAmount() + "}",
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        return orderMapper.toResponseDTO(savedOrder);
    }

    /**
     * 🔄 2. Update Existing Order Metadata & Recalculate Items
     */
    @Transactional
    @Override
    public CustomerOrderResponseDTO update(Long id, CustomerOrderRequestDTO dto) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer order matrix row missing for ID: " + id));

        // 🔒 ওল্ড ভ্যালু ট্র্যাকিং (লগের জন্য)
        Long oldCustomerId = order.getCustomer() != null ? order.getCustomer().getId() : null;
        String oldDeliveryAddress = order.getDeliveryAddress();
        double oldTotalAmount = order.getTotalAmount();

        if (dto.getCustomerId() != null && !dto.getCustomerId().equals(order.getCustomer().getId())) {
            User newCustomer = userRepository.findCustomerById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("New target profile missing or not a valid CUSTOMER! ID: " + dto.getCustomerId()));
            order.setCustomer(newCustomer);
            order.setCustomerName(newCustomer.getName());
            order.setCustomerEmail(newCustomer.getEmail());
        }

        if (dto.getDeliveryAddress() != null) order.setDeliveryAddress(dto.getDeliveryAddress());
        if (dto.getEstimatedDelivery() != null) order.setEstimatedDelivery(LocalDate.parse(dto.getEstimatedDelivery()));
        if (dto.getServiceType() != null) order.setServiceType(ServiceType.valueOf(dto.getServiceType().toUpperCase()));
        order.setCodAmount(dto.getCodAmount());

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            order.getLineItems().clear();

            dto.getItems().forEach(itemDto -> {
                OrderLineItem item = lineItemMapper.toEntity(itemDto);
                if (item != null) {
                    order.addLineItem(item);
                }
            });
            // আইটেম রিক্যালকুলেশন মেথড কল করা হতে পারে যদি এনটিটিতে থাকে
            order.executeCalculations();
        }

        CustomerOrder updatedOrder = orderRepository.save(order);

        // ✅ অ্যাক্টিভিটি লগ (UPDATE ORDER)
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "UPDATE",
                "CUSTOMER_ORDER",
                updatedOrder.getId().toString(),
                "Customer order metadata and items updated. Order Number: " + updatedOrder.getOrderNumber(),
                "{\"customerId\":" + oldCustomerId + ", \"address\":\"" + oldDeliveryAddress + "\", \"total\":" + oldTotalAmount + "}",
                "{\"customerId\":" + updatedOrder.getCustomer().getId() + ", \"address\":\"" + updatedOrder.getDeliveryAddress() + "\", \"total\":" + updatedOrder.getTotalAmount() + "}",
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );

        return orderMapper.toResponseDTO(updatedOrder);
    }

    /**
     * 📋 3. Find All Customer Orders with Fetch Join Optimization
     */
    @Transactional(readOnly = true)
    @Override
    public List<CustomerOrderResponseDTO> findAll() {
        return orderRepository.findAllOrdersWithDetails()
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔍 4. Get Single Order Details By ID
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<CustomerOrderResponseDTO> getById(Long id) {
        return orderRepository.findByIdWithDetails(id)
                .map(orderMapper::toResponseDTO);
    }

    /**
     * ❌ 5. Delete / Purge Order from Matrix Cache
     */
    @Transactional
    @Override
    public void delete(Long id) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target customer order row missing mapping index"));

        String deletedOrderNumber = order.getOrderNumber();
        double deletedTotalAmount = order.getTotalAmount();

        orderRepository.delete(order);

        // ✅ অ্যাক্টিভিটি লগ (DELETE ORDER)
        activityLogService.log(
                resolveCurrentUserId(),
                null,
                "DELETE",
                "CUSTOMER_ORDER",
                id.toString(),
                "Customer order permanently purged from logistics cluster. Order Number was: " + deletedOrderNumber,
                "{\"orderNumber\":\"" + deletedOrderNumber + "\", \"totalAmount\":" + deletedTotalAmount + "}",
                null,
                ActionStatus.SUCCESS,
                request.getRemoteAddr()
        );
    }

    /**
     * 📦 6. Live Track Order Status by Unique Track Number
     */
    @Transactional(readOnly = true)
    @Override
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