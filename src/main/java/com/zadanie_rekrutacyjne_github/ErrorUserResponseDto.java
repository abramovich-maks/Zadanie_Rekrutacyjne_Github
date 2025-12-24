package com.zadanie_rekrutacyjne_github;

import org.springframework.http.HttpStatus;

record ErrorUserResponseDto(HttpStatus status, String message) {
}
