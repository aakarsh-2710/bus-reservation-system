package com.mcs.admin.service;

import org.springframework.stereotype.Service;

import com.mcs.admin.entity.BusDetail;
import com.mcs.admin.exception.DuplicateResourceException;
import com.mcs.admin.exception.ResourceNotFoundException;
import com.mcs.admin.model.BusDetailDTO;
import com.mcs.admin.repository.BusRepository;

@Service
public class BusService {

	private BusRepository busRepository;

	public BusService(BusRepository busRepository) {
		this.busRepository = busRepository;
	}

	public void saveBusDetail(BusDetailDTO busDetailDTO) {
		BusDetail busDetail = new BusDetail(busDetailDTO.getBusNumber(), busDetailDTO.getSource(),
				busDetailDTO.getDestination(), busDetailDTO.getTotalSeats(), busDetailDTO.getPrice());

		var busData = busRepository.save(busDetail);
		addDataToInventory(busData.getBusId(), busData.getTotalSeats());
	}

	private void addDataToInventory(Long busId, Integer totalSeats) {

		// add data in Inventory service by calling inventory service

	}

	public void deleteBus(String busNumber) {

		busRepository.deleteByBusNumber(busNumber);
	}

	public void updateBusDetail(Long busId, BusDetailDTO busDetailDTO) {
		BusDetail existingBus = busRepository.findById(busId)
				.orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

		// Check if bus number is being changed and if new number already exists
		if (!existingBus.getBusNumber().equals(busDetailDTO.getBusNumber())) {
			if (busRepository.existsByBusNumber(busDetailDTO.getBusNumber())) {
				throw new DuplicateResourceException("Bus number already exists: " + busDetailDTO.getBusNumber());
			}
		}

		// Update fields
		existingBus.setBusNumber(busDetailDTO.getBusNumber());
		existingBus.setSource(busDetailDTO.getSource());
		existingBus.setDestination(busDetailDTO.getDestination());
		existingBus.setTotalSeats(busDetailDTO.getTotalSeats());
		existingBus.setPrice(busDetailDTO.getPrice());

		busRepository.save(existingBus);
	}

}
