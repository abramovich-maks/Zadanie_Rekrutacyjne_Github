package com.zadanie_rekrutacyjne_github;

class UserNotFoundException extends RuntimeException {
    UserNotFoundException(String message) {
        super(message);
    }
}
