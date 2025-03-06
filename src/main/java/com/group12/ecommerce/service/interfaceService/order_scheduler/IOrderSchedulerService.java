package com.group12.ecommerce.service.interfaceService.order_scheduler;

public interface IOrderSchedulerService {
    public void scheduleOrderRollback(String orderId, int seconds);
    public void cancelOrderRollback(String orderId);
    public void schedulePaymentRollback(String orderId, int minutes);
    public void cancelPaymentFromRollback(String orderId);
    public void checkTimeouts();
    public void cancelOrder(String orderId);
}
