package org.streampipes.sdk.builder;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.staticproperty.AnyStaticProperty;
import org.streampipes.model.staticproperty.CollectionStaticProperty;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyNary;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.PropertyValueSpecification;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;
import org.streampipes.vocabulary.XSD;
import org.streampipes.sdk.helpers.Label;
import org.streampipes.sdk.helpers.StreamIdentifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 04.12.2016.
 */
public abstract class AbstractProcessingElementBuilder<BU extends AbstractProcessingElementBuilder<BU, T>, T extends ConsumableStreamPipesEntity> extends AbstractPipelineElementBuilder<BU, T> {

  protected List<StaticProperty> staticProperties;
  protected List<SpDataStream> streamRequirements;

  protected List<EventProperty> stream1Properties;
  protected List<EventProperty> stream2Properties;

  protected EventGrounding supportedGrounding;

  protected boolean stream1 = false;
  protected boolean stream2 = false;


  protected AbstractProcessingElementBuilder(String id, String label, String description, T element) {
    super(id, label, description, element);
    this.streamRequirements = new ArrayList<>();
    this.staticProperties = new ArrayList<>();
    this.stream1Properties = new ArrayList<>();
    this.stream2Properties = new ArrayList<>();
    this.supportedGrounding = new EventGrounding();
  }

  public BU requiredStream(SpDataStream stream) {
    this.streamRequirements.add(stream);
    return me();
  }

  public BU requiredStaticProperty(StaticProperty staticProperty) {
    this.staticProperties.add(staticProperty);
    return me();
  }

  public BU requiredOntologyConcept(Label label, SupportedProperty...
          supportedOntologyProperties) {
    DomainStaticProperty dsp = prepareStaticProperty(label, new DomainStaticProperty());
    dsp.setSupportedProperties(Arrays.asList(supportedOntologyProperties));
    this.staticProperties.add(dsp);

    return me();
  }

  public BU requiredOntologyConcept(Label label, String requiredConceptUri, SupportedProperty...
          supportedOntologyProperties) {
    DomainStaticProperty dsp = prepareStaticProperty(label, new DomainStaticProperty());
    dsp.setSupportedProperties(Arrays.asList(supportedOntologyProperties));
    dsp.setRequiredClass(requiredConceptUri);
    this.staticProperties.add(dsp);

    return me();
  }

  public BU requiredParameterAsCollection(Label label, StaticProperty staticProperty) {
    CollectionStaticProperty collection = prepareStaticProperty(label, new
            CollectionStaticProperty());
    collection.setMembers(Arrays.asList(staticProperty));
    this.staticProperties.add(collection);

    return me();
  }

  public BU requiredTextParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._string.toString()));

    return me();
  }

  public BU requiredHtmlInputParameter(Label label) {
    FreeTextStaticProperty fsp = new FreeTextStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    fsp.setMultiLine(true);
    fsp.setHtmlAllowed(true);
    fsp.setPlaceholdersSupported(true);
    this.staticProperties.add(fsp);

    return me();
  }

  public BU requiredTextParameter(Label label, boolean multiLine, boolean placeholdersSupported) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label.getInternalId(),
            label.getLabel(),
            label.getDescription(),
            XSD._string.toString());
    if (multiLine) {
      fsp.setMultiLine(true);
    }
    if (placeholdersSupported) {
      fsp.setPlaceholdersSupported(true);
    }
    this.staticProperties.add(fsp);

    return me();
  }

  public BU requiredIntegerParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._integer.toString()));

    return me();
  }

  public BU requiredIntegerParameter(String internalId, String label, String description,
                                     Integer defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._integer.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  public BU requiredFloatParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._double.toString()));

    return me();
  }

  public BU requiredFloatParameter(String internalId, String label, String description, Float
          defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._double.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  public BU requiredSingleValueSelection(String internalId, String label, String description,
                                         Option... options) {
    return requiredSingleValueSelection(internalId, label, description, Arrays.asList(options));
  }

  public BU requiredSingleValueSelection(String internalId, String label, String description,
                                         List<Option> options) {
    OneOfStaticProperty osp = new OneOfStaticProperty(internalId, label, description);
    osp.setOptions(options);

    this.staticProperties.add(osp);
    return me();

  }

  public BU requiredMultiValueSelection(String internalId, String label, String description,
                                        Option... options) {
    return requiredMultiValueSelection(internalId, label, description, Arrays.asList(options));
  }

  public BU requiredMultiValueSelection(String internalId, String label, String description,
                                        List<Option> options) {
    AnyStaticProperty asp = new AnyStaticProperty(internalId, label, description);
    asp.setOptions(options);

    this.staticProperties.add(asp);
    return me();

  }

  public BU requiredIntegerParameter(String internalId, String label, String description, Integer min, Integer max, Integer step) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._integer.toString());

    PropertyValueSpecification valueSpecification = new PropertyValueSpecification(min, max, step);
    fsp.setValueSpecification(valueSpecification);
    this.staticProperties.add(fsp);
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

  public BU naryMappingPropertyWithoutRequirement(String internalName, String label, String
          description) {
    this.staticProperties.add(new MappingPropertyNary(internalName, label, description));
    return me();
  }

  public BU unaryMappingPropertyWithoutRequirement(String internalName, String label, String
          description) {
    this.staticProperties.add(new MappingPropertyUnary(internalName, label, description));
    return me();
  }

  public BU unaryMappingProperty(StreamIdentifier streamIdentifier, Integer propertyIndex, Label label) {
    EventProperty propertyRequirement;

    // TODO we need proper exception handling for the sdk
    if (streamIdentifier == StreamIdentifier.Stream0) {
      propertyRequirement = this.stream1Properties.get(propertyIndex);

    } else {
      propertyRequirement = this.stream2Properties.get(propertyIndex);
    }

    this.staticProperties.add(new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), label
            .getInternalId(), label.getLabel(), label.getDescription()));

    return me();
  }

  public BU requiredPropertyStream1WithUnaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream1Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  public BU requiredPropertyStream1WithNaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream1Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  public BU requiredPropertyStream2(EventProperty propertyRequirement) {
    this.stream2Properties.add(propertyRequirement);

    return me();
  }

  public BU requiredPropertyStream2WithUnaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream2Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  public BU requiredPropertyStream2WithNaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream2Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  public BU supportedFormats(TransportFormat... format) {
    return supportedFormats(Arrays.asList(format));
  }

  public BU supportedFormats(List<TransportFormat> formats) {
    this.supportedGrounding.setTransportFormats(formats);
    return me();
  }

  public BU supportedProtocols(TransportProtocol... protocol) {
    return supportedProtocols(Arrays.asList(protocol));
  }

  public BU supportedProtocols(List<TransportProtocol> protocols) {
    this.supportedGrounding.setTransportProtocols(protocols);
    return me();
  }

  public BU setStream1() {
    stream1 = true;
    return me();
  }

  public BU setStream2() {
    stream2 = true;
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

    if (stream1Properties.size() > 0 || stream1) {
      this.streamRequirements.add(buildStream(stream1Properties));
    }

    if (stream2Properties.size() > 0 || stream2) {
      this.streamRequirements.add(buildStream(stream2Properties));
    }

    this.elementDescription.setSupportedGrounding(supportedGrounding);
    this.elementDescription.setSpDataStreams(streamRequirements);

  }

  private SpDataStream buildStream(List<EventProperty> streamProperties) {
    SpDataStream stream = new SpDataStream();
    stream.setEventSchema(new EventSchema(streamProperties));
    return stream;
  }

}
