package QuarkusTask.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BranchInfo {

    private String branchName;
    private String commitSha;
}
