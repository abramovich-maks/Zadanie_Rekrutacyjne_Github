package com.zadanie_rekrutacyjne_github.service;

import java.util.List;

public record ReposWithAllBranch(String repoName, String ownerLogin, List<BranchWithLastCommit> branches) {
}
