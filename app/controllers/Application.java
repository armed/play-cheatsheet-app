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

import client.BlobsAndTreesReader;
import client.GithubClient;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import models.*;

public class Application extends Controller {
    
    private static final String defaultUser = "armed";
    private static final String defaultRepo = "test-repo";

    public static void index(String userName, String repoName) {
        Repo repo = null;
        if (userName == null || repoName == null || userName.isEmpty() || repoName.isEmpty()) {
            repo = getRepo(defaultUser, defaultRepo);
        } else {
            repo = getRepo(userName, repoName);
        }
        
        Cache.set(session.getId(), repo.identifier);
                
        List<Sheet> first = Lists.newArrayList();
        List<Sheet> second = Lists.newArrayList();
        List<Sheet> third = Lists.newArrayList();
        
        int col = 0;
        for (int i = 0; i < repo.sheets.size(); i++) {
            if (col == 2) {
                third.add(repo.sheets.get(i));
                col = 0;
            } else if (col == 1) {
                second.add(repo.sheets.get(i));
                col = 2;
            } else {
                first.add(repo.sheets.get(i));
                col = 1;
            }
        }
        
        render(first, second, third);
    }
    
    private static Repo getRepo(String identifier) {
        return getRepo(identifier.split(":")[0], identifier.split(":")[1]);
    }
    
    private static Repo getRepo(String userName, String repoName) {
        String identifier = Repo.makeIdentifier(userName, repoName);
        
        Repo repo = (Repo) Cache.get(identifier);
        
        if (repo == null) {
            List<Repo> repos = Lists.newArrayList(Twig.find()
                                                    .type(Repo.class)
                                                    .addFilter("identifier", FilterOperator.EQUAL, identifier)
                                                    .returnResultsNow());
            
            if (repos.size() == 0) {
                GithubClient client = new GithubClient(userName, repoName);
                repo = client.getLatest();
                repo.store();
            } else {
                repo = repos.get(0);
            }
            
            Cache.set(identifier, repo);
        }
        
        return repo;
    }

    public static void image(String name) {
        String identifier = (String) Cache.get(session.getId());
                
        if (identifier == null || identifier.isEmpty()) {
            notFound();
        }
        
        Repo repo = getRepo(identifier);

        for (Image img : repo.images) {
            if (img.name.equals("images/" + name)) {
                ByteArrayInputStream bais = new ByteArrayInputStream(img.data);
                
                response.setHeader("Content-type", img.mimeType);
                renderBinary(bais);
            }
        }
        
        notFound();
    }
}