package com.buildit.procurement.rest;

import com.buildit.BuilditRefApplication;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.service.RentalService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestContext.class, BuilditRefApplication.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class PlantHireRequestControllerTests {
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;

	@Autowired @Qualifier("_halObjectMapper")
	ObjectMapper mapper;

	@Autowired
	RentalService rentalService;

	@Autowired
	PlantHireRequestController plantHireRequestController;

	@Before
	public void setup() {
		Mockito.reset(rentalService);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetAllPlants() throws Exception {
		Resource responseBody = new ClassPathResource("trucks.json", this.getClass());
		List<PlantInventoryEntryDTO> list = mapper.readValue(responseBody.getFile(), new TypeReference<List<PlantInventoryEntryDTO>>() { });
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(2);
		when(rentalService.findAvailablePlants("Truck", startDate, endDate)).thenReturn(list);
		MvcResult result = mockMvc.perform(get("/phrs/plants?name=Truck&startDate={start}&endDate={end}", startDate, endDate))
				.andExpect(status().isOk())
				.andReturn();

		List<PlantInventoryEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PlantInventoryEntryDTO>>() {});

		System.out.println(mapper.writeValueAsString(plants));
	}
}
