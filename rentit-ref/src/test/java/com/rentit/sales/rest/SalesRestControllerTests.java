package com.rentit.sales.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentit.RentitRefApplication;
import com.rentit.common.application.dto.BusinessPeriodDTO;
import com.rentit.inventory.application.dto.PlantInventoryEntryDTO;
import com.rentit.inventory.rest.PlantInventoryEntryRestController;
import com.rentit.sales.application.dto.PurchaseOrderDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RentitRefApplication.class)
@WebAppConfiguration
@DirtiesContext
@ActiveProfiles("test")
public class SalesRestControllerTests {
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;

	@Autowired @Qualifier("_halObjectMapper")
	ObjectMapper jacksonObjectMapper;

//	@Autowired// @Qualifier("objectMapper")
//	ObjectMapper jacksonObjectMapper;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	@Sql("plants-dataset.sql")
	public void testGetAllPlants() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/inventory/plants?name=Exc&startDate=2016-03-14&endDate=2016-03-25"))
//				.andExpect(status().isOk())
				.andReturn();

		List<PlantInventoryEntryDTO> plants = jacksonObjectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PlantInventoryEntryDTO>>() { });

		assertThat(plants.size(), is(5));

		PurchaseOrderDTO order = new PurchaseOrderDTO();
		order.setPlant(plants.get(2));
		order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
		String str = null;
		System.out.println(str = jacksonObjectMapper.writeValueAsString(order));
		PurchaseOrderDTO _xorder = jacksonObjectMapper.readValue(str, PurchaseOrderDTO.class);
		System.out.println(jacksonObjectMapper.writeValueAsString(_xorder));

		result = mockMvc.perform(post("/api/sales/orders").content(jacksonObjectMapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", not(isEmptyOrNullString())))
				.andReturn();

		order = jacksonObjectMapper.readValue(result.getResponse().getContentAsString(), PurchaseOrderDTO.class);

		assertThat(order.get_xlink("accept").getHref(), is(notNullValue()));

		mockMvc.perform(post(order.get_xlink("accept").getHref())).andReturn();

		AnnotationMappingDiscoverer discoverer = new AnnotationMappingDiscoverer(RequestMapping.class);
		String mapping = discoverer.getMapping(PlantInventoryEntryRestController.class,
				PlantInventoryEntryRestController.class.getMethod("show", Long.class));
		UriTemplate uriTemplate = new UriTemplate(mapping);
		Map<String, String> map = uriTemplate.match("http://localhost:8080/api/inventory/plants/123");

		System.out.println(mapping);
		System.out.println(map);

		order = new PurchaseOrderDTO();
		order.setPlant(plants.get(2));
		order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
		result = mockMvc.perform(post("/api/sales/orders").content(jacksonObjectMapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", not(isEmptyOrNullString())))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

//		System.out.println(order.getId());
	}
}
