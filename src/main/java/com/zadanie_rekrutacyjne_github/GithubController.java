package com.zadanie_rekrutacyjne_github;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/github")
class GithubController {

    private final GithubService githubService;

    GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}/repos")
    ResponseEntity<List<ReposWithAllBranch>> listRepos(@PathVariable("username") final String username) {
        return ResponseEntity.ok(githubService.retrieveUserRepositoriesWithBranches(username));
    }
}
