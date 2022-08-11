package com.ibrahim.tdd.services;

import com.ibrahim.tdd.clients.PaymentClient;
import com.ibrahim.tdd.dtos.OrderDto;
import com.ibrahim.tdd.models.Order;
import com.ibrahim.tdd.repositories.OrderRepository;
import com.ibrahim.tdd.services.requests.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final PaymentClient paymentClient;

    public OrderDto createOrder(CreateOrderRequest request) {

        BigDecimal totalPrice = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getAmount()));
        Order order = Order.builder()
                            .totalPrice(totalPrice)
                            .build();
        this.paymentClient.pay(order);
        Order save = this.orderRepository.save(order);
        return OrderDto.builder().id(save.getId()).totalPrice(totalPrice).build();
    }

}
