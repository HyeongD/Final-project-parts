/**
 * 
 */
package com.BridginSystem.bridge.main.services;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dheyeong
 *Alipay service interface
 */
public interface IAlipayService {
	String webpagePay(String subject,String currency,String method, String outTradeNo, String totalAmount, String Description);

	String notifyUrl(HttpServletRequest request);
}
