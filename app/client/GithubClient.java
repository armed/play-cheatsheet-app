package client;

import java.util.List;

import models.Sheet;

import org.restlet.resource.ClientResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GithubClient {

    private static final String commitsPattern = "http://github.com/api/v2/json/commits/list/%s/%s/master";
    private static final String treePattern = "http://github.com/api/v2/json/tree/full/%s/%s/%s";
    private static final String userCheckPattern = "http://github.com/api/v2/json/user/show/%s";
    private static final String repositoryCheckPattern = "http://github.com/api/v2/json/repos/show/%s/%s";

    private String githubUser;
    private String repoName;

    private String treeSha;

    private static String getStringData(String req) {
        try {
            return new ClientResource(req).get().getText();
        } catch (Exception e) {
            if (!(e instanceof RuntimeException)) {
                throw new RuntimeException("Error getting data from github", e);
            } else {
                throw (RuntimeException) e;
            }
        }
    }

    public GithubClient(String githubUser, String repoName) throws GithubException {
        this.githubUser = githubUser;
        this.repoName = repoName;
    }

    public List<Sheet> getSheets() throws GithubException {
        checkUserExists();
        checkRepositoryExists();
        fetchLatestTreeSha();
        CheatSheetsReader reader = new CheatSheetsReader(githubUser, repoName, getBlobsAndTrees());

        return reader.getSheets();
    }

    private void checkUserExists() throws GithubException {
        try {
            new ClientResource(String.format(userCheckPattern, githubUser)).get().getText();
        } catch (Exception e) {
            throw new GithubException("Github user not found");
        }
    }

    private void checkRepositoryExists() throws GithubException {
        try {
            new ClientResource(String.format(repositoryCheckPattern, githubUser, repoName)).get().getText();
        } catch (Exception e) {
            throw new GithubException("Github repo not found");
        }
    }

    private void fetchLatestTreeSha() {
        JsonArray commits = asArray(commitsUrl(), "commits");
        JsonObject commit = (JsonObject) commits.get(0);
        treeSha = commit.get("tree").getAsString();
    }

    private String commitsUrl() {
        return String.format(commitsPattern, githubUser, repoName);
    }

    private JsonArray asArray(String req, String arrayName) {
        return (JsonArray) ((JsonObject) new JsonParser().parse(getStringData(req))).get(arrayName);
    }

    private JsonArray getBlobsAndTrees() {
        return asArray(treeUrl(), "tree");
    }

    private String treeUrl() {
        return String.format(treePattern, githubUser, repoName, treeSha);
    }
}
