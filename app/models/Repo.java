package models;

import static util.IteratorHelper.allOrNothing;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import play.modules.twig.Twig;
import play.modules.twig.TwigModel;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.common.collect.Maps;
import com.vercer.engine.persist.annotation.Index;
import com.vercer.engine.persist.annotation.Type;

public class Repo extends TwigModel {

    @Index
    public String githubUser;
    @Index
    public String repoName;
    public String title;

    public Author author;

    @Type(Blob.class)
    public List<String> sheets;

    public static Repo findByAuthorAndRepo(String authorEmail, String githubUser, String repoName) {
        return allOrNothing(Twig.find().type(Repo.class)
                .addFilter("author.email", FilterOperator.EQUAL, authorEmail)
                .addFilter("githubUser", FilterOperator.EQUAL, githubUser)
                .addFilter("repoName", FilterOperator.EQUAL, repoName)
                .returnResultsNow());
    }

    public static Map<String, Repo> findAll() {
        Iterator<Repo> iter = Twig.find().type(Repo.class).returnResultsNow();
        Map<String, Repo> result = Maps.newHashMap();

        while (iter.hasNext()) {
            Repo repo = iter.next();
            Key key = Twig.associatedKey(repo);

            result.put(String.valueOf(key.getId()), repo);
        }

        return result;
    }

    public static Repo findById(Long id) {
        return Twig.load(Repo.class, id);
    }
}
