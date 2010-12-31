package models;

import java.io.Serializable;
import java.util.List;

import com.vercer.engine.persist.annotation.Child;
import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Key;

import play.modules.twig.TwigModel;

public class Repo extends TwigModel implements Serializable {

    private static final String identifierPattern = "%s:%s";
    
    @Key
    public String identifier;
    public String treeSha;
    @Embed
    public List<Sheet> sheets;
    @Embed
    public List<Image> images;
    
    public static String makeIdentifier(String userName, String repoName) {
        return String.format(identifierPattern, userName, repoName);
    }
}
