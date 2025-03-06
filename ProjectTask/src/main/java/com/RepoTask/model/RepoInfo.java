package com.RepoTask.model;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RepoInfo {

    public String repositoryName;
    public String ownerLogin;
    public List<BranchInfo> branches;

}
