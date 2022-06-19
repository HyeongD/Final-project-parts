package vnpaytest.Controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URLPermission;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ch.qos.logback.classic.Logger;
import vnpaytest.configurations.VnpayConfig;

@Controller
public class MainController {

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MainController.class);

	VnpayConfig Config;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/vpay")
	public void payment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String vnp_OrderInfo = req.getParameter("vnp_OrderInfo");
		String orderType = req.getParameter("ordertype");
		String vnp_TxnRef = Config.getRandomNumber(8);
		String vnp_IpAddr = Config.getIpAddress(req);
		String vnp_TmnCode = Config.vnp_TmnCode;

		int amount = Integer.parseInt(req.getParameter("amount")) * 100;
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(amount));
		vnp_Params.put("vnp_CurrCode", "VND");
		String bank_code = req.getParameter("bankcode");
		if (bank_code != null && !bank_code.isEmpty()) {
			vnp_Params.put("vnp_BankCode", bank_code);
		}
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
		vnp_Params.put("vnp_OrderType", orderType);

		String locate = req.getParameter("language");
		if (locate != null && !locate.isEmpty()) {
			vnp_Params.put("vnp_Locale", locate);
		} else {
			vnp_Params.put("vnp_Locale", "vn");
		}
		vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());

		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		// Add Params of 2.0.1 Version
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
		
		// Build data to hash and querystring
		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		/*
		 * // if (queryUrl.containsKey("vnp_SecureHashType")) {
		 * fields.remove("vnp_SecureHashType"); } if
		 * (fields.containsKey("vnp_SecureHash")) { fields.remove("vnp_SecureHash"); }
		 */

		String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
		// req.authenticate(resp);

		URL url = new URL(paymentUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.connect();

		BufferedReader bffInput = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String getinput = "";
		StringBuilder response = new StringBuilder();
		while ((getinput = bffInput.readLine()) != null) {
			response.append(getinput);
		}
		bffInput.close();
		con.disconnect();
		String rsp = response.toString();
		String rspDecoder = URLDecoder.decode(rsp, "UTF-8");
		String[] rspArray = rspDecoder.split("&|\\=");

		JsonObject job = new JsonObject();
		// job.addProperty("code", "00");
		// job.addProperty("message", "success");
		job.addProperty("data", Arrays.toString(rspArray));
		Gson gson = new Gson();
		// resp.setCharacterEncoding("UTF-8");
		// resp.addHeader("response status", Integer.toString(resp.getStatus()));
		resp.getWriter().write(gson.toJson(job));

		JsonNode jsNode = toJsonNode(job);

		// System.out.print(jsNode.spliterator().characteristics());
		// String status =Integer.toString(resp.getStatus());
		// job.addProperty("response status", Integer.toString(resp.getStatus()));
		System.out.print(jsNode.toPrettyString());
		// System.out.print(resp.getOutputStream().toString().trim());
		// System.out.print(resp.setCharacterEncoding("UTF-8"));

	}

	@PostMapping("/vpay")
	public void Postpayment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String vnp_OrderInfo = req.getParameter("vnp_OrderInfo");
		String orderType = req.getParameter("ordertype");
		String vnp_TxnRef = Config.getRandomNumber(8);
		String vnp_IpAddr = Config.getIpAddress(req);
		String vnp_TmnCode = Config.vnp_TmnCode;

		int amount = Integer.parseInt(req.getParameter("amount")) * 100;
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(amount));
		vnp_Params.put("vnp_CurrCode", "VND");
		String bank_code = req.getParameter("bankcode");
		if (bank_code != null && !bank_code.isEmpty()) {
			vnp_Params.put("vnp_BankCode", bank_code);
		}
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
		vnp_Params.put("vnp_OrderType", orderType);

		String locate = req.getParameter("language");
		if (locate != null && !locate.isEmpty()) {
			vnp_Params.put("vnp_Locale", locate);
		} else {
			vnp_Params.put("vnp_Locale", "vn");
		}
		vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+9"));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());

		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		// Add Params of 2.0.1 Version
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
		/*
		 * //Billing vnp_Params.put("vnp_Bill_Mobile",
		 * req.getParameter("txt_billing_mobile")); vnp_Params.put("vnp_Bill_Email",
		 * req.getParameter("txt_billing_email")); String fullName =
		 * (req.getParameter("txt_billing_fullname")).trim(); if (fullName != null &&
		 * !fullName.isEmpty()) { int idx = fullName.indexOf(' '); String firstName =
		 * fullName.substring(0, idx); String lastName =
		 * fullName.substring(fullName.lastIndexOf(' ') + 1);
		 * vnp_Params.put("vnp_Bill_FirstName", firstName);
		 * vnp_Params.put("vnp_Bill_LastName", lastName);
		 * 
		 * }
		 */
		/*
		 * vnp_Params.put("vnp_Bill_Address", req.getParameter("txt_inv_addr1"));
		 * vnp_Params.put("vnp_Bill_City", req.getParameter("txt_bill_city"));
		 * vnp_Params.put("vnp_Bill_Country", req.getParameter("txt_bill_country")); if
		 * (req.getParameter("txt_bill_state") != null &&
		 * !req.getParameter("txt_bill_state").isEmpty()) {
		 * vnp_Params.put("vnp_Bill_State", req.getParameter("txt_bill_state")); } //
		 * Invoice vnp_Params.put("vnp_Inv_Phone", req.getParameter("txt_inv_mobile"));
		 * vnp_Params.put("vnp_Inv_Email", req.getParameter("txt_inv_email"));
		 * vnp_Params.put("vnp_Inv_Customer", req.getParameter("txt_inv_customer"));
		 * vnp_Params.put("vnp_Inv_Address", req.getParameter("txt_inv_addr1"));
		 * vnp_Params.put("vnp_Inv_Company", req.getParameter("txt_inv_company"));
		 * vnp_Params.put("vnp_Inv_Taxcode", req.getParameter("txt_inv_taxcode"));
		 * vnp_Params.put("vnp_Inv_Type", req.getParameter("cbo_inv_type"));
		 */
		// Build data to hash and querystring
		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

		URL url = new URL(paymentUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		;
		con.connect();

		/*
		 * BufferedWriter bffOutput = new BufferedWriter(new
		 * OutputStreamWriter(con.getOutputStream().)); String getinput="";
		 * StringBuilder response = new StringBuilder();
		 * 
		 * while((getinput = bffOutput.write("getinput")) != null) {
		 * response.append(getinput); } bffInput.close();
		 * 
		 * bffOutput.close();
		 */
		con.disconnect();
		/*
		 * String rsp = bffOutput.toString(); String rspDecoder= URLDecoder.decode(rsp,
		 * "UTF-8"); String[] rspArray = rspDecoder.split("&|\\=");
		 * 
		 * com.google.gson.JsonObject job = new JsonObject(); job.addProperty("code",
		 * "00"); job.addProperty("message", "success"); job.addProperty("data",
		 * Arrays.toString(rspArray));
		 */
		// job.addProperty("data", paymentUrl);
		JsonObject job = new JsonObject();
		job.addProperty("code", "00");
		job.addProperty("message", "success");
		job.addProperty("data", paymentUrl);
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(job));

	}

	public JsonNode toJsonNode(JsonObject jsObj) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(jsObj.toString());
	}

}
