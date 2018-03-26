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

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/\""})
@RdfsClass("sp:AdapterDescriptionList")
@Entity
public class AdapterDescriptionList extends NamedStreamPipesEntity {
    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<AdapterDescription> list;

    public AdapterDescriptionList() {
        super("http://bla.de#3", "", "");
        list = new ArrayList<>();
    }

    public List<AdapterDescription> getList() {
        return list;
    }

    public void setList(List<AdapterDescription> list) {
        this.list = list;
    }
}
