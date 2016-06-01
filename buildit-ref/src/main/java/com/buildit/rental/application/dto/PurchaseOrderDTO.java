package com.buildit.rental.application.dto;

import com.buildit.common.rest.ResourceSupport;
import com.buildit.procurement.application.dto.BusinessPeriodDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor(force = true)
public class PurchaseOrderDTO extends ResourceSupport {
    PlantInventoryEntryDTO plant;
    BusinessPeriodDTO rentalPeriod;
    BigDecimal total;
    POStatus status;
}
