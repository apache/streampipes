/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sdk.builder;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.model.staticproperty.AnyStaticProperty;
import org.streampipes.model.staticproperty.CollectionStaticProperty;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyNary;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.PropertyValueSpecification;
import org.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;
import org.streampipes.sdk.helpers.CollectedStreamRequirements;
import org.streampipes.sdk.helpers.Label;
import org.streampipes.sdk.helpers.StreamIdentifier;
import org.streampipes.vocabulary.XSD;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredStream(SpDataStream stream) {
    this.streamRequirements.add(stream);
    return me();
  }

  /**
   * Set a new stream requirement by adding restrictions on this stream. Use
   * {@link StreamRequirementsBuilder} to create requirements for a single stream.
   * @param streamRequirements: A bundle of collected {@link CollectedStreamRequirements}
   * @return this
   */
  public BU requiredStream(CollectedStreamRequirements streamRequirements) {
    this.streamRequirements.add(streamRequirements.getStreamRequirements());
    this.staticProperties.addAll(streamRequirements.getMappingProperties());

    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream1WithNaryMapping(EventProperty propertyRequirement, Label label, PropertyScope
          propertyScope) {
    this.stream1Properties.add(propertyRequirement);
    MappingPropertyNary mp = new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), label
            .getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.staticProperties.add(mp);
    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream2(EventProperty propertyRequirement) {
    this.stream2Properties.add(propertyRequirement);

    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream2WithUnaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream2Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream2WithNaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream2Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream1(EventProperty propertyRequirement) {
    this.stream1Properties.add(propertyRequirement);

    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream1WithUnaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream1Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream1WithNaryMapping(EventProperty propertyRequirement, String internalName, String label, String description) {
    this.stream1Properties.add(propertyRequirement);
    this.staticProperties.add(new MappingPropertyNary(URI.create(propertyRequirement.getElementId()), internalName, label, description));
    return me();
  }

  /**
   * Set a new event property requirement linked to a unary mapping.
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public BU requiredPropertyStream1WithUnaryMapping(EventProperty propertyRequirement, Label label, PropertyScope
          propertyScope) {
    this.stream1Properties.add(propertyRequirement);
    MappingPropertyUnary mp = new MappingPropertyUnary(URI.create(propertyRequirement.getElementId()),
            label.getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.staticProperties.add(mp);
    return me();
  }


  /**
   *
   * @param staticProperty: The required static property (e.g., user input as shown in the StreamPipes UI
   * @return BU
   */
  public BU requiredStaticProperty(StaticProperty staticProperty) {
    this.staticProperties.add(staticProperty);
    return me();
  }

  /**
   * Defines the requirement for an instance that is defined in the knowledge base.
   * @param label: A human-readable label that describes the required static property.
   * @param supportedOntologyProperties: All RDF properties any instance in the knowledge base must provide. Use
   * {@link org.streampipes.sdk.helpers.OntologyProperties} to assign supported properties.
   * @return
   */
  public BU requiredOntologyConcept(Label label, SupportedProperty...
          supportedOntologyProperties) {
    DomainStaticProperty dsp = prepareStaticProperty(label, new DomainStaticProperty());
    dsp.setSupportedProperties(Arrays.asList(supportedOntologyProperties));
    this.staticProperties.add(dsp);

    return me();
  }

  /**
   *
   * @param label: A human-readable label that describes the required static property.
   * @param requiredConceptUri: Limits the search for matching instance in the knowledge base to an instance of this
   *                          concept.
   * @param supportedOntologyProperties: All RDF properties any instance of the provided concept must provide. Use
   * {@link org.streampipes.sdk.helpers.OntologyProperties} to assign supported properties.
   * @return
   */
  public BU requiredOntologyConcept(Label label, String requiredConceptUri, SupportedProperty...
          supportedOntologyProperties) {
    DomainStaticProperty dsp = prepareStaticProperty(label, new DomainStaticProperty());
    dsp.setSupportedProperties(Arrays.asList(supportedOntologyProperties));
    dsp.setRequiredClass(requiredConceptUri);
    this.staticProperties.add(dsp);

    return me();
  }

  /**
   *
   * @param label: A human-readable label that describes the required static property.
   * @param staticProperty
   * @return
   */
  public BU requiredParameterAsCollection(Label label, StaticProperty staticProperty) {
    CollectionStaticProperty collection = prepareStaticProperty(label, new
            CollectionStaticProperty());
    collection.setMembers(Arrays.asList(staticProperty));
    this.staticProperties.add(collection);

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

  /**
   * @deprecated use {@link #requiredTextParameter(Label)}
   * @param internalId
   * @param label
   * @param description
   * @return
   */
  public BU requiredTextParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._string.toString()));

    return me();
  }

  /**
   * Assigns a new text-based configuration parameter (a string) which is required by the pipeline
   * element.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public BU requiredTextParameter(Label label) {
    this.staticProperties.add(prepareFreeTextStaticProperty(label, XSD._string.toString()));

    return me();
  }


  /**
   * @deprecated Use {@link #requiredTextParameter(Label, String)}
   * @param internalId
   * @param label
   * @param description
   * @param linkedMappingPropertyInternalName
   * @return this
   */
  public BU requiredTextParameter(String internalId, String label, String description, String
          linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._string.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param linkedMappingPropertyInternalName The inernalId of the {@link org.streampipes.model.staticproperty.MappingProperty}
   * @return this
   */
  public BU requiredTextParameter(Label label, String
          linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD._string.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time. The
   * input field generated in the StreamPipes UI allows to enter HTML content (and an HTML Wysiwyg editor will be
   * rendered).
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return this
   */
  public BU requiredHtmlInputParameter(Label label) {
    FreeTextStaticProperty fsp = new FreeTextStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    fsp.setMultiLine(true);
    fsp.setHtmlAllowed(true);
    fsp.setPlaceholdersSupported(true);
    this.staticProperties.add(fsp);

    return me();
  }

  /**
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param multiLine Defines whether the input dialog allows multiple lines.
   * @param placeholdersSupported Defines whether placeholders are supported, i.e., event property field names that
   *                              are replaced with the actual value at pipeline execution time.
   * @return this
   */
  public BU requiredTextParameter(Label label, boolean multiLine, boolean placeholdersSupported) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD._string.toString());
    if (multiLine) {
      fsp.setMultiLine(true);
    }
    if (placeholdersSupported) {
      fsp.setPlaceholdersSupported(true);
    }
    this.staticProperties.add(fsp);

    return me();
  }

  /**
   * @deprecated Use {@link #requiredIntegerParameter(Label)} instead
   * @param internalId
   * @param label
   * @param description
   * @return
   */
  public BU requiredIntegerParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._integer.toString()));

    return me();
  }

  /**
   * Assigns a new number-based configuration parameter (an integer) which is required by the pipeline
   * element.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public BU requiredIntegerParameter(Label label) {
    this.staticProperties.add(prepareFreeTextStaticProperty(label, XSD._integer.toString()));

    return me();
  }

  /**
   * @dprecated use {@link #requiredIntegerParameter(Label, String)} instead
   * @param internalId
   * @param label
   * @param description
   * @param linkedMappingPropertyInternalName
   * @return
   */
  public BU requiredIntegerParameter(String internalId, String label, String description, String
          linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._integer.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a number-based configuration parameter of type integer provided by pipeline developers at pipeline
   * authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param linkedMappingPropertyInternalName The inernalId of the {@link org.streampipes.model.staticproperty.MappingProperty}
   * @return this
   */
  public BU requiredIntegerParameter(Label label, String
          linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD._integer.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }


  /**
   * @deprecated Use {@link #requiredIntegerParameter(Label, Integer)} instead
   * @param internalId
   * @param label
   * @param description
   * @param defaultValue
   * @return
   */
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

  /**
   * Defines a number-based configuration parameter of type integer provided by pipeline developers at pipeline
   * authoring time and initializes the parameter with a default value.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param defaultValue The default integer value.
   * @return this
   */
  public BU requiredIntegerParameter(Label label,
                                     Integer defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label,
            XSD._integer.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * @deprecated Use {@link #requiredFloatParameter(Label)} instead.
   * @param internalId
   * @param label
   * @param description
   * @return
   */
  public BU requiredFloatParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._double.toString()));

    return me();
  }

  /**
   * Assigns a new number-based configuration parameter (a float) which is required by the pipeline
   * element.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public BU requiredFloatParameter(Label label) {
    this.staticProperties.add(prepareFreeTextStaticProperty(label,
            XSD._double.toString()));

    return me();
  }

  /**
   * Defines a number-based configuration parameter of type float provided by pipeline developers at pipeline
   * authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   * @deprecated use {@link #requiredFloatParameter(Label, String)}
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param linkedMappingPropertyInternalName The inernalId of the {@link org.streampipes.model.staticproperty.MappingProperty}
   * @return this
   */
  public BU requiredFloatParameter(String internalId, String label, String description, String
          linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
            label,
            description,
            XSD._double.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a number-based configuration parameter of type float provided by pipeline developers at pipeline
   * authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param linkedMappingPropertyInternalName The inernalId of the {@link org.streampipes.model.staticproperty.MappingProperty}
   * @return this
   */
  public BU requiredFloatParameter(Label label, String
          linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD._double.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * @deprecated Use {@link #requiredFloatParameter(Label, Float)} instead.
   * @param internalId
   * @param label
   * @param description
   * @param defaultValue
   * @return this
   */
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

  /**
   * Defines a number-based configuration parameter of type float provided by pipeline developers at pipeline
   * authoring time and initializes the parameter with a default value.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param defaultValue The default integer value.
   * @return this
   */
  public BU requiredFloatParameter(Label label, Float
          defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD._double.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * @deprecated Use {@link #requiredSingleValueSelection(Label, Option...)} instead.
   * @param options An arbitrary number of {@link org.streampipes.model.staticproperty.Option} elements. Use
   * {@link org.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public BU requiredSingleValueSelection(String internalId, String label, String description,
                                         Option... options) {
    return requiredSingleValueSelection(internalId, label, description, Arrays.asList(options));
  }


  /**
   * Defines a configuration parameter that lets pipeline developers select from a list of pre-defined configuration
   * options. The parameter will be rendered as a RadioGroup in the StreamPipes UI.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param options An arbitrary number of {@link org.streampipes.model.staticproperty.Option} elements. Use
   * {@link org.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public BU requiredSingleValueSelection(Label label,
                                         Option... options) {
    return requiredSingleValueSelection(label.getInternalId(), label.getLabel(), label.getDescription(), Arrays.asList(options));
  }

  /**
   * @deprecated Use {@link #requiredSingleValueSelection(Label, List)} instead.
   * @param internalId
   * @param label
   * @param description
   * @param options
   * @return
   */
  public BU requiredSingleValueSelection(String internalId, String label, String description,
                                         List<Option> options) {
    OneOfStaticProperty osp = new OneOfStaticProperty(internalId, label, description);
    osp.setOptions(options);

    this.staticProperties.add(osp);
    return me();

  }

  public BU requiredSingleValueSelectionFromRemote(Label label) {
    RuntimeResolvableOneOfStaticProperty rsp = new RuntimeResolvableOneOfStaticProperty(label.getInternalId(), label
            .getLabel(), label.getDescription());

    this.staticProperties.add(rsp);
    return me();
  }

  public BU requiredSingleValueSelectionFromContainer(Label label, String linkedMappingPropertyId) {
    RuntimeResolvableOneOfStaticProperty rsp = new RuntimeResolvableOneOfStaticProperty(label.getInternalId(), label
            .getLabel(), label.getDescription());

    rsp.setLinkedMappingPropertyId(linkedMappingPropertyId);

    this.staticProperties.add(rsp);
    return me();
  }

  /**
   * Defines a configuration parameter that lets pipeline developers select from a list of pre-defined configuration
   * options. The parameter will be rendered as a RadioGroup in the StreamPipes UI.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param options A list of {@link org.streampipes.model.staticproperty.Option} elements. Use
   * {@link org.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public BU requiredSingleValueSelection(Label label,
                                         List<Option> options) {
    OneOfStaticProperty osp = new OneOfStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    osp.setOptions(options);

    this.staticProperties.add(osp);
    return me();

  }

  /**
   * @deprecated Use {@link #requiredMultiValueSelection(Label, Option...)} instead.
   * @param internalId
   * @param label
   * @param description
   * @param options
   * @return
   */
  public BU requiredMultiValueSelection(String internalId, String label, String description,
                                        Option... options) {
    return requiredMultiValueSelection(internalId, label, description, Arrays.asList(options));
  }

  /**
   * Defines a configuration parameter that lets pipeline developers select from a list of pre-defined configuration
   * options, but multiple selections are allowed. The parameter will be rendered as a Checkbox group in the StreamPipes
   * UI.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param options An arbitrary number of {@link org.streampipes.model.staticproperty.Option} elements. Use
   * {@link org.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public BU requiredMultiValueSelection(Label label,
                                        Option... options) {
    return requiredMultiValueSelection(label.getInternalId(), label.getLabel(), label.getDescription(), Arrays.asList(options));
  }

  /**
   * @deprecated Use {@link #requiredMultiValueSelection(Label, List)} instead.
   * @param internalId
   * @param label
   * @param description
   * @param options
   * @return
   */
  public BU requiredMultiValueSelection(String internalId, String label, String description,
                                        List<Option> options) {
    AnyStaticProperty asp = new AnyStaticProperty(internalId, label, description);
    asp.setOptions(options);

    this.staticProperties.add(asp);
    return me();
  }

  /**
   * Defines a configuration parameter that lets pipeline developers select from a list of pre-defined configuration
   * options, but multiple selections are allowed. The parameter will be rendered as a Checkbox group in the StreamPipes
   * UI.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param options A list of {@link org.streampipes.model.staticproperty.Option} elements. Use
   * {@link org.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public BU requiredMultiValueSelection(Label label,
                                        List<Option> options) {
    AnyStaticProperty asp = new AnyStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
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

  /**
   * Defines a number-based configuration parameter of type float provided by pipeline developers at pipeline
   * authoring time. In addition, an allowed value range of the expected input can be assigned.
   * @param label The {@link org.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param min The minimum value of the allowed value range.
   * @param max The maximum value of the allowed value range.
   * @param step The granularity
   * @return this
   */
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

  /**
   * @deprecated Use {@link #naryMappingPropertyWithoutRequirement(Label, PropertyScope)} instead.
   * @param internalName
   * @param label
   * @param description
   * @return
   */
  public BU naryMappingPropertyWithoutRequirement(String internalName, String label, String
          description) {
    this.staticProperties.add(new MappingPropertyNary(internalName, label, description));
    return me();
  }

  /**
   * Adds a new {@link org.streampipes.model.staticproperty.MappingPropertyNary} to the pipeline element definition
   * which is not linked to a specific input property.
   * Use this method if you want to present users a selection (in form of a Checkbox Group) of all available input
   * event properties.
   * @param label A human-readable label that is displayed to users in the StreamPipes UI.
   * @param propertyScope Only input event properties that match the
   * {@link org.streampipes.model.schema.PropertyScope} are displayed.
   * @return
   */
  public BU naryMappingPropertyWithoutRequirement(Label label, PropertyScope propertyScope) {
    MappingPropertyNary mp = new MappingPropertyNary(label.getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.staticProperties.add(mp);
    return me();
  }

  /**
   * Adds a new {@link org.streampipes.model.staticproperty.MappingPropertyUnary} to the pipeline element definition
   * which is not linked to a specific input property.
   * @deprecated Use {@link #unaryMappingPropertyWithoutRequirement(Label)} instead.
   * Use this method if you want to present users a single-value selection of all available input
   * event properties.
   * @param label A human-readable label
   * @return this
   */
  public BU unaryMappingPropertyWithoutRequirement(String internalName, String label, String
          description) {
    this.staticProperties.add(new MappingPropertyUnary(internalName, label, description));
    return me();
  }

  /**
   * Adds a new {@link org.streampipes.model.staticproperty.MappingPropertyUnary} to the pipeline element definition
   * which is not linked to a specific input property.
   * @deprecated
   * Use this method if you want to present users a single-value selection of all available input
   * event properties.
   * @param label
   * @return this
   */
  public BU unaryMappingPropertyWithoutRequirement(Label label) {
    this.staticProperties.add(new MappingPropertyUnary(label.getInternalId(), label.getLabel(), label.getDescription()));
    return me();
  }

  /**
   * Adds a new {@link org.streampipes.model.staticproperty.MappingPropertyUnary} to the pipeline element definition
   * which is not linked to a specific input property.
   * @deprecated
   * Use this method if you want to present users a single-value selection of all available input
   * event properties.
   * @param label A human-readable label that is displayed to users in the StreamPipes UI.
   * @param propertyScope Only input event properties that match the
   * {@link org.streampipes.model.schema.PropertyScope} are displayed.
   * @return this
   */
  public BU unaryMappingPropertyWithoutRequirement(Label label, PropertyScope propertyScope) {
    MappingPropertyUnary mp = new MappingPropertyUnary(label.getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.staticProperties.add(mp);
    return me();
  }

  /**
   * Assigns supported transport formats to the pipeline elements that can be handled at runtime (e.g.,
   * JSON or XMl).
   * @param format An arbitrary number of supported {@link org.streampipes.model.grounding.TransportFormat}s. Use
   *                {@link org.streampipes.sdk.helpers.SupportedFormats} to assign formats from some pre-defined
   *                 ones or create your own by following the developer guide.
   * @return this
   */
  public BU supportedFormats(TransportFormat... format) {
    return supportedFormats(Arrays.asList(format));
  }

  /**
   * Assigns supported transport formats to the pipeline elements that can be handled at runtime (e.g.,
   * JSON or XMl).
   * @param formats A list of supported {@link org.streampipes.model.grounding.TransportFormat}s. Use
   *                {@link org.streampipes.sdk.helpers.SupportedFormats} to assign formats from some pre-defined
   *                 ones or create your own by following the developer guide.
   * @return this
   */
  public BU supportedFormats(List<TransportFormat> formats) {
    this.supportedGrounding.setTransportFormats(formats);
    return me();
  }

  /**
   * Assigns supported communication/transport protocols to the pipeline elements that can be handled at runtime (e.g.,
   * Kafka or JMS).
   * @param protocol An arbitrary number of supported {@link org.streampipes.model.grounding.TransportProtocol}s. Use
   *                {@link org.streampipes.sdk.helpers.SupportedProtocols} to assign protocols from some pre-defined
   *                 ones or create your own by following the developer guide.
   * @return this
   */
  public BU supportedProtocols(TransportProtocol... protocol) {
    return supportedProtocols(Arrays.asList(protocol));
  }

  /**
   * Assigns supported communication/transport protocols to the pipeline elements that can be handled at runtime (e.g.,
   * Kafka or JMS).
   * @param protocols A list of supported {@link org.streampipes.model.grounding.TransportProtocol}s. Use
   *                {@link org.streampipes.sdk.helpers.SupportedProtocols} to assign protocols from some pre-defined
   *                 ones or create your own by following the developer guide.
   * @return this
   */
  public BU supportedProtocols(List<TransportProtocol> protocols) {
    this.supportedGrounding.setTransportProtocols(protocols);
    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  public BU setStream1() {
    stream1 = true;
    return me();
  }

  /**
   *
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
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

  private FreeTextStaticProperty prepareFreeTextStaticProperty(Label label, String type) {
    return prepareFreeTextStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription(), type);
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
