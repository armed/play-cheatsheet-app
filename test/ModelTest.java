import java.util.List;

import models.Image;
import models.Repo;
import models.Sheet;

import org.junit.Test;

import play.modules.twig.Twig;
import play.test.FunctionalTest;

import client.GithubClient;

import com.google.common.collect.Lists;


public class ModelTest extends FunctionalTest {

    @Test
    public void saveDataTest() {
        Sheet s = new Sheet();
        s.data = "a sample data";
        s.name = "First Sheet";
        
        Image i = new Image();
        i.name = "test img";
        i.data = new byte[] {1, 2, 3, 4, 5};
        
        Repo r = new Repo();
        r.identifier = "https://github.com/armed/test";
        r.sheets = Lists.newArrayList();
        r.sheets.add(s);
        r.images = Lists.newArrayList();
        r.images.add(i);
        
        r.store();
        
        List<Repo> repos = Lists.newArrayList(Twig.find(Repo.class));

        assertEquals(1, repos.size());
        assertEquals(1, repos.get(0).sheets.size());
        assertEquals(1, repos.get(0).images.size());
    }
}
