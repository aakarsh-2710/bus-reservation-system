package com.mcs.booking.model;

public class HttpResponse {

	private Boolean success;
	private String message;
	private Object data;
	private Integer statusCode;

	public HttpResponse(Boolean success, String message, Integer statusCode) {
		this.success = success;
		this.message = message;
		this.statusCode = statusCode;
	}

	public HttpResponse(Boolean success, Object data, Integer statusCode) {
		this.success = success;
		this.data = data;
		this.statusCode = statusCode;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

}
