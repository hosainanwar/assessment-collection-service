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
@Table(name = "paras")
@Filter(name = TenantFilters.TENANT, condition = TenantFilters.CONDITION)
public class Para extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pbr_name", nullable = false)
    private String pbrName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", foreignKey = @ForeignKey(name = "fk_paras_words_word_id"))
    private Word word;

    @Column(name = "word_id", insertable = false, updatable = false, nullable = false)
    private Long wordId;

    @Column(name = "subdomain", nullable = false)
    private String subdomain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pourashava_id", nullable = false)
    private Pourashava pourashava;

    @Column(name = "created_by")
    private String createdBy;
}
