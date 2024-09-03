package com.monocept.myapp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.monocept.myapp.enums.PolicyStatus;
import com.monocept.myapp.enums.PremiumType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "insurancePolicies")
@Data
public class InsurancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policyNo")
    private Long policyNo;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "schemeId", referencedColumnName = "schemeId")
    private InsuranceScheme insuranceScheme;

    @Column(name = "issueDate")
    @CreationTimestamp
    private Date issueDate;

    @Column(name = "maturityDate")
    private Date maturityDate;

    @Column(name = "premiumType")
    private PremiumType premiumType;

    @Column(name = "sumAssured")
    private Double sumAssured;

    @Column(name = "premiumAmount")
    private Double premiumAmount;

    @Column(name = "status")
    private PolicyStatus status = PolicyStatus.PENDING;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "agentId", referencedColumnName = "agentId")
    private Agent agent;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    private List<Nominee> nominees;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    private List<Payment> payments;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "claim")
    private Claim claims;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    private Set<SubmittedDocument> submittedDocuments = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "tax_id", referencedColumnName = "taxId")
    private TaxSetting taxSetting;
    
    @ManyToOne
    private InsuranceSetting insuranceSetting;
}
