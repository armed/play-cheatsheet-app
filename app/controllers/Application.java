package controllers;

import play.*;
import play.cache.Cache;
import play.modules.twig.Twig;
import play.mvc.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.common.collect.Lists;

import client.CheatSheetsReader;
import client.GithubClient;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import models.*;

public class Application extends Controller {
    
    private static final String defaultUser = "armed";
    private static final String defaultRepo = "play-cheatsheets";

    public static void index(String userName, String repoName) {
        Repo repo = null;
        if (userName == null || repoName == null || userName.isEmpty() || repoName.isEmpty()) {
            repo = getRepo(defaultUser, defaultRepo);
        } else {
            repo = getRepo(userName, repoName);
        }
        
        render(repo);
    }
    
    private static Repo getRepo(String userName, String repoName) {
        String identifier = Repo.makeIdentifier(userName, repoName);
        
        Repo repo = (Repo) Cache.get(identifier);
        
        if (repo == null) {
            repo = Repo.findByIdentifier(identifier);
            
            if (repo == null) {
                Logger.info("Nothing found, requesting github");
                GithubClient client = new GithubClient(userName, repoName);
                repo = client.getLatest();
                repo.store();
            }
            
            Cache.set(identifier, repo, "8h");
        }
        
        return repo;
    }
    
    public static void resetCache(String userName, String repoName) {
        String identifier;
        
        if (userName == null || repoName == null || userName.isEmpty() || repoName.isEmpty()) {
            identifier = Repo.makeIdentifier(defaultUser, defaultRepo);
        } else {
            identifier = Repo.makeIdentifier(userName, repoName);
        }
        
        Repo repo = Repo.findByIdentifier(identifier);
        
        if (repo != null) {
            repo.delete();
        }
        
        Cache.delete(identifier);
        
        index(userName, repoName);
    }
}