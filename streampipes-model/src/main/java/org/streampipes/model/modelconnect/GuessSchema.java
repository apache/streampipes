package org.streampipes.model.modelconnect;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.empire.core.empire.annotation.SupportsRdfIdImpl;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.StaticProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Map;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/\""})
@RdfsClass("sp:GuessSchema")
@Entity
public class GuessSchema extends UnnamedStreamPipesEntity {

    @RdfProperty("sp:eventSchema")
    public EventSchema eventSchema;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:propertyProbabilityList")
    public List<DomainPropertyProbabilityList> propertyProbabilityList;

    public GuessSchema() {
        super();
    }

    public GuessSchema(EventSchema schema, List<DomainPropertyProbabilityList> propertyProbabilityList) {
        super();
        this.eventSchema = schema;
        this.propertyProbabilityList = propertyProbabilityList;
    }



    public EventSchema getEventSchema() {
        return eventSchema;
    }

    public void setEventSchema(EventSchema eventSchema) {
        this.eventSchema = eventSchema;
    }

    public List<DomainPropertyProbabilityList> getPropertyProbabilityList() {
        return propertyProbabilityList;
    }

    public void setPropertyProbabilityList(List<DomainPropertyProbabilityList> propertyProbabilityList) {
        this.propertyProbabilityList = propertyProbabilityList;
    }
}
