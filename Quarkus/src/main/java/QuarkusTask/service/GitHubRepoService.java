package QuarkusTask.service;

import QuarkusTask.model.BranchInfo;
import QuarkusTask.model.RepoInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public Uni<ResponseEntity<?>> getResponseEntityReposWithBranches(String username) {

        String url = String.format(GITHUB_API_URL_REPO, username);

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .build();
        return fetchUserReposUni(request);
    }

    public Uni<ResponseEntity<?>> fetchUserReposUni(Request request) {

        return Uni.createFrom().item(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 404) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of(STATUS, 404, MSG, "User not found"));
                }
                if (!response.isSuccessful()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of(MSG, "GitHub API error", STATUS, response.code()));
                }

                List<RepoInfo> repoList = fetchUserRepos(client, request);

                return ResponseEntity.ok(repoList);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(STATUS, 500, MSG, "Unexpected error"));
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

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .build();

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
}
