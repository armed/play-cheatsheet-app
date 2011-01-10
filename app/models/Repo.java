package models;

import static util.IteratorHelper.allOrNothing;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import play.modules.twig.Twig;
import play.modules.twig.TwigModel;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.common.collect.Maps;
import com.vercer.engine.persist.annotation.Index;
import com.vercer.engine.persist.annotation.Parent;
import com.vercer.engine.persist.annotation.Type;

public class Repo extends TwigModel {

    @Index
    public String githubUser;
    @Index
    public String repoName;
    public String title;
    public Boolean visible;

    @Parent
    public Author author;

    @Type(Blob.class)
    public List<String> sheets;

    public static Repo findByAuthorEmailAndRepo(String authorEmail, String githubUser, String repoName) {
        Author author = Author.findByEmail(authorEmail);

        if (author != null) {
            return findByRepoAndAuthor(githubUser, repoName, author);
        }
        return null;
    }

    public static Repo findByAuthorNameAndRepo(String authorName, String githubUser, String repoName) {
        Author author = Author.findByName(authorName);

        if (author != null) {
            return findByRepoAndAuthor(githubUser, repoName, author);
        }
        return null;
    }

    public static Repo findByRepoAndAuthor(String githubUser, String repoName, Author author) {
        return allOrNothing(Twig.find().type(Repo.class)
                .withAncestor(author)
                .addFilter("githubUser", FilterOperator.EQUAL, githubUser)
                .addFilter("repoName", FilterOperator.EQUAL, repoName)
                .returnResultsNow());
    }

    public static Map<String, Repo> findAllWithKeys() {
        Iterator<Repo> iter = Twig.find().type(Repo.class).returnResultsNow();
        Map<String, Repo> result = Maps.newHashMap();

        while (iter.hasNext()) {
            Repo repo = iter.next();
            Key key = Twig.associatedKey(repo);

            result.put(KeyFactory.keyToString(key), repo);
        }

        return result;
    }

    public static Repo findById(String id) {
        return Twig.load(KeyFactory.stringToKey(id));
    }
}
