package clones.identifier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        // Set the local path where you want to clone the repo
        String localRepoPath = "../../yorubaname-website";
        String remoteRepoUrl = "https://github.com/Yorubaname/yorubaname-website.git";

        File localRepoDir = new File(localRepoPath);

        // Clone the repository only if it doesn't exist
        if (!localRepoDir.exists()) {
            System.out.println("Cloning repository...");
            Git.cloneRepository()
                    .setURI(remoteRepoUrl)
                    .setDirectory(localRepoDir)
                    .call();
        } else {
            System.out.println("Repository already exists. Using local copy.");
        }

        // Open the repository using JGit
        Repository repo = new FileRepositoryBuilder()
                .setGitDir(new File(localRepoPath + "/.git"))
                .readEnvironment()
                .findGitDir()
                .build();

        // Use RefactoringMiner to analyze the commit history
        GitHistoryRefactoringMinerImpl miner = new GitHistoryRefactoringMinerImpl();

        miner.detectAll(repo, "master", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                System.out.println("Commit: " + commitId);
                for (Refactoring ref : refactorings) {
                    String refType = ref.getName();
                    // Filter only renaming-related refactorings
                    if (refType.contains("Rename")) {
                        System.out.println(ref.toString());
                    }
                }
            }
        });

        // Close the repository
        repo.close();
    }
}
