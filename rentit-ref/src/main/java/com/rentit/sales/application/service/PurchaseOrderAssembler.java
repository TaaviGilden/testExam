package com.rentit.sales.application.service;

import com.rentit.common.application.dto.BusinessPeriodDTO;
import com.rentit.common.application.exceptions.PlantNotFoundException;
import com.rentit.common.rest.ExtendedLink;
import com.rentit.inventory.application.dto.PlantInventoryEntryDTO;
import com.rentit.inventory.application.service.InventoryService;
import com.rentit.sales.application.dto.PurchaseOrderDTO;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.rest.PurchaseOrderRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {
    @Autowired
    InventoryService inventoryService;

    public PurchaseOrderAssembler() {
        super(PurchaseOrderRestController.class, PurchaseOrderDTO.class);
    }

    @Override
    public PurchaseOrderDTO toResource(PurchaseOrder purchaseOrder) {
        PurchaseOrderDTO dto = createResourceWithId(purchaseOrder.getId().getId(), purchaseOrder);
        try {
            dto.setPlant(inventoryService.findPlant(purchaseOrder.getPlant()));
        } catch (PlantNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        dto.set_id(purchaseOrder.getId().getId());
        dto.setRentalPeriod(BusinessPeriodDTO.of(purchaseOrder.getRentalPeriod().getStartDate(), purchaseOrder.getRentalPeriod().getEndDate()));
        dto.setTotal(purchaseOrder.getTotal());
        dto.setStatus(purchaseOrder.getStatus());

        try {
            switch (purchaseOrder.getStatus()) {
                case PENDING:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class).acceptPurchaseOrder(dto.get_id())).toString(),
                            "accept", PATCH));
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class).rejectPurchaseOrder(dto.get_id())).toString(),
                            "reject", DELETE));
                    break;
                case OPEN:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PurchaseOrderRestController.class).closePurchaseOrder(dto.get_id())).toString(),
                            "close", DELETE));
                    break;
            }
        } catch (Exception e) {}
        return dto;
    }
}
