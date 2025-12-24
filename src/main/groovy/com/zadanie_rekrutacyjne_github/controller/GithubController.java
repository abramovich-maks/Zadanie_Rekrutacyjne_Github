package com.zadanie_rekrutacyjne_github.controller;

import com.zadanie_rekrutacyjne_github.service.GithubService;
import com.zadanie_rekrutacyjne_github.service.ReposWithAllBranch;
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

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}/repos")
    public ResponseEntity<List<ReposWithAllBranch>> listRepos(@PathVariable("username") String username) {
        return ResponseEntity.ok(githubService.retrieveUserRepositoriesWithBranches(username));
    }
}
