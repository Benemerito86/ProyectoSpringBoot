package com.tuorg.saas_core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "plans")
@Data
@Audited
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name; // Basic, Premium, Enterprise

    @Column(nullable = false, length = 30)
    private String level;

    @Column(name = "price_monthly_cents", nullable = false)
    private Long priceMonthlyCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Boolean active = true;

    // Helper field for backward compatibility with existing code
    @Transient
    private Integer durationDays = 30; // Default 30 days
}
