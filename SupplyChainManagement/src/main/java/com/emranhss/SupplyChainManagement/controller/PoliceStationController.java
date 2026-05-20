package com.emranhss.SupplyChainManagement.controller;


import com.emranhss.SupplyChainManagement.entity.PoliceStation;
import com.emranhss.SupplyChainManagement.service.PoliceStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policeStation/")
public class PoliceStationController {

    @Autowired
    private PoliceStationService policeStationService;

    @PostMapping
    public void save(@RequestBody PoliceStation p) {

        policeStationService.saveOrUpdate(p);
    }

    @GetMapping
//    public List<PoliceStation> getAll() {
//
//        return policeStationService.getAll();
//    }

    public  ResponseEntity<List<PoliceStation>> getAll() {


        List<PoliceStation> policeStations = policeStationService.getAll();
        return ResponseEntity.ok(policeStations);
    }


}
