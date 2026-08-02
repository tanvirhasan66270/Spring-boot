package com.example.SCM.controller;

import com.example.SCM.dto.request.DeliveryTripRequestDTO;
import com.example.SCM.dto.response.DeliveryTripResponseDTO;
import com.example.SCM.service.DeliveryTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-trips")
@RequiredArgsConstructor
public class DeliveryTripController {

    private final DeliveryTripService tripService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'DRIVER')")
    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<DeliveryTripResponseDTO> create(@RequestBody DeliveryTripRequestDTO dto) {
        return new ResponseEntity<>(
                tripService.save(dto),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'DRIVER')")
    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<DeliveryTripResponseDTO> update(
            @PathVariable Long id,
            @RequestBody DeliveryTripRequestDTO dto
    ) {
        return ResponseEntity.ok(tripService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'DRIVER')")
    @PatchMapping(value = "/{id}/status", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'DRIVER')")
    public ResponseEntity<DeliveryTripResponseDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @RequestPart(value = "signature", required = false) MultipartFile signature,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        // সার্ভিস লেয়ারের মেথড সিগনেচারের সাথে ভেরিয়েবল পাসিং সিঙ্কড
        return ResponseEntity.ok(tripService.updateTripStatus(id, status, signature, photo));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'DRIVER')")
    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'COMMERCIAL_OFFICER')")
    public ResponseEntity<List<DeliveryTripResponseDTO>> getAll() {
        List<DeliveryTripResponseDTO> list = tripService.findAll();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'DRIVER')")
    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'COMMERCIAL_OFFICER', 'DRIVER')")
    public ResponseEntity<DeliveryTripResponseDTO> getById(@PathVariable Long id) {
        return tripService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'LOGISTICS_OFFICER', 'DRIVER')")
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        tripService.delete(id);
        return ResponseEntity.ok("Delivery trip cluster index cleared successfully from control matrix.");
    }
}