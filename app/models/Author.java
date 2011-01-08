package models;

import static util.IteratorHelper.allOrNothing;

import java.util.List;

import play.modules.twig.Twig;
import play.modules.twig.TwigModel;

import com.google.appengine.api.datastore.Query.FilterOperator;
import com.vercer.engine.persist.annotation.Activate;
import com.vercer.engine.persist.annotation.Child;
import com.vercer.engine.persist.annotation.Index;

public class Author extends TwigModel {

    @Index
    public String email;
    @Index
    public String name;

    @Child
    @Activate(0)
    public List<Repo> repos;

    public static Author findByEmail(String email) {
        return allOrNothing(Twig.find()
                                    .type(Author.class)
                                    .addFilter("email", FilterOperator.EQUAL, email)
                                    .returnResultsNow());
    }

    public static Author findByName(String name) {
        return allOrNothing(Twig.find()
                                    .type(Author.class)
                                    .addFilter("name", FilterOperator.EQUAL, name)
                                    .returnResultsNow());
    }
}
