package models;

import java.io.Serializable;

import com.google.appengine.api.datastore.Text;
import com.vercer.engine.persist.annotation.Activate;
import com.vercer.engine.persist.annotation.Type;

@Activate
public class Sheet implements Serializable {

    @Type(String.class)
    public String name;
    @Type(Text.class)
    public String data;
}
