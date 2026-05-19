package com.emranhss.SupplyChainManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="policeStations")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PoliceStation {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @Column(length = 50)
    private String name;



}
