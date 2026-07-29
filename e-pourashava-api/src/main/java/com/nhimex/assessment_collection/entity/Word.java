package com.nhimex.assessment_collection.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "words")
public class Word extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word_name", nullable = false)
    private String wordName;

    @Column(name = "subdomain", nullable = false)
    private String subdomain;

    @Column(name = "created_by")
    private String createdBy;
}
