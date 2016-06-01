package com.buildit.procurement.domain;

import com.buildit.rental.application.dto.POStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class PurchaseOrder {
    String href;
    @Column(precision = 8, scale = 2)
    BigDecimal total;
    @Enumerated(EnumType.STRING)
    POStatus status;
}
