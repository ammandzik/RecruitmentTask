package QuarkusTask.controller;

import QuarkusTask.service.GitHubRepoService;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/github")
class GitHubController {

    private final GitHubRepoService gitHubRepoService;

    @GetMapping("/non-forks/{username}")
    public Uni<ResponseEntity<?>> getNonForkRepositories(@PathVariable String username) {

        return gitHubRepoService.getResponseEntityReposWithBranches(username);
    }
}
