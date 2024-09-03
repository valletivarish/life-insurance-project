package com.monocept.myapp.entity;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.monocept.myapp.enums.PaymentStatus;
import com.monocept.myapp.enums.PaymentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentId")
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentType")
    private PaymentType paymentType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "paymentDate")
    @CreationTimestamp
    private Date paymentDate;

    @Column(name = "tax")
    private Double tax = 0.0;

    @Column(name = "totalPayment")
    private Double totalPayment = 0.0;

    @Column(name = "cardNumber")
    private String cardNumber;

    @Column(name = "cvv")
    private int cvv = 0;

    @Column(name = "expiry")
    private String expiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentStatus")
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
}
