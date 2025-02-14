package com.group12.ecommerce.mapper.order;

import com.group12.ecommerce.dto.request.order.OrderCreationRequest;
import com.group12.ecommerce.dto.request.order.OrderUpdateRequest;
import com.group12.ecommerce.dto.response.order.OrderResponse;
import com.group12.ecommerce.dto.response.order_product.OrderProductResponse;
import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.order_product.OrderProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IOrderMapper {
    IOrderMapper INSTANCE = Mappers.getMapper(IOrderMapper.class);

    @Mapping(target = "orderProducts", ignore = true)
    @Mapping(target = "user", ignore = true)
    OrderEntity toOrderEntity(OrderCreationRequest request);

    @Mapping(target = "products", source = "orderProducts") // 🔹 Mapping danh sách sản phẩm
    OrderResponse toOrderResponse(OrderEntity orderEntity);

    List<OrderResponse> toListOrderResponse(List<OrderEntity> orderEntityList);

    @Mapping(target = "orderProducts", ignore = true)
    void updateOrderEntity(@MappingTarget OrderEntity order, OrderUpdateRequest request);

    // 🔹 Mapping từ OrderProductEntity sang OrderProductResponse
    @Mappings({
            @Mapping(target = "product", source = "product"),
            @Mapping(target = "quantity", source = "quantity"),
            @Mapping(target = "priceAtPurchase", source = "priceAtPurchase")
    })
    OrderProductResponse toOrderProductResponse(OrderProductEntity orderProductEntity);

    // 🔹 Chuyển danh sách sản phẩm từ OrderEntity sang OrderResponse
    default Set<OrderProductResponse> mapOrderProducts(Set<OrderProductEntity> orderProducts) {
        return orderProducts.stream()
                .map(this::toOrderProductResponse)
                .collect(Collectors.toSet());
    }
}

