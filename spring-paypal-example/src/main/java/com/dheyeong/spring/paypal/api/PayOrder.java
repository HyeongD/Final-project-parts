package com.dheyeong.spring.paypal.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayOrder {
	
	public static final int Maximumlength=15000;
	private String senderbatchId;	
	private String amount;
	private String setEmailSubject;
	private String currency;
	private String Note;
	private String senderItemId;
	private String receiver;
	private String phoneNumberOrEmail;
	private String countryCode;
	private String language;

	public PayOrder() {
		super();
	}
	public String getSenderbatchId() {
		return senderbatchId;
	}
	public void setSenderbatchId(String senderbatchId) {
		SimpleDateFormat sdt = mydateformat();
		double rand = this.randValue();
		senderbatchId= Double.toString(rand);
		this.senderbatchId = "Payout_"+senderbatchId+sdt.YEAR_FIELD+sdt.MONTH_FIELD+sdt.DATE_FIELD+sdt.HOUR0_FIELD+sdt.MINUTE_FIELD+sdt.SECOND_FIELD;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSetEmailSubject() {
		return setEmailSubject;
	}
	public void setSetEmailSubject(String setEmailSubject) {
		this.setEmailSubject = setEmailSubject;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	public String getSenderItemId() {
		return senderItemId;
	}
	public void setSenderItemId(String senderItemId) {
		//formating the itemid
		SimpleDateFormat sdt = mydateformat();
		double rand = this.randValue();
	    senderItemId=sdt.YEAR_FIELD+sdt.MONTH_FIELD+sdt.DATE_FIELD+Double.toString(rand);
		this.senderItemId = senderItemId;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getPhoneNumberOrEmail() {
		return phoneNumberOrEmail;
	}
	public void setPhoneNumberOrEmail(String phoneNumberOrEmail) {
		this.phoneNumberOrEmail = phoneNumberOrEmail;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		language ="en-US";
		this.language = language;
	}
	public static SimpleDateFormat mydateformat() {
		Date nowDate = new Date();
		SimpleDateFormat sdt = new SimpleDateFormat("yyyyMMddHHmm");
		return sdt;
	}
	
	public static double randValue() {
		Random random = new Random();
		double rand = random.nextDouble(Maximumlength);
		return rand;
	}
}
