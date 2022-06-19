package com.BridginSystem.bridge.main.Controllers;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.BridginSystem.bridge.main.services.AlipayConfig;
import com.BridginSystem.bridge.main.services.AlipayOrder;
import com.BridginSystem.bridge.main.services.IAlipayService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.easysdk.factory.Factory;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/alipay")
public class AlipayController {
	@Autowired
	IAlipayService alipayService;
	
	@GetMapping("/welcome")
	public String helloWorld() {
		return "Welcome to ALipay!";
	}
	@GetMapping("/")
	public String index() { 
		return "index";
	}
//	@GetMapping("/")
//	public String MyIndex() { 
//		return "MyIndex";
//	}
//	AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","app_id","your private_key","json","GBK","alipay_public_key","RSA2");
//	AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//	request.setNotifyUrl("");
//	request.setReturnUrl("");
//	JSONObject bizContent = new JSONObject();
//	bizContent.put("out_trade_no", "20210817010101004");
//	bizContent.put("total_amount", 0.01);
//	bizContent.put("subject", "测试商品");
//	bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
//	equest.setBizContent(bizContent.toString());
//	AlipayTradePagePayResponse response = alipayClient.execute(request);
//	if(response.isSuccess()){
//	System.out.println("调用成功");
//	} else {
//	System.out.println("调用失败");
//	}
	//@Value("${alipay.returnUrl}")
    private String returnUrl="http://localhost:8080/alipay/return";
	
	/**
     * Order payment
     * When users click to pay, they should transfer parameters, which I have omitted here to avoid the trouble of modification. You should be able to realize it by yourself
	 * @throws Exception 
     */
    @SneakyThrows
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay() throws Exception {
    	
    	 //AlipayOrder alipayorder = new AlipayOrder();
        com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse response = Factory.Payment
                //Choose a web payment platform
                .Page()
                //Call payment method: order name, order number, amount, callback page
                .pay("Test product", UUID.randomUUID().toString(), "5", returnUrl);
        //Business code can be added here: for example, order generation and so on..
        return response.body;
    }
    /**
    * Interface of asynchronous callback after payment
	 * This interface requires intranet penetration because it requires others to visit your local website
    *
    * @param request
    * @return
    */
   @RequestMapping(value = "/notify", method = RequestMethod.POST)
   public String notifyAsync(HttpServletRequest request) {
       Map<String, String> map = new HashMap();
       Enumeration<String> parameterNames = request.getParameterNames();
       while (parameterNames.hasMoreElements()) {
           String key = parameterNames.nextElement();
           String value = request.getParameter(key);
           map.put(key, value);
       }
       //Signature verification
       try {
           if (Factory.Payment.Common().verifyNotify(map)) {
               //Verify the user's payment results
               String trade_status = request.getParameter("trade_status");
               if ("TRADE_SUCCESS".equals(trade_status)) {
                   //Here you can update the status of the order and so on..

               }
           } else {
               return "fail";
           }
       } catch (Exception e) {
           return "fail";
       }
       return "success";
    }
   /**
    * Asynchronous callback interface
    * 
    */
   @RequestMapping(value = "/notifyUrl", method = RequestMethod.POST)
   public String notifyInterface(HttpServletRequest request) throws Exception {
       
	return alipayService.notifyUrl(request);
   }
	
}
