package com.buildit.rental.application.service;

import com.buildit.procurement.domain.PurchaseOrder;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class RentalService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Value("${rentit.host:}")
    String host;

    @Value("${rentit.port:}")
    String port;

    public List<PlantInventoryEntryDTO> findAvailablePlants(String plantName, LocalDate startDate, LocalDate endDate) {
        PlantInventoryEntryDTO[] plants = restTemplate.getForObject(
                "http://" + host + ":" + port + "/api/inventory/plants?name={name}&startDate={start}&endDate={end}",
                PlantInventoryEntryDTO[].class, plantName, startDate, endDate);
        return Arrays.asList(plants);
    }

    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO order) throws PlantNotAvailableException {
        ResponseEntity<PurchaseOrderDTO> result = restTemplate.postForEntity(
                "http://" + host + ":" + port + "/api/sales/orders", order, PurchaseOrderDTO.class);
        return result.getBody();
    }

    public PurchaseOrderDTO fetchPurchaseOrder(PurchaseOrderDTO order) throws IOException {
        try {
            ResponseEntity<PurchaseOrderDTO> result = restTemplate.getForEntity(
                    order.getId().getHref(), PurchaseOrderDTO.class);
            return result.getBody();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.CONFLICT))
                return mapper.readValue(e.getResponseBodyAsString(), PurchaseOrderDTO.class);
        }
        return null;
    }
}