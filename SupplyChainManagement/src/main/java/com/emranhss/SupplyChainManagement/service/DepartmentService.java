package com.emranhss.SupplyChainManagement.service;


import com.emranhss.SupplyChainManagement.entity.Department;
import com.emranhss.SupplyChainManagement.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository deartmentRepository;


    public List<Department> findAll(){


        return deartmentRepository.findAll();
    }

    public Department  saveOrUpdate(Department d){

      return   deartmentRepository.save(d);

    }

    public Optional<Department> findById(Long id){

        return deartmentRepository.findById(id);
    }


    public void  delete(long id){

        deartmentRepository.deleteById(id);
    }
}

