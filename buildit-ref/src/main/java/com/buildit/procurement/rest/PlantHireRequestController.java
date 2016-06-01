package com.buildit.procurement.rest;

import com.buildit.procurement.application.ProcurementService;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by lgbanuelos on 17/03/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/procurement")
public class PlantHireRequestController {
    @Autowired
    RentalService rentalService;
    @Autowired
    ProcurementService procurementService;

    @RequestMapping(method = GET, path = "/plants")
    public List<PlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name") String plantName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return rentalService.findAvailablePlants(plantName, startDate, endDate);
    }
    @RequestMapping(method = POST, path = "/phrs")
    public PlantHireRequestDTO create(@RequestBody PlantHireRequestDTO phr) throws Exception {
        return procurementService.createPHR(phr);
    }

    @RequestMapping(method = GET, path = "/phrs")
    public List<PlantHireRequestDTO> getAllPHR() throws Exception {
        return procurementService.getAllPHR();
    }
    @RequestMapping(method = DELETE, path = "/phrs/{id}")
    public PlantHireRequestDTO closePHR(@PathVariable Long id) throws Exception {
        return procurementService.closePHR(id);
    }

    @RequestMapping(method = PATCH, path = "/phrs/{id}/po")
    public PlantHireRequestDTO updatePHRPOStatus(@PathVariable Long id) throws Exception {
        return procurementService.updatePHRPOStatus(id);
    }
}
