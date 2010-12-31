package models;

import java.io.Serializable;

import com.google.appengine.api.datastore.Blob;
import com.vercer.engine.persist.annotation.Type;

import play.modules.twig.TwigModel;

public class Image extends TwigModel implements Serializable {

    public String name;
    public String mimeType;
    
    @Type(Blob.class)
    public byte[] data;
}
