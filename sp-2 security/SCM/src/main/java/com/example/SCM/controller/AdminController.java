package com.example.SCM.controller;

import com.example.SCM.dto.request.AdminRequest;
import com.example.SCM.dto.response.AdminResponse;
import com.example.SCM.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponse> create(@RequestBody AdminRequest request) {
        return new ResponseEntity<>(adminService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> update(@PathVariable Long id, @RequestBody AdminRequest request) {
        return ResponseEntity.ok(adminService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAll() {
        return ResponseEntity.ok(adminService.getAll());
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        adminService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}