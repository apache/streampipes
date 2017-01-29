package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.PropertyValueSpecification;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.vocabulary.XSD;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 04.12.2016.
 */
public abstract class AbstractProcessingElementBuilder<BU extends AbstractProcessingElementBuilder<BU, T>, T extends ConsumableSEPAElement> extends AbstractPipelineElementBuilder<BU, T> {

    protected List<StaticProperty> staticProperties;
    protected List<EventStream> streamRequirements;

    protected List<EventProperty> stream1Properties;
    protected List<EventProperty> stream2Properties;

    protected EventGrounding supportedGrounding;

    protected AbstractProcessingElementBuilder(String id, String label, String description, T element) {
        super(id, label, description, element);
        this.streamRequirements = new ArrayList<>();
        this.staticProperties = new ArrayList<>();
        this.stream1Properties = new ArrayList<>();
        this.stream2Properties = new ArrayList<>();
        this.supportedGrounding = new EventGrounding();
    }

    public BU requiredStream(EventStream stream) {
        this.streamRequirements.add(stream);
        return me();
    }

    public BU requiredStaticProperty(StaticProperty staticProperty) {
        this.staticProperties.add(staticProperty);
        return me();
    }

    public BU requiredTextParameter(String internalId, String label, String description) {
        this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
                label,
                description,
                XSD._string.toString()));

        return me();
    }

    public BU requiredIntegerParameter(String internalId, String label, String description) {
        this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
                label,
                description,
                XSD._integer.toString()));

        return me();
    }

    public BU requiredFloatParameter(String internalId, String label, String description) {
        this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
                label,
                description,
                XSD._double.toString()));

        return me();
    }

    public BU requiredIntegerParameter(String internalId, String label, String description, Integer min, Integer max, Integer step) {
        FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
                label,
                description,
                XSD._integer.toString());

        PropertyValueSpecification valueSpecification = new PropertyValueSpecification(min, max, step);
        fsp.setValueSpecification(valueSpecification);
        return me();
    }

    public BU requiredFloatParameter(String internalId, String label, String description, Float min, Float max, Float step) {
        FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
                label,
                description,
                XSD._double.toString());

        PropertyValueSpecification valueSpecification = new PropertyValueSpecification(min, max, step);
        fsp.setValueSpecification(valueSpecification);
        this.staticProperties.add(fsp);

        return me();
    }

    public BU requiredPropertyStream1(EventProperty propertyRequirement) {
        this.stream1Properties.add(propertyRequirement);

        return me();
    }

    public BU stream1PropertyRequirementWithUnaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
        this.stream1Properties.add(propertyRequirement);
        this.staticProperties.add(new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
        return me();
    }

    public BU stream1PropertyRequirementWithNaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
        this.stream1Properties.add(propertyRequirement);
        this.staticProperties.add(new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
        return me();
    }

    public BU requiredPropertyStream2(EventProperty propertyRequirement) {
        this.stream2Properties.add(propertyRequirement);

        return me();
    }

    public BU stream2PropertyRequirementWithUnaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
        this.stream2Properties.add(propertyRequirement);
        this.staticProperties.add(new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
        return me();
    }

    public BU stream2PropertyRequirementWithNaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
        this.stream2Properties.add(propertyRequirement);
        this.staticProperties.add(new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
        return me();
    }

    public BU supportedFormats(TransportFormat... format) {
        this.supportedGrounding.setTransportFormats(Arrays.asList(format));
        return me();
    }

    public BU supportedProtocols(TransportProtocol... protocol) {
        this.supportedGrounding.setTransportProtocols(Arrays.asList(protocol));
        return me();
    }

    private FreeTextStaticProperty prepareFreeTextStaticProperty(String internalId, String label, String description, String type) {
        return new FreeTextStaticProperty(internalId,
                label,
                description,
                URI.create(type));
    }


    @Override
    public void prepareBuild() {
        this.elementDescription.setStaticProperties(staticProperties);

        if (stream1Properties.size() > 0) {
            this.streamRequirements.add(buildStream(stream1Properties));
        }

        if (stream2Properties.size() > 0) {
            this.streamRequirements.add(buildStream(stream2Properties));
        }

        this.elementDescription.setSupportedGrounding(supportedGrounding);
        this.elementDescription.setEventStreams(streamRequirements);

    }

    private EventStream buildStream(List<EventProperty> streamProperties) {
        EventStream stream = new EventStream();
        stream.setEventSchema(new EventSchema(streamProperties));
        return stream;
    }
}
