package QuarkusTask.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class RepoInfo {

    private String repositoryName;
    private String ownerLogin;
    private List<BranchInfo> branches;
}