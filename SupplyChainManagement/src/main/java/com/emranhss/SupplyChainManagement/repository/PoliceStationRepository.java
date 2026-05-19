package com.emranhss.SupplyChainManagement.repository;


import com.emranhss.SupplyChainManagement.entity.PoliceStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoliceStationRepository extends JpaRepository<PoliceStation, Long> {



}
