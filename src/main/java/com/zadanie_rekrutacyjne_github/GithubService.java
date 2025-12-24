package com.zadanie_rekrutacyjne_github;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GithubService {

    private final GithubProxy githubProxy;

    GithubService(final GithubProxy githubProxy) {
        this.githubProxy = githubProxy;
    }

    List<ReposWithAllBranch> retrieveUserRepositoriesWithBranches(final String username) {
        List<OwnerRepository> ownerRepositories = githubProxy.retrieveAllRepos(username);

        return ownerRepositories.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    String repoName = repo.name();
                    String owner = repo.owner().login();

                    List<RepositoryInfo> branches = githubProxy.retrieveAllBranchByReposName(owner, repoName);

                    List<BranchWithLastCommit> branchDetails = branches.stream()
                            .map(branch -> new BranchWithLastCommit(branch.name(), branch.commit()))
                            .toList();

                    return new ReposWithAllBranch(repoName, owner, branchDetails);
                })
                .toList();
    }
}

