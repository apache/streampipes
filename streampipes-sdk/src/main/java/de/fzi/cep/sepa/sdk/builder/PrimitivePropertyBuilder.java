package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.impl.eventproperty.Enumeration;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.eventproperty.QuantitativeValue;
import de.fzi.cep.sepa.model.impl.quality.Accuracy;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.Resolution;
import de.fzi.cep.sepa.sdk.utils.Datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 06.12.2016.
 */
public class PrimitivePropertyBuilder {

    EventPropertyPrimitive eventProperty;
    List<EventPropertyQualityDefinition> qualityDefinitions;

    private PrimitivePropertyBuilder(Datatypes datatype, String runtimeName) {
        this.eventProperty = new EventPropertyPrimitive();
        this.qualityDefinitions = new ArrayList<>();
        this.eventProperty.setRuntimeType(datatype.toString());
        this.eventProperty.setRuntimeName(runtimeName);
    }

    public static PrimitivePropertyBuilder create(Datatypes datatype, String runtimeName) {
        return new PrimitivePropertyBuilder(datatype, runtimeName);
    }

    public PrimitivePropertyBuilder domainProperty(String domainProperty) {
        this.eventProperty.setDomainProperties(Arrays.asList(URI.create(domainProperty)));
        return this;
    }

    public PrimitivePropertyBuilder measurementUnit(URI measurementUnit) {
        this.eventProperty.setMeasurementUnit(measurementUnit);
        return this;
    }

    public PrimitivePropertyBuilder valueSpecification(Float min, Float max, Float step) {
        this.eventProperty.setValueSpecification(new QuantitativeValue(min, max, step));
        return this;
    }

    public PrimitivePropertyBuilder valueSpecification(String label, String description, List<String> allowedValues) {
        this.eventProperty.setValueSpecification(new Enumeration(label, description, allowedValues));
        return this;
    }

    public PrimitivePropertyBuilder label(String label) {
        this.eventProperty.setLabel(label);
        return this;
    }

    public PrimitivePropertyBuilder description(String description) {
        this.eventProperty.setDescription(description);
        return this;
    }

    public PrimitivePropertyBuilder accuracy(Float accuracy, URI measurementUnit) {
        // TODO extend event property
        this.qualityDefinitions.add(new Accuracy(accuracy));
        return this;
    }

    public PrimitivePropertyBuilder resolution(Float resolution, URI measurementUnit) {
        // TODO extend event property
        this.qualityDefinitions.add(new Resolution(resolution));
        return this;
    }


    public EventPropertyPrimitive build() {
        if (qualityDefinitions.size() > 0) {
            this.eventProperty.setEventPropertyQualities(qualityDefinitions);
        }
        return this.eventProperty;
    }

}
