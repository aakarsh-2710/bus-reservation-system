package com.mcs.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcs.admin.entity.BusDetail;

@Repository
public interface BusRepository extends JpaRepository<BusDetail, Integer> {

	boolean existsByBusNumber(String busNumber);

}
