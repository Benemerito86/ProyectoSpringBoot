package com.tuorg.saas_core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.time.OffsetDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@Audited
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "current_plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column(name = "billing_anchor_at", nullable = false)
    private OffsetDateTime billingAnchorAt;

    @Column(name = "next_billing_at", nullable = false)
    private OffsetDateTime nextBillingAt;

    @Column(name = "canceled_at")
    private OffsetDateTime canceledAt;
}
