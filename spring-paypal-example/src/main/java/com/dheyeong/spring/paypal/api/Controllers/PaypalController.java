package com.dheyeong.spring.paypal.api.Controllers;


import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dheyeong.spring.paypal.api.Order;
import com.dheyeong.spring.paypal.api.PayOrder;
import com.dheyeong.spring.paypal.api.PaypalConfig;
import com.dheyeong.spring.paypal.api.PaypalService;
import com.dheyeong.spring.paypal.api.ResultPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Payout;
import com.paypal.base.rest.PayPalRESTException;

import ch.qos.logback.classic.Logger;

import com.paypal.api.payments.PayoutBatch;
import com.paypal.api.payments.PayoutBatchHeader;

@Controller
public class PaypalController {
	
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PaypalController.class);
	@Autowired
	PaypalService service;
	
	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "pay/cancel";
	@GetMapping("/")
	public String home() { // for the home.html file using thymeleaf
		return "home";
	}
	
	//writtting a method to perform the payment process
	@PostMapping("/pay")
	public String payment(@ModelAttribute("order") Order order) {
		try {
		 Payment payment =service.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),  order.getIntent(), order.getDescription(),
				 "http://localhost:9091/"+CANCEL_URL,"http://localhost:9091/"+SUCCESS_URL);
		 for(Links link:payment.getLinks()) {
			 if(link.getRel().equals("approval_url")) {
				 return "redirect:"+link.getHref();
			 }
		 }
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}
	
	@GetMapping(value = CANCEL_URL)
	public String cancelPay() {
		return "cancel";
	}
	
	/// if the user proceeding to the payment is success then execute payment
	@GetMapping(value = SUCCESS_URL)
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
		try {
			Payment payment = service.executePayment(paymentId, payerId);
			System.out.println(payment.toJSON());
			if(payment.getState().equals("approved")) {
				return "success";
			}
		}catch(PayPalRESTException e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/";
		
	}
	
	
	
}
