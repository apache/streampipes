package org.streampipes.model.modelconnect;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;

import javax.persistence.Entity;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:DomainPropertyProbability")
@Entity
public class DomainPropertyProbability extends NamedStreamPipesEntity {

    @RdfProperty("sp:domainProperty")
    private String domainProperty;


    @RdfProperty("sp:probability")
    private double probability;

    public DomainPropertyProbability() {
    }

    public DomainPropertyProbability(String domainProperty, double probability) {
        this.domainProperty = domainProperty;
        this.probability = probability;
    }

    public String getDomainProperty() {
        return domainProperty;
    }

    public void setDomainProperty(String domainProperty) {
        this.domainProperty = domainProperty;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
