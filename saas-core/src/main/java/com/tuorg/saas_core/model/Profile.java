package com.tuorg.saas_core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "profiles")
@Data
@Audited
public class Profile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(length = 2)
    private String country;
}
