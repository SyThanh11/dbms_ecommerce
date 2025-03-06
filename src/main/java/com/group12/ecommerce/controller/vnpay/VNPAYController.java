package com.group12.ecommerce.controller.vnpay;

import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.service.interfaceService.order.IOrderService;
import com.group12.ecommerce.service.interfaceService.vnpay.IVNPAYService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/v1/vnpay")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "VNPAY payment")
public class VNPAYController {
    @Autowired
    IVNPAYService vnPayService;
    @Autowired
    IOrderService orderService;

    @PostMapping("/submitOrder")
    public ResponseEntity<ApiResponse<?>> submidOrder(@RequestParam("amount") BigDecimal orderTotal,
                                   @RequestParam("orderInfo") String orderInfo,
                                   HttpServletRequest request){
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createPaymentUrl(orderTotal, orderInfo, baseUrl, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Get url vnpay success!")
                        .data(vnpayUrl)
                        .build()
        );
    }

    @GetMapping("/vnpay-payment")
    public ResponseEntity<ApiResponse<?>> getMapping(HttpServletRequest request, Model model){
        int paymentStatus =vnPayService.orderReturn(request);


        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        orderService.confirmPayment(orderInfo, paymentStatus == 1);

        String message = paymentStatus == 1 ? "payment success" : "payemnt fail";
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message(message)
                        .data(paymentStatus)
                        .build()
        );
    }
}
