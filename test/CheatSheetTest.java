import models.Author;
import models.Repo;

import org.junit.Before;
import org.junit.Test;

import play.modules.twig.Twig;
import play.mvc.Http;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Lists;

public class CheatSheetTest extends FunctionalTest {

    Author author;
    Author wrongAuthor;
    Repo storedRepo;
    Key storedRepoKey;

    @Before
    public void setUp() {
        Twig.deleteAll(Author.class);
        Twig.deleteAll(Repo.class);

        author = new Author();
        author.email = "artem.medeu@gmail.com";
        author.name = "armed";
        author.store();

        wrongAuthor = new Author();
        wrongAuthor.email = "wrong.author@gmail.com";
        wrongAuthor.name = "wrong";
        wrongAuthor.store();

        storedRepo = new Repo();
        storedRepo.author = author;
        storedRepo.githubUser = "armed";
        storedRepo.repoName = "test-repo";
        storedRepo.sheets = Lists.newArrayList("cheatsheet 1", "cheatsheet 2");
        storedRepo.title = "test title";
        storedRepoKey = storedRepo.store();
    }

    @Test
    public void showCheatSheet() {
        Response resp = GET("/show/armed/armed/test-repo");

        assertIsOk(resp);
        assertContentMatch(storedRepo.title, resp);
        assertContentMatch("cheatsheet 1", resp);
        assertContentMatch("cheatsheet 2", resp);
    }
    
    @Test
    public void deleteCheatSheetWithoutLogin() {
        Response resp = GET("/delete/armed/armed/test-repo");
        assertStatus(Http.StatusCode.FORBIDDEN, resp);
    }
}
