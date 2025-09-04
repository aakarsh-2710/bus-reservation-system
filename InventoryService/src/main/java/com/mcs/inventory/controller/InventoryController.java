package com.mcs.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcs.inventory.model.BusInventoryDTO;
import com.mcs.inventory.service.InventoryService;
import com.mcs.inventory.util.MessageConstant;
import com.mcs.inventory.util.ResponseTemplate;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	private InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@GetMapping("/seats")
	public ResponseEntity<?> getAvailableSeats(@RequestParam Integer busId) {

		Integer availableSeats = inventoryService.getAvailableSeats(busId);
		return ResponseTemplate.successMsg(availableSeats);
	}

	@PostMapping("/add")
	public ResponseEntity<?> addBusInInventory(@Valid @RequestBody BusInventoryDTO inventoryDTO) {

		inventoryService.addInventory(inventoryDTO);
		return ResponseTemplate.successMsg(MessageConstant.INVENTORY_ADDED);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateBusInInventory(@Valid @RequestBody BusInventoryDTO inventoryDTO) {

		inventoryService.updateInventory(inventoryDTO);
		return ResponseTemplate.successMsg(MessageConstant.INVENTORY_ADDED);
	}

}
