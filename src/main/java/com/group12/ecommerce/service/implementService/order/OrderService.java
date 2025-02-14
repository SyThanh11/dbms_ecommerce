package com.group12.ecommerce.service.implementService.order;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.order.OrderCreationRequest;
import com.group12.ecommerce.dto.request.order.OrderUpdateRequest;
import com.group12.ecommerce.dto.request.order_product.OrderProductDto;
import com.group12.ecommerce.dto.response.order.OrderResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.order.OrderStatus;
import com.group12.ecommerce.entity.order_product.OrderProductEntity;
import com.group12.ecommerce.entity.payment.PaymentEntity;
import com.group12.ecommerce.entity.payment.PaymentStatus;
import com.group12.ecommerce.entity.product.ProductEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.mapper.order.IOrderMapper;
import com.group12.ecommerce.repository.order.IOrderRepository;
import com.group12.ecommerce.repository.order_product.IOrderProductRepository;
import com.group12.ecommerce.repository.payment.IPaymentRepository;
import com.group12.ecommerce.repository.product.IProductRepository;
import com.group12.ecommerce.repository.user.IUserRepository;
import com.group12.ecommerce.service.interfaceService.order.IOrderService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {

    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IOrderProductRepository orderProductRepository;
    @Autowired
    IPaymentRepository paymentRepository;

    @Autowired
    IOrderMapper orderMapper;

    @Override
    public CustomPageResponse<OrderResponse> getAllOrdersWithPage(Pageable pageable) {
        Page<OrderEntity> orderEntities = orderRepository.findAll(pageable);
        Page<OrderResponse> orderResponses = orderEntities.map(orderMapper::toOrderResponse);
        return CustomPageResponse.fromPage(orderResponses);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreationRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .date(LocalDate.now())
                .totalPrice(BigDecimal.ZERO)
                .orderProducts(new HashSet<>())
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;
        Set<OrderProductEntity> orderProductEntitySet = new HashSet<>();

        for (OrderProductDto productDto : request.getProducts()) {
            ProductEntity productEntity = productRepository.findByIdForUpdate(productDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productDto.getProductId()));

            // ✅ Kiểm tra tồn kho
            if (productEntity.getTotal() < productDto.getQuantity()) {
                throw new RuntimeException("Product " + productEntity.getName() + " is out of stock!");
            }

            // ✅ Giảm số lượng trong kho
            productEntity.setTotal(productEntity.getTotal() - productDto.getQuantity());

            OrderProductEntity orderProduct = OrderProductEntity.builder()
                    .order(order)
                    .product(productEntity)
                    .quantity(productDto.getQuantity())
                    .priceAtPurchase(productDto.getPriceAtPurchase())
                    .build();

            totalPrice = totalPrice.add(
                    productDto.getPriceAtPurchase()
                            .multiply(BigDecimal.valueOf(productDto.getQuantity())));

            orderProductEntitySet.add(orderProduct);
        }

        // ✅ Lưu cập nhật số lượng sản phẩm
        productRepository.saveAll(orderProductEntitySet.stream().map(OrderProductEntity::getProduct).toList());

        // ✅ Lưu Order + OrderProduct
        orderProductRepository.saveAll(orderProductEntitySet);
        order.setTotalPrice(totalPrice);
        order.setOrderProducts(orderProductEntitySet);

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse selectPaymentMethod(String orderId, String paymentMethod) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException("Cannot change payment method for this order.");
        }

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .user(order.getUser())
                .method(paymentMethod)
                .date(LocalDateTime.now())
                .pricePayment(order.getTotalPrice())
                .status(PaymentStatus.PROCESSING) // Đang chờ thanh toán
                .build();

        paymentRepository.save(payment);
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse confirmPayment(String orderId, boolean isSuccess) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals(OrderStatus.PENDING) ||
                !payment.getStatus().equals(PaymentStatus.PROCESSING)) {
            throw new RuntimeException("Order is not in a valid state for payment confirmation");
        }

        if (isSuccess) {
            // ✅ Thanh toán thành công
            order.setStatus(OrderStatus.PAID);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setDate(LocalDateTime.now());
        } else {
            // ❌ Thanh toán thất bại -> Rollback
            order.setStatus(OrderStatus.CANCELLED);
            payment.setStatus(PaymentStatus.FAILED);

            // ✅ Hoàn lại số lượng sản phẩm trong kho
            for (OrderProductEntity orderProduct : order.getOrderProducts()) {
                ProductEntity product = orderProduct.getProduct();
                product.setTotal(product.getTotal() + orderProduct.getQuantity());
            }
            productRepository.saveAll(order.getOrderProducts().stream()
                    .map(OrderProductEntity::getProduct).toList());

            log.info("Payment failed for order {}. Order cancelled and stock restored.", orderId);
        }

        // ✅ Lưu cập nhật vào DB
        paymentRepository.save(payment);
        orderRepository.save(order);

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderMapper.toListOrderResponse(orderRepository.findAll());
    }

    @Override
    public OrderResponse getOrderById(String id) {
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow( () ->
                        new AppException(ErrorCode.ORDER_NOT_EXISTED)
                );

        return orderMapper.toOrderResponse(orderEntity);
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(String id, OrderUpdateRequest request) {
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        // Lấy danh sách sản phẩm cũ trong đơn hàng
        Set<OrderProductEntity> existingOrderProducts = new HashSet<>(orderEntity.getOrderProducts());

        // Hoàn lại số lượng sản phẩm cũ về kho trước khi cập nhật đơn hàng
        for (OrderProductEntity oldOrderProduct : existingOrderProducts) {
            ProductEntity product = oldOrderProduct.getProduct();
            product.setTotal(product.getTotal() + oldOrderProduct.getQuantity());
        }

        Set<OrderProductEntity> newOrderProducts = new HashSet<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderProductDto productDto : request.getProducts()) {
            ProductEntity productEntity = productRepository.findByIdForUpdate(productDto.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.NAME_PRODUCT_NOT_EXISTED));

            // Kiểm tra tồn kho trước khi cập nhật
            if (productEntity.getTotal() < productDto.getQuantity()) {
                throw new RuntimeException("Product " + productEntity.getName() + " is out of stock!");
            }

            // Cập nhật số lượng sản phẩm mới vào kho
            productEntity.setTotal(productEntity.getTotal() - productDto.getQuantity());

            // Kiểm tra xem sản phẩm đã có trong đơn hàng chưa
            OrderProductEntity orderProductEntity = existingOrderProducts.stream()
                    .filter(op -> op.getProduct().getId().equals(productEntity.getId()))
                    .findFirst()
                    .orElse(OrderProductEntity.builder()
                            .order(orderEntity)
                            .product(productEntity)
                            .quantity(0)
                            .priceAtPurchase(productDto.getPriceAtPurchase())
                            .build());

            // Cập nhật số lượng và giá
            orderProductEntity.setQuantity(productDto.getQuantity());
            orderProductEntity.setPriceAtPurchase(productDto.getPriceAtPurchase());

            totalPrice = totalPrice.add(
                    productDto.getPriceAtPurchase().multiply(BigDecimal.valueOf(productDto.getQuantity())));

            newOrderProducts.add(orderProductEntity);
        }

        // Xóa các sản phẩm không còn trong danh sách mới
        existingOrderProducts.removeAll(newOrderProducts);
        orderProductRepository.deleteAll(existingOrderProducts);

        // Lưu các sản phẩm cập nhật vào DB
        orderProductRepository.saveAll(newOrderProducts);
        productRepository.saveAll(newOrderProducts.stream().map(OrderProductEntity::getProduct).toList());

        // Cập nhật tổng giá trị đơn hàng
        orderEntity.setTotalPrice(totalPrice);
        orderEntity.getOrderProducts().clear();
        orderEntity.getOrderProducts().addAll(newOrderProducts);

        return orderMapper.toOrderResponse(orderRepository.save(orderEntity));
    }

    @Override
    @Transactional
    public void deleteOrder(String id) {
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        // Hoàn trả lại số lượng sản phẩm về kho
        for (OrderProductEntity orderProduct : orderEntity.getOrderProducts()) {
            ProductEntity product = orderProduct.getProduct();
            product.setTotal(product.getTotal() + orderProduct.getQuantity());
        }

        // Lưu lại sản phẩm đã hoàn số lượng
        productRepository.saveAll(orderEntity.getOrderProducts().stream()
                .map(OrderProductEntity::getProduct).toList());

        // Xóa toàn bộ sản phẩm trong đơn hàng trước khi xóa đơn hàng
        orderProductRepository.deleteAll(orderEntity.getOrderProducts());

        // Xóa đơn hàng
        orderRepository.deleteById(id);
    }
}
