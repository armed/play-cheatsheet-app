package client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import play.libs.WS;

import models.Image;
import models.Sheet;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.primitives.Bytes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.RequestBuilder;

public class BlobsAndTreesReader {
    
    private static final String blobPattern = "http://github.com/api/v2/json/blob/show/%s/%s/%s";

    private String userName;
    private String repoName;
    private JsonArray blobsAndTrees;
    
    public static String toHTML(String textile) {
        String html = new MarkupParser(new TextileLanguage()).parseToHtml(textile);
        html = html.substring(html.indexOf("<body>") + 6, html.lastIndexOf("</body>"));
        return html;
    }
    
    public BlobsAndTreesReader(String userName, String repoName, JsonArray blobsAndTrees) {
        this.userName = userName;
        this.repoName = repoName;
        this.blobsAndTrees = blobsAndTrees;
    }

    public List<Sheet> getSheets() {
        List<Sheet> sheets = Lists.newArrayList();
        
        for (JsonElement jsonElement : blobsAndTrees) {
            JsonObject obj = (JsonObject) jsonElement;
            
            if (isMimeTypeTextPlain(obj) && isTypeBlob(obj) && endsWithTextile(obj)) {
                Sheet s = new Sheet();
                s.name = getStringProp(obj, "name");
                s.data = toHTML(getStringData(getStringProp(obj, "sha")));
                sheets.add(s);
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
    
    private String getStringProp(JsonObject obj, String prop) {
        return obj.get(prop).getAsString();
    }

    private String getStringData(String sha) {
        return WS.url(blob(sha)).get().getString();
    }
    
    private String blob(String sha) {
        return String.format(blobPattern, userName, repoName, sha);
    }
    
    public List<Image> getImages() {
        List<Image> images = Lists.newArrayList();
        
        for (JsonElement jsonElement : blobsAndTrees) {
            JsonObject obj = (JsonObject) jsonElement;
            
            if (isMimeTypeImage(obj) && isTypeBlob(obj) && isInImagesFolder(obj)) {
                Image img = new Image();
                img.name = getStringProp(obj, "name");
                img.mimeType = getStringProp(obj, "mime_type");
                img.data = getByteData(getStringProp(obj, "sha"), getIntProp(obj, "size"));
                images.add(img);
            }
        }
        
        return images;
    }
    
    private boolean isMimeTypeImage(JsonObject obj) {
        return getStringProp(obj, "mime_type").startsWith("image/");
    }
    
    private boolean isInImagesFolder(JsonObject obj) {
        return getStringProp(obj, "name").startsWith("images/");
    }
    
    private int getIntProp(JsonObject obj, String prop) {
        return obj.get(prop).getAsInt();
    }

    public byte[] getByteData(String sha, int length) {
        try {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(blob(sha));
            client.executeMethod(get);
            byte[] buff = get.getResponseBody();
                        
            return buff;
        } catch (Exception e) {
            throw new RuntimeException("Error reading binary object", e);
        }
    }
}
