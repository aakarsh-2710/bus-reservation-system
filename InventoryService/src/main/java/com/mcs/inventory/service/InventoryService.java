package com.mcs.inventory.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mcs.inventory.entity.BusInventory;
import com.mcs.inventory.model.BusInventoryDTO;
import com.mcs.inventory.model.CancelBookingEvent;
import com.mcs.inventory.model.InventoryEvent;
import com.mcs.inventory.model.PaymentEvent;
import com.mcs.inventory.repository.BusInventoryRepository;

import jakarta.validation.Valid;

@Service
public class InventoryService {

	private final BusInventoryRepository repository;
	private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

	public InventoryService(BusInventoryRepository repository, KafkaTemplate<String, InventoryEvent> kafkaTemplate) {
		this.repository = repository;
		this.kafkaTemplate = kafkaTemplate;
	}

	public void processPaymentEvent(PaymentEvent event) {
		BusInventory inv = repository.findById(event.getBusId())
				.orElseThrow(() -> new RuntimeException("Bus not found"));

		InventoryEvent inventoryEvent = new InventoryEvent();
		inventoryEvent.setBookingNo(event.getBookingNo());
		inventoryEvent.setBusId(event.getBusId());

		if ("SUCCESS".equals(event.getStatus()) && inv.getAvailableSeats() >= event.getSeatsBooked()) {
			inv.setAvailableSeats(inv.getAvailableSeats() - event.getSeatsBooked());
			inv.setLastUpdated(LocalDateTime.now());
			repository.save(inv);

			inventoryEvent.setStatus("CONFIRMED");
		} else {
			inventoryEvent.setStatus("REJECTED");
		}

		kafkaTemplate.send("inventory-events", inventoryEvent);
	}

	public Integer getAvailableSeats(Integer busID) {

		Optional<BusInventory> busDetail = repository.findById(busID);
		if (busDetail.isPresent()) {
			return busDetail.get().getAvailableSeats();
		}

		return -1;
	}

	public void addInventory(@Valid BusInventoryDTO inventoryDTO) {
//		BusInventory busInventory = repository.findById(inventoryDTO.getBusId())
//				.orElseThrow(() -> new RuntimeException("Bus not found"));

		BusInventory busInventory = new BusInventory();
		busInventory.setBusId(inventoryDTO.getBusId());
		busInventory.setAvailableSeats(inventoryDTO.getAvailableSeats());

		LocalDateTime dateTime = LocalDateTime.now();
		busInventory.setLastUpdated(dateTime);

		repository.save(busInventory);

	}

	public void updateInventory(@Valid BusInventoryDTO inventoryDTO) {
		BusInventory busInventory = repository.findById(inventoryDTO.getBusId())
				.orElseThrow(() -> new RuntimeException("Bus not found"));

		busInventory.setAvailableSeats(inventoryDTO.getAvailableSeats());

		LocalDateTime dateTime = LocalDateTime.now();
		busInventory.setLastUpdated(dateTime);

		repository.save(busInventory);

	}

	public void processCancelBooking(CancelBookingEvent event) {
	    BusInventory inv = repository.findById(event.getBusId())
	            .orElseThrow(() -> new RuntimeException("Bus not found"));

	    inv.setAvailableSeats(inv.getAvailableSeats() + event.getNoOfSeats());
	    inv.setLastUpdated(LocalDateTime.now());
	    repository.save(inv);

	    // notify booking service
	    InventoryEvent inventoryEvent = new InventoryEvent();
	    inventoryEvent.setBookingNo(event.getBookingNo());
	    inventoryEvent.setBusId(event.getBusId());
	    inventoryEvent.setStatus("CANCELLED");

	    kafkaTemplate.send("inventory-events", inventoryEvent);
	}

}
