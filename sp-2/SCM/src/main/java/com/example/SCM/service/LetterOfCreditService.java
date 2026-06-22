package com.example.SCM.service;

import com.example.SCM.dto.request.LetterOfCreditRequestDTO;
import com.example.SCM.dto.response.LetterOfCreditResponseDTO;
import java.util.List;
import java.util.Optional;

public interface LetterOfCreditService {
    LetterOfCreditResponseDTO save(LetterOfCreditRequestDTO dto);
    LetterOfCreditResponseDTO update(Long id, LetterOfCreditRequestDTO dto);
    LetterOfCreditResponseDTO amendLC(Long id, LetterOfCreditRequestDTO dto);
    List<LetterOfCreditResponseDTO> findAll();
    Optional<LetterOfCreditResponseDTO> getById(Long id);
    void delete(Long id);
    Optional<LetterOfCreditResponseDTO> getByLcNumber(String lcNumber);
}