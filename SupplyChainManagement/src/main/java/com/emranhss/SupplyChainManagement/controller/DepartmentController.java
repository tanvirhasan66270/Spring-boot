package com.emranhss.SupplyChainManagement.controller;

import com.emranhss.SupplyChainManagement.entity.Department;
import com.emranhss.SupplyChainManagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {


    @Autowired
    private DepartmentService departmentService;


   @PostMapping
   public ResponseEntity<Department> save(@RequestBody Department d){
     Department saveDepartment=departmentService.saveOrUpdate(d);
     return new ResponseEntity<>(  saveDepartment, HttpStatus.CREATED);

   }

  
@GetMapping
public ResponseEntity< List<Department>> findAll(){
       List<Department> departments=departmentService.findAll();
       return  ResponseEntity.ok(departments);
}

//@DeleteMapping
//public ResponseEntity<Department> delete(@RequestBody long id) {
//
//       departmentService.delete(id);
//       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//}










}
