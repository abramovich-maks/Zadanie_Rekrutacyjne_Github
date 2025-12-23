package com.zadanie_rekrutacyjne_github.proxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OwnerRepository(
        String name,
        Owner owner,
        Boolean fork
) {
}
