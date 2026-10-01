package com.br.manager.domain.shift_cachs.entity;

import com.br.manager.domain.shift_cachs.enums.ShiftAllocationRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "shift_allocations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_shift_allocation_shift_user", columnNames = {"shift_id", "user_id"})
        })
public class ShiftAllocation {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Shift is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @NotNull(message = "User ID is required.")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull(message = "Allocation role is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private ShiftAllocationRoleEnum role;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public ShiftAllocationRoleEnum getRole() {
        return role;
    }

    public void setRole(ShiftAllocationRoleEnum role) {
        this.role = role;
    }
}
