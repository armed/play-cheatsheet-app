package client;

import java.io.IOException;
import java.util.List;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import models.Sheet;

import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class CheatSheetsReader {

    private static final String blobPattern = "http://github.com/api/v2/json/blob/show/%s/%s/%s";
    private static final int MAX_SHEETS = 100;
    private static final int SHEET_MAX_SIZE = 10000;

    private String userName;
    private String repoName;
    private JsonArray blobsAndTrees;

    private static String toHTML(String textile) {
        String html = new MarkupParser(new TextileLanguage()).parseToHtml(textile);
        html = html.substring(html.indexOf("<body>") + 6, html.lastIndexOf("</body>"));
        return html;
    }

    public CheatSheetsReader(String userName, String repoName, JsonArray blobsAndTrees) {
        this.userName = userName;
        this.repoName = repoName;
        this.blobsAndTrees = blobsAndTrees;
    }

    public List<Sheet> getSheets() throws GithubException {
        List<Sheet> sheets = Lists.newArrayList();

        for (JsonElement jsonElement : blobsAndTrees) {
            JsonObject obj = (JsonObject) jsonElement;

            if (isMimeTypeTextPlain(obj) && isTypeBlob(obj) && endsWithTextile(obj)
                    && notBiggerThanMaxAllowedSize(obj)) {
                Sheet s = new Sheet();
                s.name = getStringProp(obj, "name");
                s.data = toHTML(getSheetTextBySha(getStringProp(obj, "sha")));
                sheets.add(s);
            }

            if (sheets.size() > MAX_SHEETS) {
                break;
            }
        }

        return sheets;
    }

    private boolean isMimeTypeTextPlain(JsonObject obj) {
        return getStringProp(obj, "mime_type").equals("text/plain");
    }

    private boolean isTypeBlob(JsonObject obj) {
        return getStringProp(obj, "type").equals("blob");
    }

    private boolean endsWithTextile(JsonObject obj) {
        return getStringProp(obj, "name").endsWith(".textile");
    }

    private boolean notBiggerThanMaxAllowedSize(JsonObject obj) {
        return getIntProp(obj, "size") < SHEET_MAX_SIZE;
    }

    private String getStringProp(JsonObject obj, String prop) {
        return obj.get(prop).getAsString();
    }

    private int getIntProp(JsonObject obj, String prop) {
        return obj.get(prop).getAsInt();
    }

    private String getSheetTextBySha(String sha) throws GithubException {
        try {
            return new ClientResource(blobUrl(sha)).get(MediaType.TEXT_PLAIN).getText();
        } catch (IOException e) {
            throw new GithubException("Error retrieving cheat sheet from github");
        }
    }

    private String blobUrl(String sha) {
        return String.format(blobPattern, userName, repoName, sha);
    }
}
