package com.mcs.admin.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.admin.entity.BusDetail;
import com.mcs.admin.exception.DuplicateResourceException;
import com.mcs.admin.exception.ResourceNotFoundException;
import com.mcs.admin.model.BusAddDTO;
import com.mcs.admin.model.BusDeleteDTO;
import com.mcs.admin.model.BusDetailDTO;
import com.mcs.admin.repository.BusRepository;
import com.mcs.admin.util.MessageConstant;

@Service
@Transactional
public class BusService {

	private BusRepository busRepository;
	private KafkaTemplate<String, String> kafkaTemplate;
	private ObjectMapper objectMapper;

	public BusService(BusRepository busRepository, KafkaTemplate<String, String> kafkaTemplate,
			ObjectMapper objectMapper) {
		this.busRepository = busRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void saveBusDetail(BusDetailDTO busDetailDTO) {
		BusDetail busDetail = new BusDetail(busDetailDTO.getBusNumber(), busDetailDTO.getSource(),
				busDetailDTO.getDestination(), busDetailDTO.getTotalSeats(), busDetailDTO.getPrice());

		var busData = busRepository.save(busDetail);

		// publish add event to kafka
		publishAddEventToKafka(busData);

	}

	private void publishAddEventToKafka(BusDetail busData) {
		BusAddDTO event = new BusAddDTO(busData.getBusId(), busData.getTotalSeats());
		try {
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("bus.add.event", busData.getBusId().toString(), payload);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize event", e);
		}

	}

	public void deleteBus(Integer busId) {

		BusDetail existingBus = busRepository.findById(busId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(MessageConstant.BUS_NOT_FOUND, busId)));

		busRepository.deleteById(existingBus.getBusId());

		// publish delete event to kafka
		publishDeleteEventToKafka(existingBus);
	}

	private void publishDeleteEventToKafka(BusDetail existingBus) {
		BusDeleteDTO event = new BusDeleteDTO(existingBus.getBusId());
		try {
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("bus.delete.event", event.getBusId().toString(), payload);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize event", e);
		}

	}

	// We can not update number of seats in the bus. Hence inventory requires no
	// change
	public void updateBusDetail(Integer busId, BusDetailDTO busDetailDTO) {
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
