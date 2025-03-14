package com.group12.ecommerce.service.interfaceService.order;

import com.group12.ecommerce.dto.request.order.OrderCreationRequest;
import com.group12.ecommerce.dto.request.order.OrderUpdateRequest;
import com.group12.ecommerce.dto.response.order.OrderResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.entity.order.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    CustomPageResponse<OrderResponse> getAllOrdersWithPage(Pageable pageable);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(String id);
    OrderResponse updateOrder(String id, OrderUpdateRequest request);
    void deleteOrder(String id);

    OrderResponse createOrder(OrderCreationRequest request);
    String selectPaymentMethod(String orderId, String orderInfo, String paymentMethod, String urlReturn, HttpServletRequest request);
    OrderResponse confirmPayment(String orderId, boolean isSuccess);

    // history
    CustomPageResponse<OrderResponse> getUserOrderHistory(String userId, OrderStatus status, Pageable pageable);
}
