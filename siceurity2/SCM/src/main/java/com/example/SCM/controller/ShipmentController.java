package com.example.SCM.controller;

import com.example.SCM.dto.request.ShipmentRequestDTO;
import com.example.SCM.dto.response.ShipmentResponseDTO;
import com.example.SCM.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShipmentResponseDTO> create(
            @RequestPart("shipment") String shipmentJson,
            @RequestPart(value = "podFile", required = false) MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ShipmentRequestDTO dto = mapper.readValue(shipmentJson, ShipmentRequestDTO.class);
        return new ResponseEntity<>(shipmentService.save(dto, file), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShipmentResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("shipment") String shipmentJson,
            @RequestPart(value = "podFile", required = false) MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ShipmentRequestDTO dto = mapper.readValue(shipmentJson, ShipmentRequestDTO.class);
        return ResponseEntity.ok(shipmentService.update(id, dto, file));
    }

    @GetMapping
    public ResponseEntity<List<ShipmentResponseDTO>> getAll() {
        List<ShipmentResponseDTO> list = shipmentService.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDTO> getById(@PathVariable Long id) {
        return shipmentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        shipmentService.delete(id);
        return ResponseEntity.ok("Shipment record successfully removed from enterprise cluster");
    }
}