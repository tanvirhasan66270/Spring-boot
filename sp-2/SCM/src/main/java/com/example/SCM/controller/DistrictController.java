package com.example.SCM.controller;


import com.example.SCM.dto.response.DistrictResponseDTO;
import com.example.SCM.entity.District;
import com.example.SCM.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/district/")
public class DistrictController {


    @Autowired
    private DistrictService districtService;


    @PostMapping
    public ResponseEntity<District> save(@RequestBody District d) {
        District saveD = districtService.save(d);
        return ResponseEntity.ok(saveD);
    }

    @GetMapping
    public ResponseEntity<List<District>> all() {
        List<District> list = districtService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("{id}")
    public ResponseEntity<List<DistrictResponseDTO>> getByDivisionId(@PathVariable Long id) {
        List<DistrictResponseDTO> list = districtService.findByDivisionId(id);
        return ResponseEntity.ok(list);
    }

    @GetMapping("division/{name}")
    public ResponseEntity<List<DistrictResponseDTO>> getByDivisionName(@PathVariable String name) {
        List<DistrictResponseDTO> list = districtService.findByDivisionName(name);
        return ResponseEntity.ok(list);
    }

    @PutMapping("{id}")
    public  ResponseEntity<District> update(
            @PathVariable Long id,
            @RequestBody District d
    ){

        d.setId(id);
        District updatedDistrict = districtService.save(d);

        return  ResponseEntity.ok(updatedDistrict);

    }

}
