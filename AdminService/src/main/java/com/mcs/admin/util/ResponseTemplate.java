package com.mcs.admin.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mcs.admin.model.HttpResponse;

// This is a utility class to return {@link ResponseEntity} containing an {@link HttpResponse} 
// with the success status, message, and status code.
public class ResponseTemplate {
	
	private ResponseTemplate() {

	}

	/**
	 * This method creates a successful response for POST, PUT, and DELETE requests.
	 * It returns a message and the HTTP status code `201 CREATED`.
	 *
	 * @param message A custom success message to be included in the response.
	 * @return A {@link ResponseEntity} containing an {@link HttpResponse} with the
	 *         success status, message, and status code.
	 */
	public static ResponseEntity<?> successMsg(String message) {
		HttpResponse httpResponse = new HttpResponse(true, message, HttpStatus.CREATED.value());
		return new ResponseEntity<>(httpResponse, HttpStatus.CREATED);

	}

	/**
	 * This method creates a successful response for a GET request, including the
	 * requested data. It returns the data and the HTTP status code `200 OK`.
	 *
	 * @param data The data object to be returned in the response body.
	 * @return A {@link ResponseEntity} containing an {@link HttpResponse} with the
	 *         success status, data, and status code.
	 */
	public static ResponseEntity<?> successMsg(Object data) {
		HttpResponse httpResponse = new HttpResponse(true, data, HttpStatus.OK.value());
		return new ResponseEntity<>(httpResponse, HttpStatus.OK);

	}

	/**
	 * This method creates a custom error response for a request. It's designed to
	 * return a specific error message with a provided HTTP status code of `202
	 * ACCEPTED`. This method should be used for scenarios where an error occurs
	 *
	 * @param message The error message to be included in the response.
	 * @return A {@link ResponseEntity} containing an {@link HttpResponse} with a
	 *         failure status, the error message, and a status code of 202.
	 */
	public static ResponseEntity<?> errorMsg(String message) {

		HttpResponse httpResponse = new HttpResponse(false, message, HttpStatus.ACCEPTED.value());

		return new ResponseEntity<>(httpResponse, HttpStatus.ACCEPTED);

	}

}
