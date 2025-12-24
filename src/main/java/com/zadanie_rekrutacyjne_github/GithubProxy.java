package com.zadanie_rekrutacyjne_github;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
class GithubProxy {

    private final RestTemplate restTemplate;

    GithubProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${github.service.url}")
    private String url;

    @Value("${github.token}")
    private String token;


    List<OwnerRepository> retrieveAllRepos(String user) {
        String uri = UriComponentsBuilder.fromUriString(url)
                .path("/users/{user}/repos")
                .buildAndExpand(user)
                .toUriString();

        try {
            ResponseEntity<OwnerRepository[]> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    createEntity(),
                    OwnerRepository[].class
            );

            return response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("User {} not found on Github", user);
            throw new UserNotFoundException("User " + user + " not found on GitHub");
        } catch (Exception e) {
            log.error("Error while fetching repos for user {}: {}", user, e.getMessage());
            throw e;
        }
    }

    List<RepositoryInfo> retrieveAllBranchByReposName(String userName, String repoName) {
        String uri = UriComponentsBuilder.fromUriString(url)
                .path("/repos/{userName}/{reposName}/branches")
                .buildAndExpand(userName, repoName)
                .toUriString();

        ResponseEntity<RepositoryInfo[]> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                createEntity(),
                RepositoryInfo[].class
        );

        return response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();
    }

    private HttpEntity<Void> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "Zadanie_Rekrutacyjne_Github");
        if (token != null && !token.isBlank()) {
            headers.set("Authorization", "Bearer " + token);
        }
        return new HttpEntity<>(headers);
    }
}
