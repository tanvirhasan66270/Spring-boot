package com.example.SCM.service;

import com.example.SCM.entity.Country;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CountryService {

    Country save(Country c);
    List<Country> findAll();
    Optional<Country> getById(Long id);
    void delete(Long id);
}
