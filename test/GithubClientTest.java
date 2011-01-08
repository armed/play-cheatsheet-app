import java.util.List;

import org.junit.Test;

import play.test.FunctionalTest;
import client.GithubClient;

public class GithubClientTest extends FunctionalTest {

    @Test
    public void loadRemoteRepo() throws Exception {
        String userName = "armed";
        String repoName = "test-repo";

        List<String> sheets = new GithubClient(userName, repoName).getSheets();

        assertNotNull(sheets);

        assertEquals(26, sheets.size());
    }
}
