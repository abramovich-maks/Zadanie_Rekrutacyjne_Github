package com.zadanie_rekrutacyjne_github.service;

import com.zadanie_rekrutacyjne_github.proxy.GithubProxy;
import com.zadanie_rekrutacyjne_github.proxy.OwnerRepository;
import com.zadanie_rekrutacyjne_github.proxy.RepositoryInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubService {

    private final GithubProxy githubProxy;

    GithubService(final GithubProxy githubProxy) {
        this.githubProxy = githubProxy;
    }

    public List<ReposWithAllBranch> retrieveUserRepositoriesWithBranches(String username) {
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

