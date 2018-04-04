package org.streampipes.model.modelconnect;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.base.UnnamedStreamPipesEntity;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:DomainPropertyProbability")
@Entity
public class DomainPropertyProbability extends UnnamedStreamPipesEntity {

    @RdfProperty("sp:domainProperty")
    private String domainProperty;


    @RdfProperty("sp:probability")
    private String probability;

    public DomainPropertyProbability() {
        super();
    }

    public DomainPropertyProbability(String domainProperty,  String probability) {
        this.domainProperty = domainProperty;
        this.probability = probability;
    }

    public String getDomainProperty() {
        return domainProperty;
    }

    public void setDomainProperty(String domainProperty) {
        this.domainProperty = domainProperty;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }
}
