import static org.junit.Assert.*;

import java.util.List;

import models.Repo;
import models.Sheet;

import org.junit.Test;

import play.test.FunctionalTest;

import client.GithubClient;


public class GithubClientTest extends FunctionalTest {

    @Test
    public void loadRemoteRepo() throws Exception {
        String userName = "armed";
        String repoName = "test-repo";
        
        List<Sheet> sheets = new GithubClient(userName, repoName).getSheets();
        
        assertNotNull(sheets);
        
        assertEquals(26, sheets.size());
        
        for (Sheet sheet : sheets) {
            System.out.println(sheet.data);
        }
    }
}
