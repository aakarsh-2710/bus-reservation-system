package com.mcs.booking.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// This is a utility class to return {@link ResponseEntity} containing an {@link HttpResponse} 
// with the success status, message, and status code.
public class ResponseTemplate {

	private ResponseTemplate() {

	}

	public static ResponseEntity<?> successMsg(String message) {
		HttpResponse httpResponse = new HttpResponse(true, message, HttpStatus.CREATED.value());
		return new ResponseEntity<>(httpResponse, HttpStatus.CREATED);

	}

	public static ResponseEntity<?> successMsg(Object data) {
		HttpResponse httpResponse = new HttpResponse(true, data, HttpStatus.OK.value());
		return new ResponseEntity<>(httpResponse, HttpStatus.OK);

	}

	public static ResponseEntity<?> errorMsg(String message, HttpStatus httpStatus) {

		HttpResponse httpResponse = new HttpResponse(false, message, httpStatus.value());

		return new ResponseEntity<>(httpResponse, httpStatus);

	}

}
