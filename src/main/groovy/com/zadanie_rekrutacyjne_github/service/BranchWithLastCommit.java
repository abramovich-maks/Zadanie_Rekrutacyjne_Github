package com.zadanie_rekrutacyjne_github.service;


import com.zadanie_rekrutacyjne_github.proxy.Commit;

public record BranchWithLastCommit(String name, Commit commit) {
}
