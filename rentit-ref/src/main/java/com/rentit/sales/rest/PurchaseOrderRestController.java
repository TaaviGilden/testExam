package com.rentit.sales.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentit.common.application.exceptions.PlantNotFoundException;
import com.rentit.sales.application.dto.PurchaseOrderDTO;
import com.rentit.sales.application.service.SalesService;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/api/sales/orders")
@CrossOrigin
public class PurchaseOrderRestController {
    @Autowired
    SalesService salesService;
    @Autowired @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @RequestMapping(method = POST, path = "")
    public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrderDTO poDTO) throws Exception {
        poDTO = salesService.createPurchaseOrder(poDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(poDTO.getId().getHref()));
        return new ResponseEntity<PurchaseOrderDTO>(poDTO, headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = GET, path = "")
    public List<PurchaseOrderDTO> getAllPurchaseOrders() throws Exception {
        return salesService.findAll();
    }

    @RequestMapping(method = GET, path = "/{id}")
    public PurchaseOrderDTO showPurchaseOrder(@PathVariable Long id) throws Exception {
        PurchaseOrderDTO poDTO = salesService.findPurchaseOrder(PurchaseOrderID.of(id));
        return poDTO;
    }

    @RequestMapping(method = PATCH, path = "/{id}/accept")
    public PurchaseOrderDTO acceptPurchaseOrder(@PathVariable Long id) throws Exception {
        return salesService.acceptPurchaseOrder(PurchaseOrderID.of(id));
    }

    @RequestMapping(method = DELETE, path = "/{id}/accept")
    public PurchaseOrderDTO rejectPurchaseOrder(@PathVariable Long id) throws Exception {
        return salesService.rejectPurchaseOrder(PurchaseOrderID.of(id));
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    public PurchaseOrderDTO closePurchaseOrder(@PathVariable Long id) throws Exception {
        return salesService.closePurchaseOrder(PurchaseOrderID.of(id));
    }

    @ExceptionHandler({BindException.class, PlantNotFoundException.class})
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public String bindExceptionHandler(Exception ex) {
        return ex.getMessage();
    }
}
