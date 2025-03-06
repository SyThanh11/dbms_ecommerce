package com.group12.ecommerce.service.implementService.order_scheduler;

import com.group12.ecommerce.entity.order.OrderEntity;
import com.group12.ecommerce.entity.order.OrderStatus;
import com.group12.ecommerce.entity.order_product.OrderProductEntity;
import com.group12.ecommerce.entity.payment.PaymentEntity;
import com.group12.ecommerce.entity.payment.PaymentStatus;
import com.group12.ecommerce.entity.product.ProductEntity;
import com.group12.ecommerce.repository.order.IOrderRepository;
import com.group12.ecommerce.repository.payment.IPaymentRepository;
import com.group12.ecommerce.repository.product.IProductRepository;
import com.group12.ecommerce.service.interfaceService.order_scheduler.IOrderSchedulerService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderSchedulerService implements IOrderSchedulerService {

    IOrderRepository orderRepository;
    IPaymentRepository paymentRepository;
    IProductRepository productRepository;

    private final ApplicationContext applicationContext;

    ConcurrentHashMap<String, Long> rollbackOrders = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Long> rollbackPayments = new ConcurrentHashMap<>();

    @Override
    @Async
    public void scheduleOrderRollback(String orderId, int seconds) {
        rollbackOrders.put(orderId, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds));
    }

    @Override
    public void cancelOrderRollback(String orderId) {
        rollbackOrders.remove(orderId);
        log.info("Rollback for order {} has been canceled.", orderId);
    }

    public void cancelPaymentFromRollback(String orderId) {
        rollbackPayments.remove(orderId);
        log.info("Rollback for payment {} has been removed.", orderId);
    }

    @Override
    @Async
    public void schedulePaymentRollback(String orderId, int minutes) {
        rollbackPayments.put(orderId, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes));
    }

    @Override
    @Scheduled(fixedRate = 5000)
    public void checkTimeouts() {
        long now = System.currentTimeMillis();

        rollbackOrders.entrySet().removeIf(entry -> {
            if (entry.getValue() < now) {
                getSelf().cancelOrder(entry.getKey());
                return true;
            }
            return false;
        });

        rollbackPayments.entrySet().removeIf(entry -> {
            if (entry.getValue() < now) {
                PaymentEntity payment = paymentRepository.findByOrderId(entry.getKey()).orElse(null);
                if (payment != null && payment.getStatus().equals(PaymentStatus.PROCESSING)) {
                    payment.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                    getSelf().cancelOrder(entry.getKey());
                }
                return true;
            }
            return false;
        });
    }

    private OrderSchedulerService getSelf() {
        return applicationContext.getBean(OrderSchedulerService.class);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);

        if (order == null || !order.getStatus().equals(OrderStatus.PENDING)) return;

        PaymentEntity payment = paymentRepository.findByOrderId(orderId).orElse(null);
        if (payment != null) {
            if (payment.getStatus().equals(PaymentStatus.SUCCESS)) {
                log.info("Order {} has a successful payment, skipping cancellation.", orderId);
                return;
            }
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }

        for (OrderProductEntity orderProduct : order.getOrderProducts()) {
            ProductEntity product = orderProduct.getProduct();
            product.setTotal(product.getTotal() + orderProduct.getQuantity());
        }

        productRepository.saveAll(order.getOrderProducts().stream()
                .map(OrderProductEntity::getProduct).toList());

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Order {} cancelled due to timeout.", orderId);
    }
}