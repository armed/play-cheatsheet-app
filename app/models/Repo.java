package models;

import java.io.Serializable;
import java.util.List;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.common.collect.Lists;
import com.vercer.engine.persist.annotation.Child;
import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Key;

import play.modules.twig.Twig;
import play.modules.twig.TwigModel;

public class Repo extends TwigModel {

    private static final String identifierPattern = "%s:%s";
    
    @Key
    public String identifier;
    public String treeSha;
    @Embed
    public List<Sheet> sheets;
    
    public static String makeIdentifier(String userName, String repoName) {
        return String.format(identifierPattern, userName, repoName);
    }
    
    public static Repo findByIdentifier(String identifier) {
        List<Repo> repos = Lists.newArrayList(Twig.find()
                                                .type(Repo.class)
                                                .addFilter("identifier", FilterOperator.EQUAL, identifier)
                                                .returnResultsNow());
        if (repos.size() == 0) {
            return null;
        } else {
            return repos.get(0);
        }
    }
}
