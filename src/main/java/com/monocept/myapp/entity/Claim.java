package com.monocept.myapp.entity;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "claim")
@Data
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claimId")
    private Long claimId;

    @OneToOne
    @JoinColumn(name = "policyNo")
    private InsurancePolicy policy;

    @Column(name = "claimAmount")
    private double claimAmount;

    @Column(name = "bankName")
    private String bankName;

    @Column(name = "branchName")
    private String branchName;

    @Column(name = "bankAccountNumber")
    private String bankAccountNumber;

    @Column(name = "ifscCode")
    private String ifscCode;

    @Column(name = "claimDate")
    @CreationTimestamp
    private Date date;

    @Column(name = "claimStatus")
    private String status;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "agentId", referencedColumnName = "agentId")
    private Agent agent;
}
