package com.zadanie_rekrutacyjne_github;

import java.util.List;

record ReposWithAllBranch(String repoName, String ownerLogin, List<BranchWithLastCommit> branches) {
}
