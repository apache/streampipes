package org.streampipes.model.modelconnect;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.base.UnnamedStreamPipesEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/\""})
@RdfsClass("sp:DomainPropertyProbabilityList")
@Entity
public class DomainPropertyProbabilityList extends UnnamedStreamPipesEntity {

    @RdfProperty("sp:runtimeName")
    private String runtimeName;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:list")
    private List<DomainPropertyProbability> list;

    public DomainPropertyProbabilityList() {
        super();
        list = new ArrayList<>();
    }

    public List<DomainPropertyProbability> getList() {
        return list;
    }

    public void setList(List<DomainPropertyProbability> list) {
        this.list = list;
    }

    public String getRuntimeName() {
        return runtimeName;
    }

    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
    }

    public void addDomainPropertyProbability(DomainPropertyProbability property) {
        this.list.add(property);
    }
}
