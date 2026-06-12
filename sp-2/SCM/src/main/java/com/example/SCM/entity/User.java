package com.example.SCM.entity;


import com.example.SCM.role.Role;
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

    @Column(unique = true)
   private String phoneNumber;

    @Size(max = 20,min = 4)
    String password;

//    @Size(max = 20,min = 4)
//    String confirmPassword;

    @Enumerated(EnumType.STRING)
    private Role role;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id")
    private PoliceStation policeStation;



}
