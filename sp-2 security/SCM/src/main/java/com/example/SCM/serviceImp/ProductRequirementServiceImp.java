package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.ProductRequirementMapper;
import com.example.SCM.dto.request.ProductRequirementRequestDTO;
import com.example.SCM.dto.response.ProductRequirementResponseDTO;
import com.example.SCM.entity.ProductRequirement;
import com.example.SCM.enumClass.ProductRequestStatus;
import com.example.SCM.repository.ProductRequirementRepository;
import com.example.SCM.service.ProductRequirementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRequirementServiceImp implements ProductRequirementService {

    private final ProductRequirementRepository repository;
    private final ProductRequirementMapper mapper;

    // কিন্তু APPROVED record থাকলে নতুন create করা যাবে — APPROVED record-টি UPDATE করতে পারবে না
    @Override
    @Transactional
    public ProductRequirementResponseDTO save(ProductRequirementRequestDTO dto) {
        if (dto == null) throw new IllegalArgumentException("ProductRequirement data cannot be null");
        ProductRequirement entity = mapper.toEntity(dto);
        ProductRequirement saved = repository.save(entity);
        return mapper.toResponseDTO(saved);
    }

    //  LOGISTICS_OFFICER update করতে পারবে
    // কিন্তু status = APPROVED হলে update block হবে
    @Override
    @Transactional
    public ProductRequirementResponseDTO update(Long id, ProductRequirementRequestDTO dto) {
        ProductRequirement entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductRequirement not found with ID: " + id));

        // APPROVED status হলে LOGISTICS_OFFICER আর update করতে পারবে না
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLogisticsOfficer = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LOGISTICS_OFFICER"));

        if (isLogisticsOfficer && entity.getStatus() == ProductRequestStatus.APPROVED) {
            throw new RuntimeException(
                "Access Denied: This requirement is already APPROVED. Logistics Officer cannot modify an approved requirement."
            );
        }

        mapper.updateEntity(dto, entity);
        ProductRequirement updated = repository.save(entity);
        return mapper.toResponseDTO(updated);
    }

    //  শুধু PROCUREMENT Officer status update করতে পারবে (Controller-level PreAuthorize দিয়ে control)
    @Override
    @Transactional
    public ProductRequirementResponseDTO updateStatus(Long id, String status) {
        ProductRequirement entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductRequirement not found with ID: " + id));

        try {
            entity.setStatus(ProductRequestStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status + ". Allowed: PENDING, APPROVED, REJECTED, PROCESSING");
        }

        ProductRequirement updated = repository.save(entity);
        return mapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRequirementResponseDTO> findAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductRequirementResponseDTO> getById(Long id) {
        return repository.findById(id).map(mapper::toResponseDTO);
    }

    // শুধু ADMIN delete করতে পারবে (Controller-level PreAuthorize দিয়ে control)
    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("ProductRequirement not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
