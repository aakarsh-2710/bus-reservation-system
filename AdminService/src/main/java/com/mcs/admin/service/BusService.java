package com.mcs.admin.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.mcs.admin.entity.BusDetail;
import com.mcs.admin.exception.DuplicateResourceException;
import com.mcs.admin.exception.ResourceNotFoundException;
import com.mcs.admin.model.BusDetailDTO;
import com.mcs.admin.model.BusInventoryDTO;
import com.mcs.admin.model.UpdateBusDetailDTO;
import com.mcs.admin.repository.BusRepository;
import com.mcs.admin.util.MessageConstant;

@Service
@Transactional
public class BusService {

	private BusRepository busRepository;
	private RestTemplate restTemplate;

	public BusService(BusRepository busRepository, RestTemplate restTemplate) {
		this.busRepository = busRepository;
		this.restTemplate = restTemplate;
	}

	public void saveBusDetail(BusDetailDTO busDetailDTO) {
		BusDetail busDetail = new BusDetail(busDetailDTO.getBusNumber(), busDetailDTO.getSource(),
				busDetailDTO.getDestination(), busDetailDTO.getTotalSeats(), busDetailDTO.getPrice());

		var busData = busRepository.save(busDetail);
		// updating bus info in bus inventory
		addDataToInventory(busData.getBusId(), busData.getTotalSeats());
	}

	private void addDataToInventory(Integer busId, Integer totalSeats) {

		BusInventoryDTO busInventoryDTO = new BusInventoryDTO(busId, totalSeats, LocalDateTime.now());

		restTemplate.postForObject("http://INVENTORY-SERVICE/inventory/add", busInventoryDTO, Object.class);

	}

	public void deleteBus(Integer busId) {

		BusDetail existingBus = busRepository.findById(busId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(MessageConstant.BUS_NOT_FOUND, busId)));

		busRepository.deleteById(existingBus.getBusId());
		deleteFromInventory(existingBus.getBusId());
	}

	private void deleteFromInventory(Integer busId) {
		String url = "http://INVENTORY-SERVICE/inventory/delete/" + busId;
		restTemplate.delete(url);
	}

	// We can not update number of seats in the bus. Hence inventory requires no
	// change
	public void updateBusDetail(Integer busId, UpdateBusDetailDTO busDetailDTO) {
		BusDetail existingBus = busRepository.findById(busId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(MessageConstant.BUS_NOT_FOUND, busId)));

		// Check if bus number is being changed and if new number already exists
		if (!existingBus.getBusNumber().equals(busDetailDTO.getBusNumber())
				&& busRepository.existsByBusNumber(busDetailDTO.getBusNumber())) {
			throw new DuplicateResourceException(String.format(MessageConstant.DUPLICATE_BUS_NUMBER, busId));
		}

		// Update fields
		existingBus.setBusNumber(busDetailDTO.getBusNumber());
		existingBus.setSource(busDetailDTO.getSource());
		existingBus.setDestination(busDetailDTO.getDestination());
		existingBus.setPrice(busDetailDTO.getPrice());

		busRepository.save(existingBus);
	}

	public List<?> getAllBusDetails() {

		return busRepository.findAll();
	}

	public Object getBusById(Integer busId) {

		return busRepository.findById(busId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(MessageConstant.BUS_NOT_FOUND, busId)));

	}

}
