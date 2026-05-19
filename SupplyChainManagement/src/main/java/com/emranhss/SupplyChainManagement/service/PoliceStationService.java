package com.emranhss.SupplyChainManagement.service;

import com.emranhss.SupplyChainManagement.entity.PoliceStation;
import com.emranhss.SupplyChainManagement.repository.PoliceStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PoliceStationService  {

    @Autowired
    private PoliceStationRepository stationRepository;


    public List<PoliceStation> getAll(){
        return stationRepository.findAll();
    }
    public void saveOrUpdate(PoliceStation p){
        stationRepository.save(p);
    }
    public Optional<PoliceStation>getById(Long id){
        return stationRepository.findById(id);
    }
    public void delete(PoliceStation p){
        stationRepository.delete(p);
    }

}
