package com.emranhss.SupplyChainManagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Length;
import org.springframework.web.bind.annotation.RestController;


@Entity
@Table(name ="deparments")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Department {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @Column(length =50)
    private String name;



}
