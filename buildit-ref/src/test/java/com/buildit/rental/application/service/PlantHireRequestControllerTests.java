package com.buildit.rental.application.service;

import com.buildit.BuilditRefApplication;
import com.buildit.procurement.application.dto.BusinessPeriodDTO;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BuilditRefApplication.class})
@WebAppConfiguration
public class PlantHireRequestControllerTests {
	@Autowired @Qualifier("_halObjectMapper")
	ObjectMapper mapper;

	@Autowired
	RentalService rentalService;

	@Autowired
	RestTemplate restTemplate;

	List<ClientHttpRequestInterceptor> savedInterceptors = null;
	@Before
	public void setup() {
		savedInterceptors = restTemplate.getInterceptors();
	}
	@After
	public void tearoff() {
		restTemplate.setInterceptors(savedInterceptors);
	}

	@Test
	public void testGetAllPlants() throws PlantNotAvailableException {
		PlantInventoryEntryDTO plant = new PlantInventoryEntryDTO();
		plant.add(new Link("http://rentit.com/api/inventory/plants/13"));

		PurchaseOrderDTO order = new PurchaseOrderDTO();
		order.setPlant(plant);
		order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(4)));

		rentalService.createPurchaseOrder(order);
	}

	@Test
	public void testRejection() throws RestClientException, IOException {
		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
				request.getHeaders().add("prefer", "409");
				return execution.execute(request, body);
			}
		};
		restTemplate.setInterceptors(Collections.singletonList(interceptor));

		PurchaseOrderDTO order = new PurchaseOrderDTO();
		order.add(new Link("http://192.168.99.100:3000/api/sales/orders/100"));

		System.out.println(rentalService.fetchPurchaseOrder(order));
	}
}
