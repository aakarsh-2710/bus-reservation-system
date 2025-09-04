package com.mcs.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcs.admin.model.BusDetailDTO;
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

		busService.saveBusDetail(busDetailDTO);
		return ResponseTemplate.successMsg(MessageConstant.BUS_ADDED);
	}

	@PutMapping("/updateBus/{busId}")
	public ResponseEntity<?> updateBus(@PathVariable Long busId,  @Valid @RequestBody BusDetailDTO busDetailDTO) {

		busService.updateBusDetail(busId,busDetailDTO);
		return ResponseTemplate.successMsg(MessageConstant.BUS_ADDED);
	}

	@DeleteMapping("/deleteBus")
	public ResponseEntity<?> deleteBus(@RequestParam String busNumber) {

		busService.deleteBus(busNumber);
		return ResponseTemplate.successMsg(MessageConstant.BUS_DELETED);
	}

}
