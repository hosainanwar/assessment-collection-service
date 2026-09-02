package com.nhimex.assessment_collection.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "words")
@Filter(name = TenantFilters.TENANT, condition = TenantFilters.CONDITION)
public class Word extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word_name", nullable = false)
    private String wordName;

    @Column(name = "subdomain", nullable = false)
    private String subdomain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pourashava_id", nullable = false)
    private Pourashava pourashava;

    @Column(name = "created_by")
    private String createdBy;
}
