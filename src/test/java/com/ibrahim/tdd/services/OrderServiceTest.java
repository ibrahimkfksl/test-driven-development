package com.ibrahim.tdd.services;


import com.ibrahim.tdd.clients.PaymentClient;
import com.ibrahim.tdd.dtos.OrderDto;
import com.ibrahim.tdd.models.Order;
import com.ibrahim.tdd.repositories.OrderRepository;
import com.ibrahim.tdd.services.requests.CreateOrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentClient paymentClient;

    private static Stream<Arguments> order_request() {
        return Stream.of(
                Arguments.of("code1", 5, BigDecimal.valueOf(12.3), BigDecimal.valueOf(61.5)),
                Arguments.of("code2", 10, BigDecimal.valueOf(15), BigDecimal.valueOf(150))
        );
    }

    @Test
    public void it_should_create_order(){

        //given
        /*
        PodamFactory factory = new PodamFactoryImpl();
        CreateOrderRequest request = factory.manufacturePojo(CreateOrderRequest.class);
        */
       CreateOrderRequest request = CreateOrderRequest.builder()
                                .productCode("code1")
                                .amount(5)
                                .unitPrice(BigDecimal.valueOf(12.3))
                                .build();

        //when
        OrderDto order = service.createOrder(request);

        //then
        then(order).isNotNull();
        then(order.getTotalPrice()).isEqualTo(BigDecimal.valueOf(6.6));
    }

    @Test
    public void it_should_create_order_with_10_items(){
        //given
        /*
        PodamFactory factory = new PodamFactoryImpl();
        CreateOrderRequest request = factory.manufacturePojo(CreateOrderRequest.class);
        */
        CreateOrderRequest request = CreateOrderRequest.builder()
                .productCode("code1")
                .amount(10)
                .unitPrice(BigDecimal.valueOf(15))
                .build();

        //when
        OrderDto order = service.createOrder(request);

        //then
        then(order).isNotNull();
        then(order.getTotalPrice()).isEqualTo(BigDecimal.valueOf(150));
    }


    //farklı farklı parametrelerle kodunu test etmek istiyorsan ParameterizedTest anatasyonunu kullanmalısın
    @ParameterizedTest
    @MethodSource("order_request")
    public void it_should_create_orders(String productCode, Integer amount, BigDecimal unitPrice, BigDecimal totalPrice){

        //given
        CreateOrderRequest request = CreateOrderRequest.builder()
                .productCode(productCode)
                .unitPrice(unitPrice)
                .amount(amount)
                .build();

        Order order = new Order();
        order.setId(12312321);

        when(orderRepository.save(ArgumentMatchers.any())).thenReturn(order);

        //when
        OrderDto orderDto = service.createOrder(request);

        //then
        then(orderDto.getTotalPrice()).isEqualTo(totalPrice);
    }


    @Test
    public void it_should_fail_order_creation_when_payment_failed(){
        //given

        CreateOrderRequest request = CreateOrderRequest.builder()
                .productCode("code1")
                .unitPrice(BigDecimal.valueOf(12))
                .amount(3)
                .build();

        //when

        // when(paymentClient.pay(any())).thenThrow(() -> new IllegalArgumentException()); void methodlarda bu yöntem çalışmıyor
        doThrow(new IllegalArgumentException()).when(paymentClient).pay(ArgumentMatchers.any());
        Throwable throwable = catchThrowable(() -> {
                                service.createOrder(request);
                             });


        //then

        verifyNoInteractions(orderRepository); //veritabanına yazmayacağı için bu doğrulamayı yapmalıyız. Eğer yazıyorsa bir sorun vardır.
        then(throwable).isInstanceOf(IllegalArgumentException.class);
    }
}
