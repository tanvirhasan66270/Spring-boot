package com.example.SCM.controller;

import com.example.SCM.dto.request.DeliveryTripRequestDTO;
import com.example.SCM.dto.response.DeliveryTripResponseDTO;
import com.example.SCM.service.DeliveryTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryTripController {

    private final DeliveryTripService tripService;

    @PostMapping
    public ResponseEntity<DeliveryTripResponseDTO> create(@RequestBody DeliveryTripRequestDTO dto) {
        return new ResponseEntity<>(
                tripService.save(dto),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryTripResponseDTO> update(
            @PathVariable Long id,
            @RequestBody DeliveryTripRequestDTO dto
    ) {
        return ResponseEntity.ok(tripService.update(id, dto));
    }

    @PatchMapping(value = "/{id}/status", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DeliveryTripResponseDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @RequestPart(value = "signature", required = false) MultipartFile signature,
            @RequestPart(value = "photo", required = false) MultipartFile photo // 🎯 ফ্রন্টএন্ড থেকে FormData-তে 'photo' নামে ফাইল পুশ করতে হবে
    ) {
        // সার্ভিস লেয়ারের মেথড সিগনেচারের সাথে ভেরিয়েবল পাসিং সিঙ্কড
        return ResponseEntity.ok(tripService.updateTripStatus(id, status, signature, photo));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryTripResponseDTO>> getAll() {
        List<DeliveryTripResponseDTO> list = tripService.findAll();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryTripResponseDTO> getById(@PathVariable Long id) {
        return tripService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        tripService.delete(id);
        return ResponseEntity.ok("Delivery trip cluster index cleared successfully from control matrix.");
    }
}