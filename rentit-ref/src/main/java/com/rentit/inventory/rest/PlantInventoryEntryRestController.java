package com.rentit.inventory.rest;

import com.rentit.common.application.exceptions.PlantNotFoundException;
import com.rentit.inventory.application.dto.PlantInventoryEntryDTO;
import com.rentit.inventory.application.service.InventoryService;
import com.rentit.inventory.domain.model.PlantInventoryEntryID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/api/inventory/plants")
@CrossOrigin
public class PlantInventoryEntryRestController {
    @Autowired
    InventoryService inventoryService;

    @RequestMapping(method = GET, path = "")
    public List<PlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name", required = false) Optional<String> plantName,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate) {

        if (plantName.isPresent() && startDate.isPresent() && endDate.isPresent()) {
            if (endDate.get().isBefore(startDate.get()))
                throw new IllegalArgumentException("Something wrong with the requested period ('endDate' happens before 'startDate')");
            return inventoryService.findAvailablePlants(plantName.get(), startDate.get(), endDate.get());
        } else if (plantName.isPresent() && !startDate.isPresent() && !endDate.isPresent())
            return inventoryService.findPlantsByName(plantName.get());
        else if (!plantName.isPresent() && !startDate.isPresent() && !endDate.isPresent())
            return inventoryService.findAllPlantInventoryEntries();
        throw new IllegalArgumentException(
                String.format("Wrong number of parameters: Name='%s', Start date='%s', End date='%s'",
                        plantName.get(), startDate.get(), endDate.get()));
    }

    @RequestMapping(method = GET, path = "/{id}")
    public PlantInventoryEntryDTO show(@PathVariable Long id) throws PlantNotFoundException {
            return inventoryService.findPlant(PlantInventoryEntryID.of(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Exception exc) {
        return exc.getMessage();
    }

    @ExceptionHandler(PlantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception exc) {
        return exc.getMessage();
    }
}
