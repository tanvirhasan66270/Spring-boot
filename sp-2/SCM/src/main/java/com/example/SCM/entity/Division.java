package com.example.SCM.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="division")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Division {


    @Id
     @GeneratedValue(strategy= GenerationType.IDENTITY)
   Long id;

    @Column(unique=true)
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;


}
