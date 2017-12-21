package org.streampipes.sdk.builder;

import org.streampipes.model.schema.Enumeration;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.model.schema.QuantitativeValue;
import org.streampipes.model.quality.Accuracy;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.quality.Resolution;
import org.streampipes.sdk.utils.Datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrimitivePropertyBuilder {

    EventPropertyPrimitive eventProperty;
    List<EventPropertyQualityDefinition> qualityDefinitions;

    private PrimitivePropertyBuilder(Datatypes datatype, String runtimeName) {
        this.eventProperty = new EventPropertyPrimitive();
        this.qualityDefinitions = new ArrayList<>();
        this.eventProperty.setRuntimeType(datatype.toString());
        this.eventProperty.setRuntimeName(runtimeName);
    }

    /**
     * A builder class helping to define advanced primitive properties. For simple property definitions, you can also
     * use {@link org.streampipes.sdk.helpers.EpProperties}.
     * @param datatype The primitive {@link org.streampipes.sdk.utils.Datatypes} definition of the new property.
     * @param runtimeName The name of the property at runtime (e.g., the field name of the JSON primitive.
     * @return this
     */
    public static PrimitivePropertyBuilder create(Datatypes datatype, String runtimeName) {
        return new PrimitivePropertyBuilder(datatype, runtimeName);
    }

    /**
     * Specifies the semantics of the property (e.g., whether a double value stands for a latitude coordinate).
     * @param domainProperty The domain property as a String. The domain property should reflect an URI. Use some
     *                       existing vocabulary from {@link org.streampipes.vocabulary} or create your own.
     * @return
     */
    public PrimitivePropertyBuilder domainProperty(String domainProperty) {
        this.eventProperty.setDomainProperties(Arrays.asList(URI.create(domainProperty)));
        return this;
    }

    /**
     * Defines the measurement unit (e.g., tons) of the event property.
     * @param measurementUnit The measurement unit as a URI from a vocabulary (e.g., QUDT).
     * @return
     */
    public PrimitivePropertyBuilder measurementUnit(URI measurementUnit) {
        this.eventProperty.setMeasurementUnit(measurementUnit);
        return this;
    }

    /**
     * Defines the value range. The data type of the event property must be a number.
     * @param min The minimum value the property can have at runtime.
     * @param max The maximum value the property can have at runtime.
     * @param step The expected granularity the property has at runtime.
     * @return this
     */
    public PrimitivePropertyBuilder valueSpecification(Float min, Float max, Float step) {
        this.eventProperty.setValueSpecification(new QuantitativeValue(min, max, step));
        return this;
    }

    /**
     * Defines the value range in form of an enumeration. The data type of the event property must be of type String
     * or Number.
     * @param label A human-readable label describing this enumeration.
     * @param description A human-readable description of the enumeration.
     * @param allowedValues A list of allowed values of the event property at runtime.
     * @return this
     */
    public PrimitivePropertyBuilder valueSpecification(String label, String description, List<String> allowedValues) {
        this.eventProperty.setValueSpecification(new Enumeration(label, description, allowedValues));
        return this;
    }

    /**
     * Assigns a human-readable label to the event property. The label is used in the StreamPipes UI for better
     * explaining  users the meaning of the property.
     * @param label
     * @return this
     */
    public PrimitivePropertyBuilder label(String label) {
        this.eventProperty.setLabel(label);
        return this;
    }

    /**
     * Assigns a human-readable description to the event property. The description is used in the StreamPipes UI for
     * better explaining users the meaning of the property.
     * @param description
     * @return this
     */
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

    /**
     * Assigns a property scope to the event property.
     * @param propertyScope The {@link org.streampipes.model.schema.PropertyScope}.
     * @return this
     */
    public PrimitivePropertyBuilder scope(PropertyScope propertyScope) {
        this.eventProperty.setPropertyScope(propertyScope.name());
        return this;
    }


    public EventPropertyPrimitive build() {
        if (qualityDefinitions.size() > 0) {
            this.eventProperty.setEventPropertyQualities(qualityDefinitions);
        }
        return this.eventProperty;
    }

}
