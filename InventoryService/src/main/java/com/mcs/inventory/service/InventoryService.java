package com.mcs.inventory.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcs.inventory.entity.BusInventory;
import com.mcs.inventory.model.BusAddDTO;
import com.mcs.inventory.model.CancelBookingEvent;
import com.mcs.inventory.model.InventoryEvent;
import com.mcs.inventory.model.PaymentEvent;
import com.mcs.inventory.repository.BusInventoryRepository;

import jakarta.validation.Valid;

@Service
@Transactional
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
		inventoryEvent.setBookingId(event.getBookingId());
		inventoryEvent.setBusId(event.getBusId());

		if ("SUCCESS".equals(event.getStatus())) {
			inv.setAvailableSeats(inv.getAvailableSeats() - event.getSeatsBooked());
			inv.setLastUpdated(LocalDateTime.now());
			repository.save(inv);

			inventoryEvent.setStatus("CONFIRMED");
		} else {
			inventoryEvent.setStatus("REJECTED");
		}
		kafkaTemplate.send("inventory.event", event.getBookingId().toString(), inventoryEvent);
	}

	public void addInventory(@Valid BusAddDTO inventoryDTO) {

		BusInventory busInventory = new BusInventory();
		busInventory.setBusId(inventoryDTO.getBusId());
		busInventory.setAvailableSeats(inventoryDTO.getAvailableSeats());

		busInventory.setLastUpdated(LocalDateTime.now());

		repository.save(busInventory);

	}

	public void processCancelBooking(CancelBookingEvent event) {
		BusInventory inv = repository.findById(event.getBusId())
				.orElseThrow(() -> new RuntimeException("Bus not found"));

		inv.setAvailableSeats(inv.getAvailableSeats() + event.getNoOfSeats());
		inv.setLastUpdated(LocalDateTime.now());
		repository.save(inv);

	}

	public void deleteBus(Integer busId) {

		repository.deleteById(busId);

	}

	public Integer getSeatsAvailable(Integer busId) {
		Optional<BusInventory> busDetail = repository.findById(busId);
		if (busDetail.isPresent()) {
			return busDetail.get().getAvailableSeats();
		}

		return -1;
	}

}
