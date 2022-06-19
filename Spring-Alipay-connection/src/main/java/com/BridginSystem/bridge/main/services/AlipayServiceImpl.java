package com.BridginSystem.bridge.main.services;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayConfig;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AlipayServiceImpl implements IAlipayService {
	
	Logger log = LoggerFactory.getLogger(AlipayServiceImpl.class);
	//private OrderService orderService;
	//@Value("${alipay.returnUrl}")
	private String returnUrl="http://localhost:8080/alipay/return";
	//private PayService payService;
	@Override
	public String webpagePay(String subject,String currency,String method, String outTradeNo, String totalAmount, String Description) {
		// TODO Auto-generated method stub
		
		try {
			AlipayTradePagePayResponse response = Factory.Payment.Page().pay(subject, outTradeNo, totalAmount, returnUrl);
			if(ResponseChecker.success(response)) {
				log.info("Successful call !");
				return response.getBody();
			}else {
				log.error(" Call failed ! reason :"+ response.getBody());
			}
		}catch(Exception e) {
			log.error(" Call exception ! reason :"+ e.getMessage());
		}
		return null;
	}
	@Override
	public String notifyUrl(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
