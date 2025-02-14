package com.group12.ecommerce.service.implementService.order;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.order.OrderCreationRequest;
import com.group12.ecommerce.dto.request.order.OrderUpdateRequest;
import com.group12.ecommerce.dto.request.order_product.OrderProductDto;
import com.group12.ecommerce.dto.response.order.OrderResponse;
import com.group12.ecommerce.dto.response.page.CustomPageResponse;
import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.order_product.OrderProductEntity;
import com.group12.ecommerce.entity.product.ProductEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.mapper.order.IOrderMapper;
import com.group12.ecommerce.repository.order.IOrderRepository;
import com.group12.ecommerce.repository.order_product.IOrderProductRepository;
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
