package com.sbp.orderservice.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sbp.orderservice.api.dto.Payment;
import com.sbp.orderservice.api.dto.TransactionRequest;
import com.sbp.orderservice.api.dto.TransactionResponse;
import com.sbp.orderservice.api.entity.Order;
import com.sbp.orderservice.api.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RestTemplate restTemplate;

	public TransactionResponse saveOrder(TransactionRequest request) {
		String response = "";
		Order order = request.getOrder();
		Payment payment = request.getPayment();
		payment.setOrderId(order.getId());
		payment.setAmount(order.getPrice());

		// rest call
		Payment paymentResponse = restTemplate.postForObject("http://PAYMENT-SERVICE/payment/doPayment", payment,
				Payment.class);
		response = paymentResponse.getPaymentStatus().equals("success")
				? "Payment Processed Successfully and Order Placed"
				: "There is failure in Payment API, Order added to cart";
		orderRepository.save(order);
		return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(),
				response);
	}
}
