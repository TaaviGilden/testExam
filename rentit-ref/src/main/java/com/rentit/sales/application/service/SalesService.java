package com.rentit.sales.application.service;

import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.common.domain.validation.BusinessPeriodValidator;
import com.rentit.inventory.application.dto.PlantInventoryEntryDTO;
import com.rentit.inventory.application.dto.PlantReservationDTO;
import com.rentit.inventory.application.service.InventoryService;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.domain.model.PlantInventoryEntryID;
import com.rentit.inventory.domain.model.PlantReservation;
import com.rentit.common.application.exceptions.PlantNotFoundException;
import com.rentit.inventory.domain.model.PlantReservationID;
import com.rentit.sales.application.dto.PurchaseOrderDTO;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderID;
import com.rentit.sales.domain.repository.PurchaseOrderRepository;
import com.rentit.sales.domain.validation.ContactPersonValidator;
import com.rentit.sales.domain.validation.PurchaseOrderValidator;
import com.rentit.sales.infrastructure.idgeneration.SalesIdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;

import java.util.List;

@Service
public class SalesService {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    SalesIdentifierGenerator identifierGenerator;

    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) throws BindException, PlantNotFoundException {
        PlantInventoryEntryDTO plant = inventoryService.findPlantFullRepresentation(purchaseOrderDTO.getPlant());
        PurchaseOrder po = PurchaseOrder.of(
                identifierGenerator.nextPurchaseOrderID(),
                PlantInventoryEntryID.of(plant.get_id()),
                BusinessPeriod.of(purchaseOrderDTO.getRentalPeriod().getStartDate(), purchaseOrderDTO.getRentalPeriod().getEndDate())
        );

        DataBinder binder = new DataBinder(po);
        binder.addValidators(new PurchaseOrderValidator(new BusinessPeriodValidator(), new ContactPersonValidator()));
        binder.validate();

        if (binder.getBindingResult().hasErrors())
            throw new BindException(binder.getBindingResult());

        purchaseOrderRepository.save(po);

//        PlantReservationDTO reservationDTO = inventoryService.createPlantReservation(plant, purchaseOrderDTO.getRentalPeriod());
//
//        po.confirmReservation(
//                PlantReservationID.of(reservationDTO.get_id()),
//                plant.getPrice()); // Shouldn't we also pass the reservation schedule as a parameter to check if it matches rental period?
//
//        binder = new DataBinder(po);
//        binder.addValidators(new PurchaseOrderValidator(new BusinessPeriodValidator(), new ContactPersonValidator()));
//        binder.validate();
//
//        if (binder.getBindingResult().hasErrors())
//            throw new BindException(binder.getBindingResult());
//
//        purchaseOrderRepository.save(po);

        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO findPurchaseOrder(PurchaseOrderID id) {
        return purchaseOrderAssembler.toResource(purchaseOrderRepository.findOne(id));
    }

    public List<PurchaseOrderDTO> findAll() {
        return purchaseOrderAssembler.toResources(purchaseOrderRepository.findAll());
    }

    public PurchaseOrderDTO acceptPurchaseOrder(PurchaseOrderID id) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.accept();
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO rejectPurchaseOrder(PurchaseOrderID id) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.reject();
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO closePurchaseOrder(PurchaseOrderID id) {
        PurchaseOrder po = purchaseOrderRepository.findOne(id);
        po.close();
        purchaseOrderRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }
}
