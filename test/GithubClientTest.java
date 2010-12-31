import static org.junit.Assert.*;
import models.Repo;
import models.Sheet;

import org.junit.Test;

import play.test.FunctionalTest;

import client.GithubClient;


public class GithubClientTest extends FunctionalTest {

    @Test
    public void loadRemoteRepo() {
        String userName = "armed";
        String repoName = "test-repo";
        
        Repo r = new GithubClient(userName, repoName).getLatest();
        
        assertNotNull(r);
        
        assertEquals(2, r.sheets.size());
        
        for (Sheet sheet : r.sheets) {
            System.out.println(sheet.data);
        }
        
        assertEquals(1, r.images.size());
        assertEquals("images/hba.png", r.images.get(0).name);
        
        assertEquals(66240, r.images.get(0).data.length);
    }
}
