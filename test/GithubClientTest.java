import java.util.List;

import org.junit.Test;

import play.test.BaseTest;
import client.GithubClient;

public class GithubClientTest extends BaseTest {

    @Test
    public void loadRemoteRepo() throws Exception {
        String userName = "armed";
        String repoName = "test-repo";

        List<String> sheets = new GithubClient(userName, repoName).getSheets();

        assertNotNull(sheets);

        assertEquals(26, sheets.size());
    }
}
