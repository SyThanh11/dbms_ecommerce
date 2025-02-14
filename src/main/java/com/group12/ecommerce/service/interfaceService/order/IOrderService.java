package com.group12.ecommerce.service.interfaceService.order;

import com.group12.ecommerce.dto.request.order.OrderCreationRequest;
import com.group12.ecommerce.dto.request.order.OrderUpdateRequest;
import com.group12.ecommerce.dto.response.order.OrderResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    CustomPageResponse<OrderResponse> getAllOrdersWithPage(Pageable pageable);
    OrderResponse createOrder(OrderCreationRequest request);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(String id);
    OrderResponse updateOrder(String id, OrderUpdateRequest request);
    void deleteOrder(String id);
}
