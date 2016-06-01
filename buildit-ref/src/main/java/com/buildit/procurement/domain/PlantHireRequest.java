package com.buildit.procurement.domain;

import com.buildit.rental.application.dto.POStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)

public class PlantHireRequest {
    @Id @GeneratedValue
    Long id;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name="href", column=@Column(name="plantHRef"))})
    PlantInventoryEntry plant;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="href", column=@Column(name="poHRef")),
            @AttributeOverride(name="total", column=@Column(name="poTotal")),
            @AttributeOverride(name="status", column=@Column(name="poStatus"))})
    PurchaseOrder purchaseOrder;
    @Embedded
    BusinessPeriod rentalPeriod;

    @Column(precision = 8, scale = 2)
    BigDecimal total;

    @Enumerated(EnumType.STRING)
    PHRStatus status;

    public static PlantHireRequest of(PlantInventoryEntry plant, BusinessPeriod rentalPeriod) {
        PlantHireRequest phr = new PlantHireRequest();
        phr.plant = plant;
        phr.rentalPeriod = rentalPeriod;
        phr.status = PHRStatus.CREATED;
        return phr;
    }

    public void updatePurchaseOrder(PurchaseOrder purchaseOrder) throws Exception {
        if (this.purchaseOrder != null && !this.purchaseOrder.getHref().equals(purchaseOrder.getHref()))
            throw new Exception("Invalid Purchase Order reference");
        this.purchaseOrder = purchaseOrder;
        if (purchaseOrder.getStatus().equals(POStatus.PENDING))
            this.status = PHRStatus.PO_PENDING;
        else if (purchaseOrder.getStatus().equals(POStatus.REJECTED))
            this.status = PHRStatus.PO_REJECTED;
        else if (purchaseOrder.getStatus().equals(POStatus.OPEN))
            this.status = PHRStatus.OPEN;
    }

    public void closePHR() {
        if (this.status.equals(PHRStatus.OPEN))
            this.status = PHRStatus.CLOSED;
    }
}
