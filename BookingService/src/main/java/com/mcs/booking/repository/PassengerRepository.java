package com.mcs.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcs.booking.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

}
