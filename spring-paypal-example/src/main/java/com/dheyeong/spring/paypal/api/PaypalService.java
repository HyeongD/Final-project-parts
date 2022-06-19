package com.dheyeong.spring.paypal.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
///import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.braintreegateway.TransactionRequest;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Payout;
import com.paypal.api.payments.PayoutItem;
import com.paypal.api.payments.PayoutSenderBatchHeader;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class PaypalService {
	// injecting the ApI context
	@Autowired
	private APIContext apiContext;
	PayOrder payoutOrder = new PayOrder();
	
	//setting the payment 
	public Payment createPayment(Double total, String currency, String method, String intent, String description, String cancelUrl, String successUrl) throws PayPalRESTException {
		Amount amount = new Amount();
		amount.setCurrency(currency);
		total = new BigDecimal(total).setScale(2,RoundingMode.HALF_UP).doubleValue();
		amount.setTotal(String.format("%.2f", total));													//Paypal receives data types as string
		
		// creation and setting the transaction
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);
		
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction); 																	//  storing a transaction
		
		Payer payer = new Payer();
		//payer.setPaymentMethod(method.toString()); 														//payment method == bank account, credit/debit card, wallet
		payer.setPaymentMethod(method);
		
		Payment payment = new Payment();
		//payment.setIntent(intent.toString());
		payment.setIntent(intent);
		payment.setPayer(payer);												//setting Source of the funds for this payment represented by a PayPal account or a direct credit card.
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);
		
		return payment.create(apiContext);
	}
	// executing the payment by connecting the payerid to the payment using paymentid
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId); 																		//identifier of the payment resource created.
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);																//The ID of the Payer, passed in the `return_url` by PayPal.
		return payment.execute(apiContext,  paymentExecute);
	}
	
	// A resource representing a  Single payout
	@SuppressWarnings("removal")
	public Payout createPayout(String senderbatchId, String amount, String setEmailSubject, String currency,
			String Note,String senderItemId,String receiver,String phoneNumberOrEmail,String countryCode, String language/*, String token*/) {
		
		Payout payout = new Payout();
		
		
		/*
		 * // double rand = payoutOrder.randValue();
		 * payoutOrder.setSenderbatchId(Double.toString(rand)); senderbatchId =
		 * payoutOrder.getSenderbatchId();
		 */
		//setting the bacth header
		PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();
		senderBatchHeader.setSenderBatchId(senderbatchId).setEmailSubject(
				"You have a Payout!");
		
		//formating the item id
		/*
		 * SimpleDateFormat sdt = payoutOrder.mydateformat(); rand =
		 * payoutOrder.randValue(); senderItemId=
		 * sdt.YEAR_FIELD+sdt.MONTH_FIELD+sdt.DATE_FIELD+Double.toString(rand);
		 */
		//currency
		Currency amounts = new Currency();
		amounts.setValue(amount).setCurrency(currency);
		
		// #### Sender Item
		PayoutItem senderItem = new PayoutItem();
		senderItem.setRecipientType(phoneNumberOrEmail)
					.setNote(Note)
					.setReceiver(receiver)
					.setSenderItemId(senderItemId)
					.setAmount(amounts);
		
		List<PayoutItem> items = new ArrayList<PayoutItem>();
		items.add(senderItem);
		//payout.create(token, null)
		//payout.getCredential();
		//payout.create(apiContext, null)
		//payout.setSenderBatchHeader(senderBatchHeader).setItems(items);
		return payout;
	}
	//generating an access token
	public String generatePayoutAccessToken() throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
				RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");
				Request req = new Request.Builder()
				  .url("https://api-m.sandbox.paypal.com/v1/oauth2/token")
				  .method("POST", body)
				  .addHeader("Authorization", "your authorization key")// confer to paypal dev
				  .addHeader("Content-Type", "application/x-www-form-urlencoded")
				  .build();
				Response resp = client.newCall(req).execute();
				JSONObject jOb = new JSONObject(resp.body().string());
				String scope = jOb.getString("scope");
				String tokenType = jOb.getString("token_type");
				String appId = jOb.getString("app_id");
				String expiresIn = jOb.getString("expires_in");
				String nonce = jOb.getString("nonce");
				String  accessToken= jOb.getString("access_token");
				jOb=null;
		return accessToken;
	}
	
	//creating a batch payout method1
	public JSONObject createBatchPayout(String senderbatchId,String amount,
									String setEmailSubject,String currency,
									String note,String senderItemId,
									String receiver,String phoneNumberOrEmail,
									String countryCode,String language,
									String accessToken) throws IOException {
		//String accessToken= generatePayoutAccessToken();
		//senderbatchId="TS&P";
		//formatting the sender batchheader
		payoutOrder.setSenderbatchId(senderbatchId);
			//using okhttp to create a req/resp 
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				MediaType mediaType = MediaType.parse("application/json");
				RequestBody body = RequestBody.create(mediaType,
								"{\n    \"sender_batch_header\": {\n "
								+ "       \"sender_batch_id\": \""+payoutOrder.getSenderbatchId()+"\",\n"
								+ "        \"email_subject\": \"You have a payout!\",\n "
								+ "       \"email_message\": \"You have received a payout! Thanks for using our service!\"\n    },\n"
								+ "    \"items\": [\n"
								+ "        {\n"
								+ "            \"recipient_type\": \"EMAIL\",\n"
								+ "            \"amount\": {\n"
								+ "                \"value\": \""+amount+"\",\n"
								+ "                \"currency\": \""+currency+"\"\n"
								+ "         },\n"
								+ "            \"note\": \""+note+"\",\n"
								+ "            \"sender_item_id\": \""+senderItemId+"\",\n"
								+ "            \"receiver\": \""+receiver+"\",\n"
								+ "            \"notification_language\": \""+language+"\"\n"
								+ "        },\n"
								//+ "        {\n"
								//+ "            \"recipient_type\": \"PHONE\",\n"
								//+ "            \"amount\": {\n"
								//+ "                \"value\": \"20.00\",\n"
								///+ "                \"currency\": \"USD\"\n"
								//+ "            },\n"
								//+ "            \"note\": \"Thanks for your support!\",\n"
								//+ "            \"sender_item_id\": \"201403140002\",\n"
								//+ "            \"receiver\": \"1-523-661-2833\"\n"
								//+ "        },\n"
								//+ "        {\n"
								//+ "            \"recipient_type\": \"PAYPAL_ID\",\n"
								//+ "            \"amount\": {\n"
								//+ "                \"value\": \"30.00\",\n"
								//+ "                \"currency\": \"USD\"\n"
								//+ "            },\n"
								//+ "            \"note\": \"Thanks for your patronage!\",\n"
								//+ "            \"sender_item_id\": \"201403140003\",\n"
								//+ "            \"receiver\": \"G83KXTJ5EHCQ2\"\n"
								//+ "        }\n"
								+ "    ]\n"
								+ "}");
				Request req = new Request.Builder()
				  .url("https://api-m.sandbox.paypal.com/v1/payments/payouts")
				  .method("POST", body)
				  .addHeader("Content-Type", "application/json")
				  .addHeader("PayPal-Request-Id", "83cf51f2-2ae5-40b1-9d27-c48f9e5babeb")
				  .addHeader("Authorization", "Bearer"+ accessToken)
				  .build();
				Response resp = client.newCall(req).execute();
				JSONObject jOb = new JSONObject(resp.body().string());
				
		
		return /*jOb.toString()*/ jOb;
	}
	
	//returning a response after creating a payout method 2
	//creating a batch payout
		public Response createBatchPayoutResponse(String senderbatchId,String amount,
										String setEmailSubject,String currency,
										String note,String senderItemId,
										String receiver,String phoneNumberOrEmail,
										String countryCode,String language,
										String accessToken) throws IOException {
			//String accessToken= generatePayoutAccessToken();
			//senderbatchId="TS&P";
			//formatting the sender batchheader
			payoutOrder.setSenderbatchId(senderbatchId);
				//using okhttp to create a req/resp 
			OkHttpClient client = new OkHttpClient().newBuilder()
					  .build();
					MediaType mediaType = MediaType.parse("application/json");
					RequestBody body = RequestBody.create(mediaType,
									"{\n    \"sender_batch_header\": {\n "
									+ "       \"sender_batch_id\": \""+payoutOrder.getSenderbatchId()+"\",\n"
									+ "        \"email_subject\": \"You have a payout!\",\n "
									+ "       \"email_message\": \"You have received a payout! Thanks for using our service!\"\n    },\n"
									+ "    \"items\": [\n"
									+ "        {\n"
									+ "            \"recipient_type\": \"EMAIL\",\n"
									+ "            \"amount\": {\n"
									+ "                \"value\": \""+amount+"\",\n"
									+ "                \"currency\": \""+currency+"\"\n"
									+ "         },\n"
									+ "            \"note\": \""+note+"\",\n"
									+ "            \"sender_item_id\": \""+senderItemId+"\",\n"
									+ "            \"receiver\": \""+receiver+"\",\n"
									+ "            \"notification_language\": \""+language+"\"\n"
									+ "        },\n"
									//+ "        {\n"
									//+ "            \"recipient_type\": \"PHONE\",\n"
									//+ "            \"amount\": {\n"
									//+ "                \"value\": \"20.00\",\n"
									///+ "                \"currency\": \"USD\"\n"
									//+ "            },\n"
									//+ "            \"note\": \"Thanks for your support!\",\n"
									//+ "            \"sender_item_id\": \"201403140002\",\n"
									//+ "            \"receiver\": \"1-523-661-2833\"\n"
									//+ "        },\n"
									//+ "        {\n"
									//+ "            \"recipient_type\": \"PAYPAL_ID\",\n"
									//+ "            \"amount\": {\n"
									//+ "                \"value\": \"30.00\",\n"
									//+ "                \"currency\": \"USD\"\n"
									//+ "            },\n"
									//+ "            \"note\": \"Thanks for your patronage!\",\n"
									//+ "            \"sender_item_id\": \"201403140003\",\n"
									//+ "            \"receiver\": \"G83KXTJ5EHCQ2\"\n"
									//+ "        }\n"
									+ "    ]\n"
									+ "}");
					Request req = new Request.Builder()
					  .url("https://api-m.sandbox.paypal.com/v1/payments/payouts")
					  .method("POST", body)
					  .addHeader("Content-Type", "application/json")
					  .addHeader("PayPal-Request-Id", "83cf51f2-2ae5-40b1-9d27-c48f9e5babeb")
					  .addHeader("Authorization", "Bearer"+ accessToken)
					  .build();
					Response resp = client.newCall(req).execute();
					JSONObject jOb = new JSONObject(resp.body().string());
					Response cacheResponse = client.newCall(req).execute().cacheResponse();
			
			return cacheResponse;
		}
	//Showing the payout batch details
	public String showPayoutBatchDetails(String accesstoken,JSONObject JOb) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		
		MediaType mediaType = MediaType.parse("text/plain");
		//MediaType mediaType = MediaType.parse("text/json");
		//RequestBody body = RequestBody.create(mediaType, "");
		RequestBody body = RequestBody.create(mediaType, JOb.toString());
		Request req = new Request.Builder()
				.url("https://api.sandbox.paypal.com/v1/payments/payouts/" + JOb.getString("payout_batch_id"))
				.method("GET", body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer "+ accesstoken)
				.build();
		
		Response resp = client.newCall(req).execute();
		JSONObject jOb = new JSONObject(resp.body().string());
		
		return jOb.toString();
	}
	
	//Show payout item details
	public String ShowPayoutItemDetails(String accesstoken,JSONObject JOb) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
				
				MediaType mediaType = MediaType.parse("text/plain");
				//MediaType mediaType = MediaType.parse("text/json");
				//RequestBody body = RequestBody.create(mediaType, "");
				RequestBody body = RequestBody.create(mediaType, JOb.toString());
				Request req = new Request.Builder()
						.url("https://api.sandbox.paypal.com/v1/payments/payouts/" + JOb.getString("payout_item_id"))
						.method("GET", body)
						.addHeader("Content-Type", "application/json")
						.addHeader("Authorization", "Bearer "+ accesstoken)
						.build();
				
				Response resp = client.newCall(req).execute();
				JSONObject jOb = new JSONObject(resp.body().string());
		
		return jOb.toString();
		
	}
	
	//Cancel unclaimed payout
	public String cancelUnclaimedPayout(String accesstoken,JSONObject JOb) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		
		MediaType mediaType = MediaType.parse("text/plain");
		//MediaType mediaType = MediaType.parse("text/json");
		//RequestBody body = RequestBody.create(mediaType, "");
		RequestBody body = RequestBody.create(mediaType, JOb.toString());
		Request req = new Request.Builder()
				.url("https://api.sandbox.paypal.com/v1/payments/payouts/" + JOb.getString("payout_item_id"+"/cancel"))
				.method("GET", body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer "+ accesstoken)
				.build();
		
		Response resp = client.newCall(req).execute();
		JSONObject jOb = new JSONObject(resp.body().string());

return jOb.toString();
	}
	///////
	/*TransactionRequest request = new TransactionRequest()
			  .amount(request.queryParams("amount"))
			  .paymentMethodNonce(request.queryParams("paymentMethodNonce"))
			  .deviceData(request.queryParams("device_data"))
			  .orderId("Mapped to PayPal Invoice Number")
			  .options()
			    .submitForSettlement(true)
			    .paypal()
			      .customField("PayPal custom field")
			      .description("Description for PayPal email receipt")
			      .done()
			    .storeInVaultOnSuccess(true)
			    .done();

			Result<Transaction> saleResult = gateway.transaction().sale(request);

			if (result.isSuccess()) {
			  Transaction transaction = result.getTarget();
			  System.out.println("Success ID: " + transaction.getId());
			} else {
			  System.out.println("Message: " + result.getMessage());
			}*/
}
