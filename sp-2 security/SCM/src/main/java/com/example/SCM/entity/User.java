package com.example.SCM.entity;


import com.example.SCM.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String name;

    @Column(unique = true)
    private String email;

    private boolean active;

    @Column(unique = true)
   private String phoneNumber;

    @Size(max = 20,min = 4)
    String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id")
    private PoliceStation policeStation;







}
