package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CurrencyConversionService {

    private List<CurrencyConversion> currencyConversion;

    public List<CurrencyConversion> getCurrencyConversion() {
        return currencyConversion;
    }

    public void setCurrencyConversion(List<CurrencyConversion> currencyConversion) {
        this.currencyConversion = currencyConversion;
    }
}
