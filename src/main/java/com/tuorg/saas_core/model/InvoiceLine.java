package com.tuorg.saas_core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "invoice_lines")
@Data
@Audited
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "line_type", nullable = false, length = 30)
    private String lineType;

    @Column(nullable = false)
    private String description;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;
}
