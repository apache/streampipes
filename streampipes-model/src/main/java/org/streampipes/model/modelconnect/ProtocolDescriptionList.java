package org.streampipes.model.modelconnect;


import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:ProtocolDescriptionList")
@Entity
public class ProtocolDescriptionList extends NamedStreamPipesEntity {

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<ProtocolDescription> list;
//    private List<StaticProperty> list;

    @RdfProperty("rdfs:label")
    protected String label;

    public ProtocolDescriptionList() {
        super("http://bla.de#1", "", "");
        list = new ArrayList<>();
    }

    public void addDesctiption(ProtocolDescription protocolDescription) {
        list.add(protocolDescription);
    }

    public ProtocolDescriptionList(List<ProtocolDescription> list) {
        this.list = list;
    }

    public List<ProtocolDescription> getList() {
        return list;
    }

    public void setList(List<ProtocolDescription> list) {
        this.list = list;
    }


}
