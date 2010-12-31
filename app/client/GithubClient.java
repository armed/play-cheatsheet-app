package client;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import models.Image;
import models.Repo;
import models.Sheet;
import play.libs.WS;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GithubClient {

    private static final String commitsPattern = "http://github.com/api/v2/json/commits/list/%s/%s/master";
    private static final String identifierPattern = "%s:%s";
    private static final String treePattern = "http://github.com/api/v2/json/tree/full/%s/%s/%s";
    
    private String userName;
    private String repoName;
    
    private String treeSha;
          
    public GithubClient(String userName, String repoName) {
        this.userName = userName;
        this.repoName = repoName;
    }

    public Repo getLatest() {
        fetchLatestTreeSha();
        
        Repo repo = new Repo();
        repo.identifier = String.format(identifierPattern, userName, repoName);
        repo.treeSha = treeSha;
        
        BlobsAndTreesReader reader = new BlobsAndTreesReader(userName, repoName, getBlobsAndTrees());
                
        repo.sheets = reader.getSheets();
        repo.images = reader.getImages();
        
        return repo;
    }

    private void fetchLatestTreeSha() {
        JsonArray commits = asArray(commits(), "commits");
        JsonObject commit = (JsonObject) commits.get(0);
        treeSha = commit.get("tree").getAsString();
    }
    
    private String commits() {
        return String.format(commitsPattern, userName, repoName);
    }
    
    private JsonArray asArray(String req, String arrayName) {
        return (JsonArray) ((JsonObject) WS.url(req).get().getJson()).get(arrayName);
    }
    
    private JsonArray getBlobsAndTrees() {
        return asArray(tree(), "tree");
    }
    
    private String tree() {
        return String.format(treePattern, userName, repoName, treeSha);
    }
}
