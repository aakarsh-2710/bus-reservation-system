package com.mcs.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcs.inventory.model.BusAddDTO;
import com.mcs.inventory.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	private InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	// TO BE CALLED BY BOOKING-SERVICE
	@GetMapping("/getAvailableSeats/{busId}")
	public Integer getAvailableSeats(@PathVariable Integer busId) {

		return inventoryService.getSeatsAvailable(busId);
	}

	// TO BE CALLED BY BOOKING-SERVICE
	@PutMapping("/update")
	public void updateBusInventory(@Valid @RequestBody BusAddDTO inventoryDTO) {

		inventoryService.addInventory(inventoryDTO);
	}
}
