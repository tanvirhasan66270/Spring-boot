package com.example.SCM.service;

import com.example.SCM.dto.request.InvoiceRequestDTO;
import com.example.SCM.dto.response.InvoiceResponseDTO;
import com.example.SCM.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    InvoiceResponseDTO save(InvoiceRequestDTO dto);
    InvoiceResponseDTO update(Long id, InvoiceRequestDTO dto);
    List<InvoiceResponseDTO> findAll();
    Optional<InvoiceResponseDTO> getById(Long id);
    void delete(Long id);
    InvoiceResponseDTO getByOrderNumberOrId(String query);

}