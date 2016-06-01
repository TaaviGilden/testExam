package com.buildit.procurement.application;

import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.*;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcurementService {
    @Autowired
    RentalService rentalService;
    @Autowired
    PlantHireRequestRepository phrRepository;
    @Autowired
    PlantHireRequestAssembler assembler;

    public PlantHireRequestDTO createPHR(PlantHireRequestDTO phrDTO) throws Exception {
        PlantHireRequest phr = PlantHireRequest.of(
                PlantInventoryEntry.of(phrDTO.getPlant().getLink("self").getHref(), phrDTO.getPlant().getName()),
                BusinessPeriod.of(phrDTO.getRentalPeriod().getStartDate(), phrDTO.getRentalPeriod().getEndDate())
        );
        phrRepository.save(phr);

        PurchaseOrderDTO poDTO = new PurchaseOrderDTO();
        poDTO.setPlant(phrDTO.getPlant());
        poDTO.setRentalPeriod(phrDTO.getRentalPeriod());
        poDTO = rentalService.createPurchaseOrder(poDTO);

        phr.updatePurchaseOrder(PurchaseOrder.of(poDTO.getLink("self").getHref(), poDTO.getTotal(), poDTO.getStatus()));

        phrRepository.save(phr);

        return assembler.toResource(phr);
    }

    public PlantHireRequestDTO closePHR(Long id) {
        PlantHireRequest phr = phrRepository.findOne(id);
        phr.closePHR();
        phrRepository.save(phr);

        return assembler.toResource(phr);
    }

    public List<PlantHireRequestDTO> getAllPHR() {
        return assembler.toResources(phrRepository.findAll());
    }

    public PlantHireRequestDTO updatePHRPOStatus(Long id) throws Exception {
        PlantHireRequest phr = phrRepository.findOne(id);
        PurchaseOrderDTO poDTO = new PurchaseOrderDTO();
        poDTO.add(new Link(phr.getPurchaseOrder().getHref()));
        poDTO = rentalService.fetchPurchaseOrder(poDTO);
        phr.updatePurchaseOrder(PurchaseOrder.of(poDTO.getLink("self").getHref(), poDTO.getTotal(), poDTO.getStatus()));
        phrRepository.save(phr);
        return assembler.toResource(phr);
    }
}
