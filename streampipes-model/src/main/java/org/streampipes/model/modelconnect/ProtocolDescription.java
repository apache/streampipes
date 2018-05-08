package org.streampipes.model.modelconnect;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:ProtocolDescription")
@Entity
public class ProtocolDescription extends NamedStreamPipesEntity {


    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:config")
    List<StaticProperty> config;

    public ProtocolDescription() {
    }

    public ProtocolDescription(String uri, String name, String description) {
        super(uri, name, description);
        this.config = new ArrayList<>();
    }

    public ProtocolDescription(String uri, String name, String description, List<StaticProperty> config) {
        super(uri, name, description);
        this.config = config;
    }

    public void addConfig(StaticProperty sp) {
        this.config.add(sp);
    }

    public List<StaticProperty> getConfig() {
        return config;
    }

    public void setConfig(List<StaticProperty> config) {
        this.config = config;
    }
}
