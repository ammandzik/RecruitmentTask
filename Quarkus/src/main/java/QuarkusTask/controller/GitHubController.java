package QuarkusTask.controller;

import QuarkusTask.service.GitHubRepoService;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Path("api/github")
class GitHubController {

    private final GitHubRepoService gitHubRepoService;
    @GET
    @Path("/non-forks/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Object> getNonForkRepositories(@PathParam("username") String username) {

        return gitHubRepoService.getResponseEntityReposWithBranches(username);
    }
}
