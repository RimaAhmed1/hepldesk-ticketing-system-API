package com.helpdesk.ticketing.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sla_rule")
@Getter
@Setter
@NoArgsConstructor
public class SlaRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slarule_id")
    private Long slaRuleId;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "target_hours", nullable = false)
    private Integer targetHours;
}
