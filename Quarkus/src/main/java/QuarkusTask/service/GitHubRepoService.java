package QuarkusTask.service;

import QuarkusTask.model.BranchInfo;
import QuarkusTask.model.RepoInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubRepoService {
    private static final String GITHUB_API_URL_REPO = "https://api.github.com/users/%s/repos";
    private static final String GITHUB_API_URL_BRANCHES = "https://api.github.com/repos/%s/%s/branches";
    private final OkHttpClient client = new OkHttpClient();
    private static final String STATUS = "status";
    private static final String MSG = "message";

    public Uni<Object> getResponseEntityReposWithBranches(String username) {

        String url = String.format(GITHUB_API_URL_REPO, username);
        Request request = createRequest(url);

        return fetchUserReposUni(request);
    }

    public Uni<Object> fetchUserReposUni(Request request) {

        return Uni.createFrom().item(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 403) {
                    return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.FORBIDDEN)
                            .entity(Map.of(STATUS, response.code(), MSG, "GitHub API limit reached"))
                            .build();
                }
                if (response.code() == 404) {
                    return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.NOT_FOUND)
                            .entity(Map.of(STATUS, response.code(), MSG, "User not found"))
                            .build();
                }
                if (!response.isSuccessful()) {
                    return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(Map.of(STATUS, response.code(), MSG, "GitHub API error"))
                            .build();
                }

                List<RepoInfo> repoList = fetchUserRepos(client, request);

                return jakarta.ws.rs.core.Response.ok(repoList);

            } catch (IOException e) {
                return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Map.of(STATUS, 500, MSG, "Unexpected error"))
                        .build();
            }
        }).onItem().transform(result -> result);
    }

    public List<RepoInfo> fetchUserRepos(OkHttpClient client, Request request) throws IOException, NullPointerException {

        try (Response response = client.newCall(request).execute()) {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode repositories = objectMapper.readTree(response.body().string());
            List<RepoInfo> repoList = new ArrayList<>();

            for (JsonNode repo : repositories) {
                if (!repo.get("fork").asBoolean()) {
                    String repoName = repo.get("name").asText();
                    String ownerLogin = repo.get("owner").get("login").asText();
                    List<BranchInfo> branches = getBranchesForRepo(ownerLogin, repoName, client);
                    repoList.add(new RepoInfo(repoName, ownerLogin, branches));
                }
            }
            return repoList;
        }
    }

    public List<BranchInfo> getBranchesForRepo(String owner, String repo, OkHttpClient client) throws IOException, NullPointerException {

        String url = String.format(GITHUB_API_URL_BRANCHES, owner, repo);
        Request request = createRequest(url);

        try (Response response = client.newCall(request).execute()) {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode branches = objectMapper.readTree(response.body().string());
            List<BranchInfo> branchesList = new ArrayList<>();

            for (JsonNode branch : branches) {
                String branchName = branch.get("name").asText();
                String commitSha = branch.get("commit").get("sha").asText();
                branchesList.add(new BranchInfo(branchName, commitSha));
            }
            return branchesList;
        }
    }

    private Request createRequest(String url) {

        return new Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .build();
    }
}
