package com.mcs.inventory.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcs.inventory.model.BusInventoryDTO;
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

	// TO BE CALLED BY ADMIN-SERVICE
	@PostMapping("/add")
	public void addBusInInventory(@Valid @RequestBody BusInventoryDTO inventoryDTO) {

		inventoryService.addInventory(inventoryDTO);
	}

	// TO BE CALLED BY ADMIN-SERVICE
	@DeleteMapping("/delete/{busId}")
	public void deleteBusInInventory(@PathVariable Integer busId) {

		inventoryService.deleteBus(busId);
	}
}
