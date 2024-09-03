package com.monocept.myapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "query")
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queryId")
    private Long queryId;

    @NotEmpty(message = "Title is required")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Message is required")
    @Column(name = "message")
    private String message;

    @Column(name = "response")
    private String response;

    @Column(name = "isResolved")
    private boolean resolved = false;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "customerId", referencedColumnName = "customerId")
    private Customer customer;
}
