package com.buildit.procurement.application.dto;

import com.buildit.common.rest.ResourceSupport;
import com.buildit.procurement.domain.BusinessPeriod;
import com.buildit.procurement.domain.PHRStatus;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
public class PlantHireRequestDTO extends ResourceSupport {
    BusinessPeriodDTO rentalPeriod;
    PlantInventoryEntryDTO plant;
    PurchaseOrderDTO purchaseOrder;
    @Column(precision = 8, scale = 2)
    BigDecimal total;
    @Enumerated(EnumType.STRING)
    PHRStatus status;
}
