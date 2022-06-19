package com.dheyeong.spring.paypal.api;

import java.util.HashMap;
import java.util.Map;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {
	@Value("${paypal.client.id}")
	private String clientId;
	@Value("${paypal.client.secret}")
	private String clientSecret;
	@Value("${paypal.mode}")
	private String mode;
	
	// returning the configuration mode
	@Bean
	public Map<String, String> paypalSdkConfig(){
		Map<String, String> configMap = new HashMap<>();
		configMap.put("mode",mode); 
		return configMap;
	}
	 public PaypalConfig(){
		 
	 }
	
	//creating an oauth Tocken return a constructor for our paypal account by giving it Cl.id and cl.sec
	@Bean
	public OAuthTokenCredential oAuthTockenCredentials() {
		return new OAuthTokenCredential(clientId, clientSecret,paypalSdkConfig());
	}
	
	//creating an apiContext constructor
	@Bean
	public APIContext apiContext() throws PayPalRESTException  {
		//APIContext context = new APIContext(oAuthTockenCredentials().getAccessToken());
		//context.setConfigurationMap(paypalSdkConfig());
		//mode="sandbox";
		APIContext context = new APIContext(clientId, clientSecret,mode);
		//context.setConfigurationMap(paypalSdkConfig());
		return  context;
		
	}
}
