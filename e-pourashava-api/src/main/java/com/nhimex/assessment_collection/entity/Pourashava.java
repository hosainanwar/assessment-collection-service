package com.nhimex.assessment_collection.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pourashavas")
public class Pourashava extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "subdomain", unique = true, nullable = false)
    private String subdomain;

    @Column(name = "bn_name", nullable = false)
    private String bnName;

    @Column(name = "en_name", nullable = false)
    private String enName;

    @Column(name = "features", columnDefinition = "json")
    private String features;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "ip_address", length = 45, nullable = false)
    private String ipAddress;
}
