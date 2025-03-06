package com.RepoTask.controller;

import com.RepoTask.model.BranchInfo;
import com.RepoTask.model.RepoInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/github")
public class GitHubRepoController {

    private static final String GITHUB_API_URL = "https://api.github.com/users/%s/repos";
    private static final String GITHUB_API_URL_BRANCHES = "https://api.github.com/repos/%s/%s/branches";
    private static final String STATUS = "status";
    private static final String MSG = "message";

    @GetMapping("/non-forks/{username}")
    public Mono<ResponseEntity<Object>> getNonForkRepositories(@PathVariable String username) {

        OkHttpClient client = new OkHttpClient();
        String url = String.format(GITHUB_API_URL, username);

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        return Mono.fromCallable(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 404) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of(STATUS, 404, MSG, "User not found"));
                }
                if (!response.isSuccessful()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of(STATUS, response.code(), MSG, "GitHub API error"));
                }

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode repositories = objectMapper.readTree(response.body().string());

                List<RepoInfo> repoList = new ArrayList<>();


                for (JsonNode repo : repositories) {
                    if (!repo.get("fork").asBoolean()) {
                        String repoName = repo.get("name").asText();
                        String ownerLogin = repo.get("owner").get("login").asText();
                        List<BranchInfo> branches = getBranchesForRepo(ownerLogin, repoName);
                        repoList.add(new RepoInfo(repoName, ownerLogin, branches));
                    }
                }
                return ResponseEntity.ok(repoList);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(STATUS, 500, MSG, "Unexpected error"));
            }
        });
    }
     public List<BranchInfo> getBranchesForRepo(@PathVariable String owner, @PathVariable String repo) {

         OkHttpClient client = new OkHttpClient();
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

             }catch(IOException ex){
                 return new ArrayList<>();
             }
     }
}
