package com.group12.ecommerce.controller.order;

import com.group12.ecommerce.dto.request.order.OrderCreationRequest;
import com.group12.ecommerce.dto.request.order.OrderUpdateRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.order.OrderResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.entity.order.OrderStatus;
import com.group12.ecommerce.service.interfaceService.order.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Order Management")
public class OrderController {

    @Autowired
    IOrderService orderService;

    @Operation(summary = "Create new order", description = "Api for create order")
    @PostMapping
    ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderCreationRequest request){
        OrderResponse orderResponse = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OrderResponse>builder()
                        .message("Create order success!")
                        .data(orderResponse)
                        .build()
        );
    }

    @Operation(summary = "Select payment method", description = "API for selecting payment method for an order")
    @PostMapping("/{orderId}/select-payment")
    ResponseEntity<ApiResponse<OrderResponse>> selectPaymentMethod(
            @PathVariable String orderId,
            @RequestParam String paymentMethod) {

        OrderResponse orderResponse = orderService.selectPaymentMethod(orderId, paymentMethod);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<OrderResponse>builder()
                        .message("Payment method selected successfully!")
                        .data(orderResponse)
                        .build()
        );
    }

    @Operation(summary = "Confirm payment", description = "API for confirming payment for an order")
    @PostMapping("/{orderId}/confirm-payment")
    ResponseEntity<ApiResponse<OrderResponse>> confirmPayment(
            @PathVariable String orderId,
            @RequestParam boolean isSuccess) {

        OrderResponse orderResponse = orderService.confirmPayment(orderId, isSuccess);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<OrderResponse>builder()
                        .message(isSuccess ? "Payment confirmed successfully!" : "Payment failed, order canceled.")
                        .data(orderResponse)
                        .build()
        );
    }


    @Operation(summary = "Get order by id", description = "Api for get order by id")
    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<OrderResponse>builder()
                        .message("Get order success!")
                        .data(
                                orderService.getOrderById(id)
                        )
                        .build()
        );
    }

    @Operation(summary = "Get all orders", description = "Api for get all orders")
    @GetMapping
    ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<OrderResponse>>builder()
                        .message("Get order success!")
                        .data(
                                orderService.getAllOrders()
                        )
                        .build()
        );
    }

    @Operation(summary = "Update order", description = "Api for update order by id")
    @PutMapping
    ResponseEntity<ApiResponse<OrderResponse>> updateOrder(@RequestParam String id,
                                                         @RequestBody OrderUpdateRequest request){
        OrderResponse orderResponse = orderService.updateOrder(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<OrderResponse>builder()
                        .message("Update order success")
                        .data(orderResponse)
                        .build()
        );
    }

    @Operation(summary = "Delete order", description = "Api for delete order by id")
    @DeleteMapping
    ResponseEntity<ApiResponse<?>> deleteOrder(@RequestParam String id){
        orderService.deleteOrder(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Delete order success!")
                        .build()
        );
    }

    @Operation(summary = "Get all orders with paging", description = "API for getting all orders with paging")
    @GetMapping("/get-page")
    ResponseEntity<ApiResponse<CustomPageResponse<OrderResponse>>> getAllOrdersWithPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable;

        if (sort.isEmpty()) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sortBy = Sort.by(sortDirection, sort);
            pageable = PageRequest.of(page, size, sortBy);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CustomPageResponse<OrderResponse>>builder()
                        .message("Get all users success!")
                        .data(orderService.getAllOrdersWithPage(pageable))
                        .build()
        );
    }

    @Operation(summary = "Get user order history", description = "API for retrieving user's order history")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<CustomPageResponse<OrderResponse>>> getUserOrderHistory(
            @RequestParam String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable;

        if (sort.isEmpty()) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction sortDirection =
                    direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sortBy = Sort.by(sortDirection, sort);
            pageable = PageRequest.of(page, size, sortBy);
        }

        OrderStatus orderStatus = null;
        if(status != null && !status.isEmpty()){
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.<CustomPageResponse<OrderResponse>>builder()
                                .message("Invalid status value: " + status)
                                .build()
                );
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CustomPageResponse<OrderResponse>>builder()
                        .message("Get user order history success!")
                        .data(orderService.getUserOrderHistory(userId, orderStatus, pageable))
                        .build()
        );
    }
}
