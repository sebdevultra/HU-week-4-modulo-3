package com.riwi.creditapplication.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for Affiliate.
 * Represents the persistence model for affiliates in the database.
 */
@Entity
@Table(name = "affiliates", indexes = {
        @Index(name = "idx_affiliate_document", columnList = "document_number", unique = true),
        @Index(name = "idx_affiliate_email", columnList = "email"),
        @Index(name = "idx_affiliate_status", columnList = "status")
})
public class AffiliateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "Document number is required")
    @Pattern(regexp = "^[0-9]{8,15}$", message = "Document number must be between 8 and 15 digits")
    @Column(name = "document_number", nullable = false, unique = true, length = 15)
    private String documentNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.01", message = "Salary must be greater than 0")
    @Column(name = "salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal salary;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AffiliateStatus status;

    // Relación OneToMany con CreditApplication
    // Configurada con FetchType.LAZY para optimización
    @OneToMany(mappedBy = "affiliate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CreditApplicationEntity> creditApplications = new ArrayList<>();

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public AffiliateEntity() {
    }

    public AffiliateEntity(String firstName, String lastName, String documentNumber,
            String email, BigDecimal salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.email = email;
        this.salary = salary;
        this.status = AffiliateStatus.ACTIVE;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.registrationDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = AffiliateStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods for bidirectional relationship
    public void addCreditApplication(CreditApplicationEntity creditApplication) {
        creditApplications.add(creditApplication);
        creditApplication.setAffiliate(this);
    }

    public void removeCreditApplication(CreditApplicationEntity creditApplication) {
        creditApplications.remove(creditApplication);
        creditApplication.setAffiliate(null);
    }

    // Business validation methods
    public boolean isActive() {
        return this.status == AffiliateStatus.ACTIVE;
    }

    public boolean hasSufficientSalary(BigDecimal minimumSalary) {
        return this.salary.compareTo(minimumSalary) >= 0;
    }

    public boolean hasValidDocument() {
        return this.documentNumber != null &&
                this.documentNumber.matches("^[0-9]{8,15}$");
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

    public List<CreditApplicationEntity> getCreditApplications() {
        return creditApplications;
    }

    public void setCreditApplications(List<CreditApplicationEntity> creditApplications) {
        this.creditApplications = creditApplications;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Enum for Affiliate Status
    public enum AffiliateStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AffiliateEntity))
            return false;
        AffiliateEntity that = (AffiliateEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AffiliateEntity{" +
                "id=" + id +
                ", documentNumber='" + documentNumber + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
