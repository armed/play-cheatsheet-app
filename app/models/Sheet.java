package models;

import java.io.Serializable;

import com.google.appengine.api.datastore.Text;
import com.vercer.engine.persist.annotation.Parent;
import com.vercer.engine.persist.annotation.Type;

import play.modules.twig.TwigModel;

public class Sheet extends TwigModel {

    public String name;
    @Type(Text.class)
    public String data;
}
