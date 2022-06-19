package com.dheyeong.spring.paypal.api.Controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dheyeong.spring.paypal.api.PayOrder;
import com.dheyeong.spring.paypal.api.PaypalConfig;
import com.dheyeong.spring.paypal.api.PaypalService;
import com.dheyeong.spring.paypal.api.ResultPrinter;
import com.paypal.api.payments.Payout;
import com.paypal.api.payments.PayoutBatch;
import com.paypal.api.payments.PayoutBatchHeader;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.http.HttpResponse;

import ch.qos.logback.classic.Logger;

@Controller
public class PaypalPayoutController {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PaypalController.class);
	@Autowired
	PaypalService service;
	String token;
	String getTocken() throws IOException {
		String token = service.generatePayoutAccessToken();
		return token;
	}
	
	
	/*
	 * @RequestMapping("/singlePayout") public ModelAndView singlePayout(){
	 * ModelAndView mv = new ModelAndView(); mv.setViewName("singlePayout"); return
	 * mv; }
	 */

	
	  @RequestMapping("/singlePayout") 
	  public String singlePayout() { 
	  return "singlePayout"; 
	  }
	 
	  @PostMapping("/payout")
	  public void createpayout(@ModelAttribute("payOrder") PayOrder payOrder /*,HttpServletRequest req, HttpServletResponse resp*/) throws IOException {
		  try {
				token= getTocken();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		  JSONObject jOb = service.createBatchPayout(payOrder.getSenderbatchId(),payOrder.getAmount(), payOrder.getSetEmailSubject(),payOrder.getCurrency(),
					payOrder.getNote(), payOrder.getSenderItemId(),payOrder.getReceiver(), payOrder.getPhoneNumberOrEmail(), payOrder.getCountryCode(),
					payOrder.getLanguage(),token);
		  System.out.println(jOb.toString());
	  }
		
	@PostMapping("/payouts")
	public PayoutBatch createSinglePayout(@ModelAttribute("payOrder") PayOrder payOrder,HttpRequest req, HttpResponse resp /*,HttpServletRequest req, HttpServletResponse resp*/) {
		Payout payout = service.createPayout(payOrder.getSenderbatchId(),payOrder.getAmount(), payOrder.getSetEmailSubject(),payOrder.getCurrency(),
				payOrder.getNote(), payOrder.getSenderItemId(),payOrder.getReceiver(), payOrder.getPhoneNumberOrEmail(), payOrder.getCountryCode(), payOrder.getLanguage());
		//PaypalConfig ppConfig = PaypalConfig();
		PayoutBatch batch= null;
		//PayoutBatchHeader pBacthHeader = new PayoutBatchHeader();
		//pBacthHeader.
		
		
		
		//batch.setBatchHeader(null);
		try {
			//batch = payout.createSynchronous(ppConfig.apiContext());
			APIContext apc = PaypalConfig();
			batch = payout.createSynchronous(apc);
			LOGGER.info("Payouty with ID:"+batch.getBatchHeader().getPayoutBatchId());
			
			
			URL url = new URL("https://api-m.sandbox.paypal.com/v1/payments/payouts");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("accept", "application/json");
			http.setRequestProperty("accept-language", "en-US");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("authorization", "your authorization");
			String body="grant_type=client_credentials";
			
			//Send request
			
			/*DataOutputStream wr = new DataOutputStream(http.getOutputStream());
			wr.writeBytes(body);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader((http.getInputStream())));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = in.readLine())!=null) {
				response.append((inputLine));
			}
			in.close();
			// Print the response
			System.out.println(response.toString());*/
			byte[] out = body.getBytes(StandardCharsets.UTF_8);
			OutputStream stream = http.getOutputStream();
			stream.write(out);
			System.out.println(http.getResponseCode() +" "+http.getResponseMessage());
			/*req.getRequestDispatcher("https://api-m.sandbox.paypal.com/v1/payments/payouts");
			req.setCharacterEncoding("StandardCharsets.UTF_8");*/
			//req =(HttpServletRequest) http;
			//System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
			
			/*
			 * ResultPrinter.addResult(req, resp, "Created Single Synchronous Payout",
			 * Payout.getLastRequest(), Payout.getLastResponse(), null);
			 * System.out.println(batch.toJSON());
			 */
			http.disconnect();
		}catch(PayPalRESTException e) {
			/*
			 * ResultPrinter.addResult(req, resp, "Created Single Synchronous Payout",
			 * Payout.getLastRequest(), null, e.getMessage());
			 */
			e.printStackTrace();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return batch;
		
	}

	private APIContext PaypalConfig() throws PayPalRESTException {
		PaypalConfig ppConfig= new PaypalConfig();
		return ppConfig.apiContext();
	}
}
