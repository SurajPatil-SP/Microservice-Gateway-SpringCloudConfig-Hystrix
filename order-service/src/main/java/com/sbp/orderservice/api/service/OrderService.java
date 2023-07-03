package com.sbp.orderservice.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.sbp.orderservice.api.dto.Payment;
import com.sbp.orderservice.api.dto.TransactionRequest;
import com.sbp.orderservice.api.dto.TransactionResponse;
import com.sbp.orderservice.api.entity.Order;
import com.sbp.orderservice.api.repository.OrderRepository;

import reactor.core.publisher.Mono;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

//	@Autowired
//	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient webClient;

	public TransactionResponse saveOrder(TransactionRequest request) {
		String response = "";
		Order order = request.getOrder();
		Payment payment = request.getPayment();
		payment.setOrderId(order.getId());
		payment.setAmount(order.getPrice());

		// rest call
//		Payment paymentResponse = restTemplate.postForObject("http://PAYMENT-SERVICE/payment/doPayment", payment,
//				Payment.class);
		
		Payment paymentResponse = webClient.post().uri("/doPayment")
				.body(Mono.just(payment), Payment.class)
				.retrieve()
				.bodyToMono(Payment.class)
				.block();
		
		response = paymentResponse.getPaymentStatus().equals("success")
				? "Payment Processed Successfully and Order Placed"
				: "There is failure in Payment API, Order added to cart";
		orderRepository.save(order);
		return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(),
				response);
	}
}
