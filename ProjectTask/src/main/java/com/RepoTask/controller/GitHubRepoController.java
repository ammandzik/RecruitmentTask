package com.RepoTask.controller;

import com.RepoTask.service.GitHubRepoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("api/github")
@AllArgsConstructor
public class GitHubRepoController {

    private final GitHubRepoService gitHubRepoService;

    @GetMapping("/non-forks/{username}")
    public Mono<ResponseEntity<Object>> getNonForkRepositories(@PathVariable String username) {

        return gitHubRepoService.getResponseEntityReposWithBranches(username);
    }


}
