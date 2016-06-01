package com.buildit.procurement.rest;

import com.buildit.rental.application.service.RentalService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestContext {
    @Bean
    public RentalService rentalService() {
        return Mockito.mock(RentalService.class);
    }
}