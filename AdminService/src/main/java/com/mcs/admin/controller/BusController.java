package com.mcs.admin.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcs.admin.exception.DuplicateResourceException;
import com.mcs.admin.exception.ResourceNotFoundException;
import com.mcs.admin.model.BusDetailDTO;
import com.mcs.admin.model.UpdateBusDetailDTO;
import com.mcs.admin.service.BusService;
import com.mcs.admin.util.MessageConstant;
import com.mcs.admin.util.ResponseTemplate;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class BusController {

	private BusService busService;

	public BusController(BusService busService) {
		this.busService = busService;
	}

	@PostMapping("/addBus")
	public ResponseEntity<?> addBus(@Valid @RequestBody BusDetailDTO busDetailDTO) {

		try {
			busService.saveBusDetail(busDetailDTO);
		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseTemplate.successMsg(MessageConstant.BUS_ADDED);
	}

	@PutMapping("/updateBus/{busId}")
	public ResponseEntity<?> updateBus(@PathVariable Integer busId, @Valid @RequestBody UpdateBusDetailDTO busDetailDTO) {

		try {
			busService.updateBusDetail(busId, busDetailDTO);

		} catch (ResourceNotFoundException e) {
			return ResponseTemplate.errorMsg(e.getMessage(), HttpStatus.NOT_FOUND);

		} catch (DuplicateResourceException e) {
			return ResponseTemplate.errorMsg(e.getMessage(), HttpStatus.CONFLICT);

		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseTemplate.successMsg(MessageConstant.BUS_UPDATED);
	}

	@DeleteMapping("/deleteBus/{busId}")
	public ResponseEntity<?> deleteBus(@PathVariable Integer busId) {
		try {
			busService.deleteBus(busId);

		} catch (ResourceNotFoundException e) {
			return ResponseTemplate.errorMsg(e.getMessage(), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseTemplate.successMsg(MessageConstant.BUS_DELETED);
	}

	@GetMapping("/getAllBus")
	public ResponseEntity<?> getAllBus() {
		List<?> allBusDetails = new ArrayList<>();
		try {
			allBusDetails = busService.getAllBusDetails();
		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseTemplate.successMsg(allBusDetails);
	}

	@GetMapping("/getBus/{busId}")
	public ResponseEntity<?> getAllBus(@PathVariable Integer busId) {
		Object busDetail = new Object();
		try {
			busDetail = busService.getBusById(busId);
		} catch (ResourceNotFoundException e) {
			return ResponseTemplate.errorMsg(e.getMessage(), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseTemplate.successMsg(busDetail);
	}

}
