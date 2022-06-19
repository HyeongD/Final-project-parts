package com.dheyeong.spring.paypal.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// this class is you to get the data passed form the home.html

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
	private double price;
	private String currency;
	private String method;
	private String intent;
	private String description;
	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @return the intent
	 */
	public String getIntent() {
		return intent;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @param intent the intent to set
	 */
	public void setIntent(String intent) {
		this.intent = intent;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	


}
