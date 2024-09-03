package com.monocept.myapp.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Entity
@Table(name = "schemeDetail")
@Data
public class SchemeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detailId")
    private Long detailId;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] schemeImage;

    @NotEmpty(message = "Description is required")
    @Column(name = "description")
    private String description;

    @PositiveOrZero(message = "Minimum amount must be a non-negative number")
    @Column(name = "minAmount")
    private Double minAmount;

    @PositiveOrZero(message = "Maximum amount must be a non-negative number")
    @Column(name = "maxAmount")
    private Double maxAmount;

    @PositiveOrZero(message = "Minimum investment time must be a non-negative number")
    @Column(name = "minInvestmentTime")
    private int minInvestmentTime;

    @PositiveOrZero(message = "Maximum investment time must be a non-negative number")
    @Column(name = "maxInvestmentTime")
    private Integer maxInvestmentTime;

    @PositiveOrZero(message = "Minimum age must be a non-negative number")
    @Column(name = "minAge")
    private int minAge;

    @PositiveOrZero(message = "Maximum age must be a non-negative number")
    @Column(name = "maxAge")
    private Integer maxAge;

    @PositiveOrZero(message = "Profit ratio must be a non-negative number")
    @Column(name = "profitRatio")
    private Double profitRatio;

    @PositiveOrZero(message = "Registration commission ratio must be a non-negative number")
    @Column(name = "registrationCommRatio")
    private Double registrationCommRatio;

    @PositiveOrZero(message = "Installment commission ratio must be a non-negative number")
    @Column(name = "installmentCommRatio")
    private Double installmentCommRatio;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "schemeDetail_document", joinColumns = @JoinColumn(name = "detailId"), inverseJoinColumns = @JoinColumn(name = "documentId"))
    private Set<SchemeDocument> documents = new HashSet<>();
}
