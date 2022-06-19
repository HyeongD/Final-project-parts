package net.dheyeong.tutorials.springbootbackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RessourceNotFounfException extends RuntimeException {
	private String resourceName;
	private String fieldName;
	private Object fieldValue;
	
	public RessourceNotFounfException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s not found with %s : '%s'",resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public String getResourceName() {
		return resourceName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}
	
	
}
