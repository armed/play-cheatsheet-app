import java.util.Map;

import models.Author;
import models.Repo;

import org.junit.Before;
import org.junit.Test;

import play.modules.twig.Twig;
import play.test.BaseTest;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Lists;

public class RepoModelShouldReturn extends BaseTest {

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
        storedRepo.sheets = Lists.newArrayList("test", "test2");
        storedRepoKey = storedRepo.store();
    }

    @Test
    public void nullWhenWrongParamsFromFindByAuthorEmailAndRepo() {
        play.test.BaseTest.
                assertNull(Repo.findByAuthorEmailAndRepo(wrongAuthor.email, "armed", "test-repo"));
        assertNull(Repo.findByAuthorEmailAndRepo(author.email, "wrongGithubUser", "test-repo"));
        assertNull(Repo.findByAuthorEmailAndRepo(author.email, "armed", "wrongRepoName"));
    }

    @Test
    public void nullWhenWrongParamsFromFindByAuthorNameAndRepo() {
        assertNull(Repo.findByAuthorNameAndRepo(wrongAuthor.name, "armed", "test-repo"));
        assertNull(Repo.findByAuthorNameAndRepo(author.name, "wrongGithubUser", "test-repo"));
        assertNull(Repo.findByAuthorNameAndRepo(author.name, "armed", "wrongRepoName"));
    }

    @Test
    public void instanceWhenCorrectParamsFromFindByAuthorNameAndRepo() {
        Repo repo = Repo.findByAuthorEmailAndRepo(author.email, "armed", "test-repo");

        assertNotNull(repo);

        assertNotNull(Repo.findByAuthorNameAndRepo("armed", "armed", "test-repo"));

        assertEquals(author.email, repo.author.email);
        assertEquals(storedRepo.githubUser, "armed");
        assertEquals(storedRepo.repoName, "test-repo");
    }

    @Test
    public void instanceWhenCorrectParamsFromFindByAuthorAndRepo() {
        Repo repo = Repo.findByAuthorEmailAndRepo(author.email, "armed", "test-repo");

        assertNotNull(repo);

        assertEquals(author.email, repo.author.email);
        assertEquals(storedRepo.githubUser, "armed");
        assertEquals(storedRepo.repoName, "test-repo");
    }

    @Test
    public void mapWithKeysAndInstancesFromFindAll() {
        Map<String, Repo> reposWithKeys = Repo.findAllWithKeys();

        assertNotNull(reposWithKeys);
        assertEquals(1, reposWithKeys.size());
    }

    @Test
    public void instanceFromFindByIdShould() {
        Map<String, Repo> reposWithKeys = Repo.findAllWithKeys();

        Repo repo = Repo.findById(reposWithKeys.keySet().iterator().next());
        assertNotNull(repo);
    }
}
