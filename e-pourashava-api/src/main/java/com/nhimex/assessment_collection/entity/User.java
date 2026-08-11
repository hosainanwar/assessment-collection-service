package com.nhimex.assessment_collection.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "department")
    private String department;

    @Column(name = "designation")
    private String designation;

    @Column(name = "address")
    private String address;

    @Column(name = "division")
    private String division;

    @Column(name = "district")
    private String district;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "bio", columnDefinition = "LONGTEXT")
    private String bio;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "subdomain")
    private String subdomain;

    @Column(name = "status")
    @Builder.Default
    private Boolean status = true;

    @Column(name = "role")
    @Builder.Default
    private String role = "USER";
}
