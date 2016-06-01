package com.buildit;

import com.buildit.rental.application.service.RentalService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EntityScan(basePackageClasses = { BuilditRefApplication.class, Jsr310JpaConverters.class })
@SpringBootApplication
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class BuilditRefApplication {
	@Configuration
	static class RestTemplateConfiguration {
		@Autowired @Qualifier("_halObjectMapper")
		private ObjectMapper springHateoasObjectMapper;

		@Bean(name = "objectMapper")
		ObjectMapper objectMapper() {
//			springHateoasObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			springHateoasObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			springHateoasObjectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
			springHateoasObjectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
			springHateoasObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			springHateoasObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			springHateoasObjectMapper.registerModules(new JavaTimeModule());
			return springHateoasObjectMapper;
		}
		@Bean
		public RestTemplate restTemplate() {
			RestTemplate _restTemplate = new RestTemplate();
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
			messageConverters.add(new MappingJackson2HttpMessageConverter(springHateoasObjectMapper));
			_restTemplate.setMessageConverters(messageConverters);
			return _restTemplate;
		}
	}
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BuilditRefApplication.class, args);
	}
}
