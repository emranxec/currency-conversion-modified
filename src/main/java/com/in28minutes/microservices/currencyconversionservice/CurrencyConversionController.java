package com.in28minutes.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api")
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeProxy proxy;

	@Autowired()
	@Qualifier("webClient")
	private WebClient.Builder webClient;
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	@PreAuthorize("hasAnyRole('ROLE_STUDENT')")
	public CurrencyConversion calculateCurrencyConversion(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity
			) {
		
		HashMap<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from",from);
		uriVariables.put("to",to);
		
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity
		("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversion.class, uriVariables);
		
		CurrencyConversion currencyConversion = responseEntity.getBody();
		
		return new CurrencyConversion(currencyConversion.getId(), 
				from, to, quantity, 
				currencyConversion.getConversionMultiple(), 
				quantity.multiply(currencyConversion.getConversionMultiple()), 
				currencyConversion.getEnvironment()+ " " + "rest template");
		
	}

	//asynchronous
	@GetMapping("/currency-conversion-web-client/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionService calculateCurrencyConversionWebClient(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity
			) {
				List<CurrencyConversion> currencyConversionList=new ArrayList<CurrencyConversion>();
		CurrencyConversion currencyConversion = webClient
				.build()
				.get()
				.uri("http://localhost:8000/currency-exchange/from/"+from+"/to/" + to)
				.retrieve()
				.bodyToMono(CurrencyConversion.class)
				.block(); //making async to sync
		CurrencyConversion currencyConversion1 = new CurrencyConversion(currencyConversion.getId(),
				from, to, quantity,
				currencyConversion.getConversionMultiple(),
				quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment() + " " + "web-client");

		CurrencyConversionService currencyConversionService=new CurrencyConversionService();
		currencyConversionList.add(currencyConversion1);
		currencyConversionService.setCurrencyConversion(currencyConversionList);
		return currencyConversionService;
		
	}

	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	@PreAuthorize("hasAuthority('course:write')")
	public CurrencyConversion calculateCurrencyConversionFeign(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity
	) {

		CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

		return new CurrencyConversion(currencyConversion.getId(),
				from, to, quantity,
				currencyConversion.getConversionMultiple(),
				quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment() + " " + "feign");

	}


}
