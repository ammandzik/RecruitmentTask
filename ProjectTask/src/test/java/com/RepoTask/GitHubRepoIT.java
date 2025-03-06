package com.RepoTask;

import com.RepoTask.service.GitHubRepoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GitHubRepoIT {

    @Autowired
    GitHubRepoService gitHubRepoService;

    @Test
    void fetchReposWithBranchesAndCommitShaCorrectly() {

        //given

        //when

        //then

    }
}
