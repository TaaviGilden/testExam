package com.buildit.procurement.application;


import com.buildit.common.rest.ExtendedLink;
import com.buildit.procurement.application.dto.BusinessPeriodDTO;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.PlantHireRequest;
import com.buildit.procurement.rest.PlantHireRequestController;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;

@Service
public class PlantHireRequestAssembler extends ResourceAssemblerSupport<PlantHireRequest, PlantHireRequestDTO> {

    public PlantHireRequestAssembler() {
        super(PlantHireRequestController.class, PlantHireRequestDTO.class);
    }

    public PlantHireRequestDTO toResource(PlantHireRequest phr) {
        PlantHireRequestDTO dto = createResourceWithId(phr.getId(), phr);

        PlantInventoryEntryDTO plantDTO = new PlantInventoryEntryDTO();
        plantDTO.setName(phr.getPlant().getName());
        plantDTO.add(new Link(phr.getPlant().getHref()));
        dto.setPlant(plantDTO);

        dto.setRentalPeriod(BusinessPeriodDTO.of(phr.getRentalPeriod().getStartDate(), phr.getRentalPeriod().getEndDate()));

        if (phr.getPurchaseOrder() != null) {
            PurchaseOrderDTO poDTO = new PurchaseOrderDTO();
            poDTO.add(new Link(phr.getPurchaseOrder().getHref()));
            poDTO.setStatus(phr.getPurchaseOrder().getStatus());
            poDTO.setTotal(phr.getPurchaseOrder().getTotal());
            dto.setPurchaseOrder(poDTO);
        }

        dto.setTotal(phr.getTotal());
        dto.setStatus(phr.getStatus());

        try {
            switch (phr.getStatus()) {
                case PO_PENDING:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PlantHireRequestController.class).updatePHRPOStatus(phr.getId())).toString(),
                            "updatePOStatus", PATCH));
                    break;
                case CLOSED:
                    dto.add(new ExtendedLink(
                            linkTo(methodOn(PlantHireRequestController.class).closePHR(phr.getId())).toString(),
                            "closePHR", DELETE));
                    break;
            }
        } catch (Exception e) {}

        return dto;
    }
}
