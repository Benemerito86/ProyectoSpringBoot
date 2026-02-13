package com.tuorg.saas_core;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaxService {

    private static final Map<String, Double> VAT = Map.of(
            "ES", 0.21,
            "PT", 0.23,
            "FR", 0.20
    );

    public double vatRate(String country) {
        if (country == null) return 0.0;
        return VAT.getOrDefault(country.toUpperCase(), 0.0);
    }
}
