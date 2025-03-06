package com.RepoTask.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BranchInfo {

    String branchName;
    String commitSha;


}
