package com.RepoTask;

import com.RepoTask.service.GitHubRepoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class GitHubRepoIT {

    @Autowired
    GitHubRepoService gitHubRepoService;

    @Test
    void fetchReposWithBranchesAndCommitsShaCorrectly() {

        //given
        String USERNAME = "octocat";

        //when
        Mono<ResponseEntity<Object>> result = assertDoesNotThrow(() -> gitHubRepoService.getResponseEntityReposWithBranches(USERNAME));

        //then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getStatusCode().is2xxSuccessful()

                )
                .verifyComplete();

    }
}
