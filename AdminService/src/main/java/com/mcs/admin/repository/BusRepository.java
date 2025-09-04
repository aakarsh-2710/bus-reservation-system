package com.mcs.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcs.admin.entity.BusDetail;

@Repository
public interface BusRepository extends JpaRepository<BusDetail, Long> {

//	@Modifying
//	@Query(value = "delete from Book where isbn =:isbn", nativeQuery = true)
	void deleteByBusNumber(String busNumber);

	boolean existsByBusNumber(String busNumber);

}
