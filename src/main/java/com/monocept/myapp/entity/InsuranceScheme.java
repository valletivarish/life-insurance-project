package com.monocept.myapp.entity;

import java.util.List;

import com.monocept.myapp.enums.DocumentType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Entity
@Table(name = "insuranceScheme")
@Data
public class InsuranceScheme {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schemeId")
	private Long schemeId;

	@NotEmpty(message = "Scheme name is required")
	@Column(name = "schemeName")
	private String schemeName;

	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH })
	private List<PolicyAccount> policies;
	
	@ManyToOne
    @JoinColumn(name = "plan_id") 
    private InsurancePlan insurancePlan;

	@Column(name = "isActive")
	private boolean active = true;

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] schemeImage;

	@NotEmpty(message = "Description is required")
	@Lob
	@Column(columnDefinition = "TEXT")
	private String description;

	@PositiveOrZero(message = "Minimum amount must be a non-negative number")
	@Column(name = "minAmount")
	private Double minAmount;

	@PositiveOrZero(message = "Maximum amount must be a non-negative number")
	@Column(name = "maxAmount")
	private Double maxAmount;

	@PositiveOrZero(message = "Minimum policy time must be a non-negative number")
	@Column(name = "minPolicyTerm")
	private int minPolicyTerm;

	@PositiveOrZero(message = "Maximum policy time must be a non-negative number")
	@Column(name = "maxPolicyTerm")
	private Integer maxPolicyTerm;

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
	
	@ElementCollection(targetClass = DocumentType.class)
    @Column(name = "requiredDocuments")
    private List<DocumentType> requiredDocuments;
}
