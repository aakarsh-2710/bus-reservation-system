package com.mcs.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcs.inventory.entity.BusInventory;

@Repository
public interface BusInventoryRepository extends JpaRepository<BusInventory, Integer> {

}
