package com.nhimex.assessment_collection.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "paras")
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

    @Column(name = "created_by")
    private String createdBy;
}
