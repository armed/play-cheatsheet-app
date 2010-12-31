package models;

import java.util.List;

import com.vercer.engine.persist.annotation.Child;
import com.vercer.engine.persist.annotation.Embed;
import com.vercer.engine.persist.annotation.Key;

import play.modules.twig.TwigModel;

public class Repo extends TwigModel {

    @Key
    public String identifier;
    public String treeSha;
    @Embed
    public List<Sheet> sheets;
    @Embed
    public List<Image> images;
}
