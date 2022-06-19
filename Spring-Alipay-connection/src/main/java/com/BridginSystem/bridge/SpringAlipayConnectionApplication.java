package com.BridginSystem.bridge;

import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alipay.ams.AMS;
import com.alipay.ams.AMSClient;
import com.alipay.ams.cfg.AMSSettings;
import com.alipay.ams.domain.Amount;
import com.alipay.ams.domain.Callback;
import com.alipay.ams.domain.Env;
import com.alipay.ams.domain.Merchant;
import com.alipay.ams.domain.Order;
import com.alipay.ams.domain.PaymentResultModel;
import com.alipay.ams.domain.ResponseHeader;
import com.alipay.ams.domain.ResponseResult;
import com.alipay.ams.domain.Store;
import com.alipay.ams.domain.requests.UserPresentedCodePaymentRequest;
import com.alipay.ams.domain.responses.UserPresentedCodePaymentResponse;

@SpringBootApplication
public class SpringAlipayConnectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAlipayConnectionApplication.class, args);
		AMSSettings cfg = new AMSSettings();
        
        

        long amountInCents = 1000l;

        Order order = new Order();
        Currency currency = Currency.getInstance("JPY");
        order.setOrderAmount(new Amount(currency, amountInCents));
        order.setOrderDescription("New White Lace Sleeveless");
        order.setReferenceOrderId("0000000001");
        order.setMerchant(new Merchant("Some_Mer", "seller231117459", "7011", new Store(
            "Some_store", "store231117459", "7011")));

        order.setEnv(new Env());
        order.getEnv().setStoreTerminalId("some_setStoreTerminalId");
        order.getEnv().setStoreTerminalRequestTime("2020-06-11T13:35:02+08:00");

        String paymentRequestId = "PR20190000000001_" + System.currentTimeMillis();
        String buyerPaymentCode = "288888888888888888";


        final UserPresentedCodePaymentRequest request = new UserPresentedCodePaymentRequest(cfg,
            paymentRequestId, order, currency, amountInCents, buyerPaymentCode);

        AMS.with(cfg).execute(request,
        //Handle the API response in a callback, or you can just use the predefined `UserPresentedCodePaymentCallback`
            new Callback<UserPresentedCodePaymentRequest, UserPresentedCodePaymentResponse>() {

                @Override
                public void onIOException(IOException e, AMSClient client,
                                          UserPresentedCodePaymentRequest request) {
                    //Handle IOException, retry one more time, make an inquiry or make a cancellation.
                }

                @Override
                public void onHttpStatusNot200(AMSClient client,
                                               UserPresentedCodePaymentRequest request, int code) {
                    //Retry one more time, make an inquiry or make a cancellation.
                }

                @Override
                public void onFstatus(AMSClient client, UserPresentedCodePaymentRequest request,
                                      ResponseResult responseResult) {
                    //Check the result code
                }

                @Override
                public void onUstatus(AMSClient client, UserPresentedCodePaymentRequest request,
                                      ResponseResult responseResult) {
                    //Payment is still under processing...  
                    //Schedule a later inquiry
                }

                @Override
                public void onSstatus(AMSClient client, String requestURI,
                                      ResponseHeader responseHeader, HashMap<String, Object> body,
                                      UserPresentedCodePaymentRequest request) {
                    UserPresentedCodePaymentResponse paymentResponse = new UserPresentedCodePaymentResponse(
                        cfg, requestURI, responseHeader, body);
                    PaymentResultModel resultModel = paymentResponse.getPaymentResultModel();
                    //... Get payment result info from resultModel
                    //...

                }
            });
	}

}
