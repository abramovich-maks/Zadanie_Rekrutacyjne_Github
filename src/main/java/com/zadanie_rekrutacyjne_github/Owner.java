package com.zadanie_rekrutacyjne_github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record Owner(
        String login
) {
}
