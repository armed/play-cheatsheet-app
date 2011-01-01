package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import models.Repo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GithubClient {

    private static final String commitsPattern = "http://github.com/api/v2/json/commits/list/%s/%s/master";
    private static final String treePattern = "http://github.com/api/v2/json/tree/full/%s/%s/%s";

    private String userName;
    private String repoName;

    private String treeSha;

    public static String getStringData(String req) {
        try {
            URL url = new URL(req);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder jsonString = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            return jsonString.toString();
        } catch (Exception e) {
            if (!(e instanceof RuntimeException)) {
                throw new RuntimeException("Error getting data from github", e);
            } else {
                throw (RuntimeException) e;
            }
        }
    }
    
    public GithubClient(String userName, String repoName) {
        this.userName = userName;
        this.repoName = repoName;
    }

    public Repo getLatest() {
        fetchLatestTreeSha();

        Repo repo = new Repo();
        repo.identifier = Repo.makeIdentifier(userName, repoName);
        repo.treeSha = treeSha;

        CheatSheetsReader reader = new CheatSheetsReader(userName, repoName, getBlobsAndTrees());

        repo.sheets = reader.getSheets();

        return repo;
    }

    private void fetchLatestTreeSha() {
        JsonArray commits = asArray(commitsUrl(), "commits");
        JsonObject commit = (JsonObject) commits.get(0);
        treeSha = commit.get("tree").getAsString();
    }

    private String commitsUrl() {
        return String.format(commitsPattern, userName, repoName);
    }

    private JsonArray asArray(String req, String arrayName) {
        return (JsonArray) ((JsonObject) new JsonParser().parse(getStringData(req))).get(arrayName);
    }

    private JsonArray getBlobsAndTrees() {
        return asArray(treeUrl(), "tree");
    }

    private String treeUrl() {
        return String.format(treePattern, userName, repoName, treeSha);
    }
}
