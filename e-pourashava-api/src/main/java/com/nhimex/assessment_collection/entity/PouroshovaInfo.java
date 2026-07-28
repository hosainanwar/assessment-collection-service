package com.nhimex.assessment_collection.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pouroshova_infos")
public class PouroshovaInfo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pouroshova_name", nullable = false)
    private String pouroshovaName;

    @Column(name = "meyor_name", nullable = false)
    private String meyorName;

    @Column(name = "kor_nirdharok_name")
    private String korNirdharokName;

    @Column(name = "ps_name", nullable = false)
    private String psName;

    @Column(name = "ds_name", nullable = false)
    private String dsName;

    @Column(name = "signature_name", nullable = false)
    private String signatureName;

    @Column(name = "mayor_sign")
    private String mayorSign;

    @Column(name = "assessor_sign")
    private String assessorSign;

    @Column(name = "tax_collector_type")
    private String taxCollectorType;

    @Column(name = "tax_collector_sign")
    private String taxCollectorSign;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "subdomain", nullable = false)
    private String subdomain;

    @Column(name = "mayor_label_type", nullable = false)
    @Builder.Default
    private String mayorLabelType = "mayor";

    @Column(name = "mayor_label_type_collection", nullable = false)
    @Builder.Default
    private String mayorLabelTypeCollection = "mayor";

    @Column(name = "logo")
    private String logo;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "nirdharon_mobile")
    private String nirdharonMobile;
}
