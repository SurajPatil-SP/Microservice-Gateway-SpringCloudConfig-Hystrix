package com.sbp.paymentservice.api.service;

import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbp.paymentservice.api.entity.Payment;
import com.sbp.paymentservice.api.repository.PaymentRepository;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	public Payment doPayment(Payment payment) {
		payment.setPaymentStatus(paymentProcessing());
		payment.setTransactionId(UUID.randomUUID().toString());
		return paymentRepository.save(payment);
	}

	public String paymentProcessing() {
		// api should be 3rd party payment gateway (paypal,paytm...)
		return new Random().nextBoolean() ? "success" : "false";
	}
	
	 public Payment findPaymentHistoryByOrderId(int orderId) {
	        Payment payment=paymentRepository.findByOrderId(orderId);
	        return payment ;
	    }

}
