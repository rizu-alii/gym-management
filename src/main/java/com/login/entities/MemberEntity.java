package com.login.entities;


//import com.login.services.Unique;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity users;

    //    @Unique(entity = MemberEntity.class, field = "email", message = "Email must be unique")
    private String email;

    @JoinColumn(name = "date_of_birth")
    @NotNull(message = "Date of birth cannot be null")
    private LocalDateTime dateOfBirth;

    @NotNull(message = "Gender cannot be null")
    private String gender;
    @Column(unique = true)
    @NotNull(message = "Phone number cannot be null")
//    @Unique(entity = MemberEntity.class, field = "email", message = "Email must be unique")

    private String phone;

    private String address;

    @NotNull(message = "Fee Status cannot be null")
    private String feeStatus ="Unpaid" ;

    private String membership_type;

    private String blood_group;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    private boolean isDeleted = false; // New field for soft delete
}
