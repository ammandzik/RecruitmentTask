package service;

import QuarkusTask.service.GitHubRepoService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
class GitHubServiceIT {
    @Autowired
    GitHubRepoService gitHubRepoService;

    @Test
    void fetchExistingUserReposWithBranchesAncCommitsSuccessfully() {

        //given
        final String USERNAME = "octocat";

        //when & then
        Uni<Object> response = assertDoesNotThrow(() -> gitHubRepoService.getResponseEntityReposWithBranches(USERNAME));
        RestAssured.given()
                .when().get("/api/github/non-forks/" + USERNAME)
                .then()
                .statusCode(anyOf(
                        is(200),
                        is(201),
                        is(202),
                        is(203),
                        is(204)
                ))
                .body("size()", greaterThanOrEqualTo(0))
                .body("[0].name", not(emptyString()))
                .body("[0].branches", not(emptyString()))
                .body("[0].branches.size()", greaterThanOrEqualTo(0));
    }
}