package com.riwi.creditapplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing an Affiliate in the credit application system.
 * This is a core business entity with no framework dependencies.
 */
public class Affiliate {

    private Long id;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String email;
    private String phoneNumber;
    private BigDecimal salary;
    private LocalDateTime registrationDate;
    private AffiliateStatus status;

    public Affiliate() {
    }

    public Affiliate(Long id, String firstName, String lastName, String documentNumber,
            String email, String phoneNumber, LocalDateTime registrationDate,
            AffiliateStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public AffiliateStatus getStatus() {
        return status;
    }

    public void setStatus(AffiliateStatus status) {
        this.status = status;
    }

    public enum AffiliateStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }
}
