/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.staticproperty.AnyStaticProperty;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty;
import org.apache.streampipes.model.staticproperty.DomainStaticProperty;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.PropertyValueSpecification;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.SupportedProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.XSD;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractConfigurablePipelineElementBuilder<K extends
    AbstractConfigurablePipelineElementBuilder<K, V>, V extends NamedStreamPipesEntity> extends
    AbstractPipelineElementBuilder<K, V> {

  protected List<StaticProperty> staticProperties;

  protected AbstractConfigurablePipelineElementBuilder(String appId, String label, String description, V element) {
    super(appId, label, description, element);
    this.staticProperties = new ArrayList<>();
  }

  protected AbstractConfigurablePipelineElementBuilder(String appId, V element) {
    super(appId, element);
    this.staticProperties = new ArrayList<>();
  }

  /**
   *
   * @param staticProperty The required static property (e.g., user input as shown in the StreamPipes UI
   * @return BU
   */
  public K requiredStaticProperty(StaticProperty staticProperty) {
    this.staticProperties.add(staticProperty);
    return me();
  }

  /**
   * Defines the requirement for an instance that is defined in the knowledge base.
   *
   * @param label                     A human-readable label that describes the required static property.
   * @param supportedOntologyProperties All RDF properties any instance in the knowledge base must provide. Use
   *                                     {@link org.apache.streampipes.sdk.helpers.OntologyProperties}
   *                                      to assign supported properties.
   *
   */
  public K requiredOntologyConcept(Label label, SupportedProperty...
      supportedOntologyProperties) {
    DomainStaticProperty dsp = prepareStaticProperty(label, new DomainStaticProperty());
    dsp.setSupportedProperties(Arrays.asList(supportedOntologyProperties));
    this.staticProperties.add(dsp);

    return me();
  }

  /**
   * @param label                        A human-readable label that describes the required static property.
   * @param requiredConceptUri           Limits the search for matching instance
   *                                     in the knowledge base to an instance of this concept.
   * @param supportedOntologyProperties  All RDF properties any instance of the provided concept must provide. Use
   *                                     {@link org.apache.streampipes.sdk.helpers.OntologyProperties}
   *                                     to assign supported properties.
   *
   */
  public K requiredOntologyConcept(Label label, String requiredConceptUri, SupportedProperty...
      supportedOntologyProperties) {
    DomainStaticProperty dsp = prepareStaticProperty(label, new DomainStaticProperty());
    dsp.setSupportedProperties(Arrays.asList(supportedOntologyProperties));
    dsp.setRequiredClass(requiredConceptUri);
    this.staticProperties.add(dsp);

    return me();
  }

  /**
   * @param label         A human-readable label that describes the required static property.
   * @param staticProperty
   * @return
   */
  public K requiredParameterAsCollection(Label label, StaticProperty staticProperty) {
    CollectionStaticProperty collection = prepareStaticProperty(label, new
        CollectionStaticProperty());
    collection.setStaticPropertyTemplate(staticProperty);
    this.staticProperties.add(collection);

    return me();
  }

  /**
   * @deprecated use {@link #requiredTextParameter(Label)}
   * @param internalId
   * @param label
   * @param description
   * @return
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredTextParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.STRING.toString()));

    return me();
  }

  /**
   * Assigns a new secret text-based configuration parameter (e.g., a password) which is required
   * by the processing element.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public K requiredSecret(Label label) {
    SecretStaticProperty secretStaticProperty = new SecretStaticProperty(label.getInternalId(),
        label.getLabel(), label.getDescription());
    this.staticProperties.add(secretStaticProperty);

    return me();
  }

  /**
   * Assigns a new code block parameter which is required
   * by the processing element.
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in an user-friendly manner.
   * @param codeLanguage The {@link org.apache.streampipes.sdk.helpers.CodeLanguage}
   *                     code language the code block is built for.
   * @return this
   */
  public K requiredCodeblock(Label label, CodeLanguage codeLanguage) {
    this.requiredCodeblock(label, codeLanguage, codeLanguage.getDefaultSkeleton());

    return me();
  }

  /**
   * Assigns a new code block parameter which is required
   * by the processing element.
   *
   * @param label           The {@link org.apache.streampipes.sdk.helpers.Label}
   *                        that describes why this parameter is needed in an user-friendly manner.
   * @param codeLanguage    The {@link org.apache.streampipes.sdk.helpers.CodeLanguage}
   *                        code language the code block is built for.
   * @param defaultSkeleton The code skeleton that is used as a default value.
   * @return this
   */
  public K requiredCodeblock(Label label, CodeLanguage codeLanguage, String defaultSkeleton) {
    CodeInputStaticProperty codeInputStaticProperty = new CodeInputStaticProperty(label.getInternalId(),
        label.getLabel(), label.getDescription());
    codeInputStaticProperty.setLanguage(codeLanguage.name());
    codeInputStaticProperty.setCodeTemplate(defaultSkeleton);
    this.staticProperties.add(codeInputStaticProperty);

    return me();
  }

  /**
   * Assigns a new required slide toggle for a true/false selection
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in an user-friendly manner.
   * @param defaultValue The toggle's default value
   * @return this
   */
  public K requiredSlideToggle(Label label, boolean defaultValue) {
    SlideToggleStaticProperty slideToggle = new SlideToggleStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription(),
        defaultValue);

    slideToggle.setSelected(defaultValue);

    this.staticProperties.add(slideToggle);

    return me();
  }

  /**
   * Assigns a new code block parameter (without a specific language) which is required
   * by the processing element.
   *
   * @param label           The {@link org.apache.streampipes.sdk.helpers.Label}
   *                        that describes why this parameter is needed in a user-friendly manner.
   * @param defaultSkeleton The code skeleton that is used as a default value.
   * @return this
   */
  public K requiredCodeblock(Label label, String defaultSkeleton) {
    return requiredCodeblock(label, CodeLanguage.None, defaultSkeleton);
  }

  /**
   * Assigns a new code block parameter (without a specific language) which is required
   * by the processing element.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return this
   */
  public K requiredCodeblock(Label label) {
    return requiredCodeblock(label, CodeLanguage.None);
  }

  /**
   * Assigns a new text-based configuration parameter (a string) which is required by the pipeline
   * element.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public K requiredTextParameter(Label label) {
    this.staticProperties.add(prepareFreeTextStaticProperty(label, XSD.STRING.toString()));

    return me();
  }

  /**
   * Assigns a new color picker parameter which is required by the pipeline
   * element.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public K requiredColorParameter(Label label) {
    ColorPickerStaticProperty csp =
        new ColorPickerStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    this.staticProperties.add(csp);

    return me();
  }

  /**
   * Assigns a new color picker parameter which is required by the pipeline
   * element.
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in a user-friendly manner.
   * @param defaultColor The default color, encoded as an HTML color code
   * @return
   */
  public K requiredColorParameter(Label label, String defaultColor) {
    ColorPickerStaticProperty csp =
        new ColorPickerStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    csp.setSelectedColor(defaultColor);
    this.staticProperties.add(csp);

    return me();
  }

  /**
   * @deprecated Use {@link #requiredTextParameterWithLink(Label, String)}
   *
   * @param internalId
   * @param label
   * @param description
   * @param linkedMappingPropertyInternalName
   * @return this
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredTextParameter(String internalId, String label, String description, String
      linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.STRING.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in an user-friendly manner.
   * @param defaultValue The default value is displayed to the user in the input field
   * @return this
   */
  public K requiredTextParameter(Label label,
                                 String defaultValue) {
    FreeTextStaticProperty fsp = StaticProperties.stringFreeTextProperty(label, defaultValue);

    this.staticProperties.add(fsp);
    return me();
  }


  /**
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   *
   * @param label                             The {@link org.apache.streampipes.sdk.helpers.Label}
   *                                          that describes why this parameter is needed in a
   *                                          user-friendly manner.
   * @param linkedMappingPropertyInternalName The inernalId of the
   *                                            {@link org.apache.streampipes.model.staticproperty.MappingProperty}
   * @return this
   */
  public K requiredTextParameterWithLink(Label label, String
      linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.STRING.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time. The
   * input field generated in the StreamPipes UI allows to enter HTML content (and an HTML Wysiwyg editor will be
   * rendered).
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return this
   */
  public K requiredHtmlInputParameter(Label label) {
    FreeTextStaticProperty fsp =
        new FreeTextStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    fsp.setMultiLine(true);
    fsp.setHtmlAllowed(true);
    fsp.setPlaceholdersSupported(true);
    this.staticProperties.add(fsp);

    return me();
  }

  /**
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time.
   *
   * @param label                 The {@link org.apache.streampipes.sdk.helpers.Label}
   *                              that describes why this parameter is needed in a user-friendly manner.
   * @param multiLine             Defines whether the input dialog allows multiple lines.
   * @param placeholdersSupported Defines whether placeholders are supported, i.e., event property field names that
   *                              are replaced with the actual value at pipeline execution time.
   * @return this
   */
  public K requiredTextParameter(Label label, boolean multiLine, boolean placeholdersSupported) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.STRING.toString());
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
   * Defines a text-based configuration parameter provided by pipeline developers at pipeline authoring time.
   *
   * @param label                 The {@link org.apache.streampipes.sdk.helpers.Label}
   *                              that describes why this parameter is needed in an user-friendly manner.
   * @param multiLine             Defines whether the input dialog allows multiple lines.
   * @param placeholdersSupported Defines whether placeholders are supported, i.e., event property field names that
   *                              are replaced with the actual value at pipeline execution time.
   * @param htmlFontFormat        Defines to only use bold, italic, striked in dialog.
   * @return this
   */
  public K requiredTextParameter(Label label, boolean multiLine, boolean placeholdersSupported,
                                 boolean htmlFontFormat) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.STRING.toString());
    if (multiLine) {
      fsp.setMultiLine(true);
    }
    if (placeholdersSupported) {
      fsp.setPlaceholdersSupported(true);
    }
    if (htmlFontFormat) {
      fsp.setHtmlFontFormat(true);
    }
    this.staticProperties.add(fsp);

    return me();
  }


  /**
   * @deprecated Use {@link #requiredIntegerParameter(Label)} instead
   *
   * @param internalId
   * @param label
   * @param description
   * @return
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredIntegerParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.INTEGER.toString()));

    return me();
  }

  /**
   * Assigns a new number-based configuration parameter (an integer) which is required by the pipeline
   * element.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public K requiredIntegerParameter(Label label) {
    this.staticProperties.add(prepareFreeTextStaticProperty(label, XSD.INTEGER.toString()));

    return me();
  }

  /**
   * @deprecated use {@link #requiredIntegerParameter(Label, String)} instead
   *
   * @param internalId
   * @param label
   * @param description
   * @param linkedMappingPropertyInternalName
   * @return
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredIntegerParameter(String internalId, String label, String description, String
      linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.INTEGER.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a number-based configuration parameter of type integer provided by pipeline developers at pipeline
   * authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   *
   * @param label                             The {@link org.apache.streampipes.sdk.helpers.Label}
   *                                          that describes why this parameter is needed in a user-friendly manner.
   * @param linkedMappingPropertyInternalName The inernalId of the
   *                                            {@link org.apache.streampipes.model.staticproperty.MappingProperty}
   * @return this
   */
  public K requiredIntegerParameter(Label label, String
      linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.INTEGER.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }


  /**
   * @deprecated Use {@link #requiredIntegerParameter(Label, Integer)} instead
   *
   * @param internalId
   * @param label
   * @param description
   * @param defaultValue
   * @return
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredIntegerParameter(String internalId, String label, String description,
                                    Integer defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.INTEGER.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a number-based configuration parameter of type integer provided by pipeline developers at pipeline
   * authoring time and initializes the parameter with a default value.
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in a user-friendly manner.
   * @param defaultValue The default integer value.
   * @return this
   */
  public K requiredIntegerParameter(Label label,
                                    Integer defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label,
        XSD.INTEGER.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a number-based configuration parameter of type long provided by pipeline developers at pipeline
   * authoring time and initializes the parameter with a default value.
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in a user-friendly manner.
   * @param defaultValue The default long value.
   * @return this
   */
  public K requiredLongParameter(Label label,
                                 Long defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label,
        XSD.LONG.toString());
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
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredFloatParameter(String internalId, String label, String description) {
    this.staticProperties.add(prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.DOUBLE.toString()));

    return me();
  }

  /**
   * Assigns a new number-based configuration parameter (a float) which is required by the pipeline
   * element.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return
   */
  public K requiredFloatParameter(Label label) {
    this.staticProperties.add(prepareFreeTextStaticProperty(label,
        XSD.DOUBLE.toString()));

    return me();
  }

  /**
   * @deprecated use {@link #requiredFloatParameter(Label, String)}
   *
   * Defines a number-based configuration parameter of type float provided by pipeline developers at pipeline
   * authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   *
   * @param label                             The {@link org.apache.streampipes.sdk.helpers.Label}
   *                                          that describes why this parameter is needed in a user-friendly manner.
   * @param linkedMappingPropertyInternalName The inernalId of the
   *                                            {@link org.apache.streampipes.model.staticproperty.MappingProperty}
   * @return this
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredFloatParameter(String internalId, String label, String description, String
      linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.DOUBLE.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a number-based configuration parameter of type float provided by pipeline developers at pipeline
   * authoring time. The
   * value range of the parameter is restricted to the value specification of a selected input event property.
   *
   * @param label                             The {@link org.apache.streampipes.sdk.helpers.Label}
   *                                          that describes why this parameter is needed in a user-friendly manner.
   * @param linkedMappingPropertyInternalName The internalId of the
   *                                            {@link org.apache.streampipes.model.staticproperty.MappingProperty}
   * @return this
   */
  public K requiredFloatParameter(Label label, String
      linkedMappingPropertyInternalName) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.DOUBLE.toString());

    fsp.setMapsTo(linkedMappingPropertyInternalName);
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * @deprecated Use {@link #requiredFloatParameter(Label, Float)} instead.
   *
   * @param internalId
   * @param label
   * @param description
   * @param defaultValue
   * @return this
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredFloatParameter(String internalId, String label, String description, Float
      defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.DOUBLE.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * Defines a number-based configuration parameter of type float provided by preprocessing developers at preprocessing
   * authoring time and initializes the parameter with a default value.
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in a user-friendly manner.
   * @param defaultValue The default integer value.
   * @return this
   */
  public K requiredFloatParameter(Label label, Float
      defaultValue) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.DOUBLE.toString());
    fsp.setValue(String.valueOf(defaultValue));
    this.staticProperties.add(fsp);
    return me();
  }

  /**
   * @deprecated Use {@link #requiredFloatParameter(Label, Float, Float, Float)} instead.
   * Defines a number-based configuration parameter of type float provided by preprocessing developers at preprocessing
   * authoring time. In addition, an allowed value range of the expected input can be assigned.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param min   The minimum value of the allowed value range.
   * @param max   The maximum value of the allowed value range.
   * @param step  The granularity
   * @return this
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredFloatParameter(String internalId, String label, String description, Float min, Float max,
                                  Float step) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(internalId,
        label,
        description,
        XSD.DOUBLE.toString());

    PropertyValueSpecification valueSpecification = new PropertyValueSpecification(min, max, step);
    fsp.setValueSpecification(valueSpecification);
    this.staticProperties.add(fsp);

    return me();
  }

  /**
   * Defines a number-based configuration parameter of type float provided by preprocessing developers at preprocessing
   * authoring time. In addition, an allowed value range of the expected input can be assigned.
   *
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @param min   The minimum value of the allowed value range.
   * @param max   The maximum value of the allowed value range.
   * @param step  The granularity
   * @return this
   */
  public K requiredFloatParameter(Label label, Float min, Float max, Float step) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.DOUBLE.toString());
    PropertyValueSpecification valueSpecification = new PropertyValueSpecification(min, max, step);
    fsp.setValueSpecification(valueSpecification);
    this.staticProperties.add(fsp);

    return me();
  }

  /**
   * Defines a number-based configuration parameter of type float provided by preprocessing developers at preprocessing
   * authoring time. In addition, an allowed value range of the expected input can be assigned.
   *
   * @param label        The {@link org.apache.streampipes.sdk.helpers.Label}
   *                     that describes why this parameter is needed in a user-friendly manner.
   * @param defaultValue The default float value.
   * @param min          The minimum value of the allowed value range.
   * @param max          The maximum value of the allowed value range.
   * @param step         The granularity
   * @return this
   */
  public K requiredFloatParameter(Label label, Float defaultValue, Float min, Float max, Float step) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label, XSD.DOUBLE.toString());
    fsp.setValue(String.valueOf(defaultValue));
    PropertyValueSpecification valueSpecification = new PropertyValueSpecification(min, max, step);
    fsp.setValueSpecification(valueSpecification);
    this.staticProperties.add(fsp);

    return me();
  }

  /**
   * @deprecated Use {@link #requiredSingleValueSelection(Label, Option...)} instead.
   *
   * @param options An arbitrary number of {@link org.apache.streampipes.model.staticproperty.Option} elements. Use
   *                {@link org.apache.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredSingleValueSelection(String internalId, String label, String description,
                                        Option... options) {
    return requiredSingleValueSelection(internalId, label, description, Arrays.asList(options));
  }


  /**
   * Defines a configuration parameter that lets preprocessing developers
   * select from a list of pre-defined configuration options.
   * The parameter will be rendered as a RadioGroup in the StreamPipes UI.
   *
   * @param label   The {@link org.apache.streampipes.sdk.helpers.Label} that describes
   *                why this parameter is needed in a user-friendly manner.
   * @param options An arbitrary number of {@link org.apache.streampipes.model.staticproperty.Option} elements. Use
   *                {@link org.apache.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public K requiredSingleValueSelection(Label label,
                                        Option... options) {
    return requiredSingleValueSelection(label.getInternalId(), label.getLabel(), label.getDescription(),
        Arrays.asList(options));
  }

  /**
   * @deprecated Use {@link #requiredSingleValueSelection(Label, List)} instead.
   *
   * @param internalId
   * @param label
   * @param description
   * @param options
   * @return
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredSingleValueSelection(String internalId, String label, String description,
                                        List<Option> options) {
    OneOfStaticProperty osp = new OneOfStaticProperty(internalId, label, description);
    osp.setOptions(options);

    this.staticProperties.add(osp);
    return me();

  }

  /**
   * Defines a configuration parameter that lets preprocessing developers
   * select from a list of pre-defined configuration options.
   * The parameter will be rendered as a RadioGroup in the StreamPipes UI.
   *
   * @param label   The {@link org.apache.streampipes.sdk.helpers.Label} that describes
   *                why this parameter is needed in a user-friendly manner.
   * @param options A list of {@link org.apache.streampipes.model.staticproperty.Option} elements. Use
   *                {@link org.apache.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public K requiredSingleValueSelection(Label label,
                                        List<Option> options) {
    OneOfStaticProperty osp = new OneOfStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    osp.setOptions(options);

    this.staticProperties.add(osp);
    return me();
  }

  /**
   * Defines a configuration parameter that lets preprocessing developers
   * select from a list of pre-defined configuration options.
   * The parameter will be rendered as a RadioGroup in the StreamPipes UI.
   *
   * @param label               The {@link org.apache.streampipes.sdk.helpers.Label}
   *                            that describes why this parameter is needed in a user-friendly manner.
   * @param options             A list of {@link org.apache.streampipes.model.staticproperty.Option} elements. Use
   * @param horizontalRendering when set to true
   *                            {@link org.apache.streampipes.sdk.helpers.Options}
   *                            to create option elements from string values.
   * @return this
   */
  public K requiredSingleValueSelection(Label label,
                                        List<Option> options,
                                        boolean horizontalRendering) {
    OneOfStaticProperty osp =
        new OneOfStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription(), horizontalRendering);
    osp.setOptions(options);

    this.staticProperties.add(osp);
    return me();
  }


  /**
   * @deprecated Use {@link #requiredMultiValueSelection(Label, Option...)} instead.
   *
   * @param internalId
   * @param label
   * @param description
   * @param options
   * @return
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredMultiValueSelection(String internalId, String label, String description,
                                       Option... options) {
    return requiredMultiValueSelection(internalId, label, description, Arrays.asList(options));
  }

  /**
   * Defines a configuration parameter that lets preprocessing developers
   * select from a list of pre-defined configuration options, but multiple selections are allowed.
   * The parameter will be rendered as a Checkbox group in the StreamPipes UI.
   *
   * @param label   The {@link org.apache.streampipes.sdk.helpers.Label}
   *                that describes why this parameter is needed in a user-friendly manner.
   * @param options An arbitrary number of {@link org.apache.streampipes.model.staticproperty.Option} elements. Use
   *                {@link org.apache.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public K requiredMultiValueSelection(Label label,
                                       Option... options) {
    return requiredMultiValueSelection(label.getInternalId(), label.getLabel(), label.getDescription(),
        Arrays.asList(options));
  }

  /**
   * @deprecated Use {@link #requiredMultiValueSelection(Label, List)} instead.
   *
   * @param internalId
   * @param label
   * @param description
   * @param options
   * @return
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K requiredMultiValueSelection(String internalId, String label, String description,
                                       List<Option> options) {
    AnyStaticProperty asp = new AnyStaticProperty(internalId, label, description);
    asp.setOptions(options);

    this.staticProperties.add(asp);
    return me();
  }

  /**
   * Defines a configuration parameter that lets preprocessing developers
   * select from a list of pre-defined configuration options, but multiple selections are allowed.
   * The parameter will be rendered as a Checkbox group in the StreamPipes UI.
   *
   * @param label   The {@link org.apache.streampipes.sdk.helpers.Label}
   *                that describes why this parameter is needed in a user-friendly manner.
   * @param options A list of {@link org.apache.streampipes.model.staticproperty.Option} elements. Use
   *                {@link org.apache.streampipes.sdk.helpers.Options} to create option elements from string values.
   * @return this
   */
  public K requiredMultiValueSelection(Label label,
                                       List<Option> options) {
    AnyStaticProperty asp = new AnyStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription());
    asp.setOptions(options);

    this.staticProperties.add(asp);
    return me();
  }

  public K requiredIntegerParameter(Label label, Integer min, Integer max, Integer step) {
    FreeTextStaticProperty fsp = prepareFreeTextStaticProperty(label.getInternalId(),
        label.getLabel(),
        label.getDescription(),
        XSD.INTEGER.toString());

    PropertyValueSpecification valueSpecification = new PropertyValueSpecification(min, max, step);
    fsp.setValueSpecification(valueSpecification);
    this.staticProperties.add(fsp);
    return me();
  }

  public K requiredIntegerParameter(String internalId, String label, String description, Integer min, Integer max,
                                    Integer step) {
    return requiredIntegerParameter(Labels.from(internalId, label, description), min, max, step);
  }

  /**
   * @param label The {@link org.apache.streampipes.sdk.helpers.Label} that describes why this parameter is needed in a
   *              user-friendly manner.
   * @return this
   */
  public K requiredFile(Label label) {
    FileStaticProperty fp = new FileStaticProperty(label.getInternalId(), label.getLabel(), label
        .getDescription());

    this.staticProperties.add(fp);

    return me();

  }

  /**
   * @param label             The {@link org.apache.streampipes.sdk.helpers.Label}
   *                          that describes why this parameter is needed in a user-friendly manner.
   * @param requiredFiletypes A list of {@link org.apache.streampipes.sdk.helpers.Filetypes}
   *                          required filetypes the element supports.
   * @return this
   */
  public K requiredFile(Label label, Filetypes... requiredFiletypes) {
    List<String> collectedFiletypes = new ArrayList<>();
    Arrays.stream(requiredFiletypes).forEach(rf -> collectedFiletypes.addAll(rf.getFileExtensions()));

    return requiredFile(label, collectedFiletypes.toArray(new String[0]));
  }

  /**
   * @param label             The {@link org.apache.streampipes.sdk.helpers.Label}
   *                          that describes why this parameter is needed in a user-friendly manner.
   * @param requiredFiletypes A list of required filetypes (a string marking the file extension) the element supports.
   * @return this
   */
  public K requiredFile(Label label, String... requiredFiletypes) {
    FileStaticProperty fp = new FileStaticProperty(label.getInternalId(), label.getLabel(), label
        .getDescription());

    List<String> collectedFiletypes = Arrays.asList(requiredFiletypes);
    fp.setRequiredFiletypes(collectedFiletypes);
    this.staticProperties.add(fp);

    return me();

  }

  public K requiredAlternatives(Label label, StaticPropertyAlternative... alternatives) {
    StaticPropertyAlternatives alternativesContainer =
        new StaticPropertyAlternatives(label.getInternalId(), label.getLabel(), label.getDescription());

    for (int i = 0; i < alternatives.length; i++) {
      alternatives[i].setIndex(i);
    }

    alternativesContainer.setAlternatives(Arrays.asList(alternatives));
    this.staticProperties.add(alternativesContainer);

    return me();
  }

  public K requiredSingleValueSelectionFromContainer(Label label) {
    this.staticProperties.add(StaticProperties.singleValueSelectionFromContainer(label));
    return me();
  }

  public K requiredSingleValueSelectionFromContainer(Label label,
                                                     List<String> dependsOn) {
    this.staticProperties.add(StaticProperties.singleValueSelectionFromContainer(label, dependsOn));
    return me();
  }

  public K requiredMultiValueSelectionFromContainer(Label label) {
    this.staticProperties.add(StaticProperties.multiValueSelectionFromContainer(label));
    return me();
  }

  public K requiredMultiValueSelectionFromContainer(Label label,
                                                    List<String> dependsOn) {
    this.staticProperties.add(StaticProperties.multiValueSelectionFromContainer(label, dependsOn));
    return me();
  }

  public K requiredRuntimeResolvableTreeInput(Label label,
                                              List<String> dependsOn,
                                              boolean multiSelection) {
    return requiredRuntimeResolvableTreeInput(label, dependsOn, false, multiSelection);
  }

  public K requiredRuntimeResolvableTreeInput(Label label,
                                              List<String> dependsOn,
                                              boolean resolveDynamically,
                                              boolean multiSelection) {
    this.staticProperties.add(
        StaticProperties.runtimeResolvableTreeInput(label, dependsOn, resolveDynamically, multiSelection)
    );
    return me();
  }

  /**
   * Defines a collection of configuration parameters of the specified staticProperties.
   * The developer can fill the staticProperties multiply times.
   *
   * @param label            The {@link org.apache.streampipes.sdk.helpers.Label}
   *                         that describes why this parameter is needed in a user-friendly manner.
   * @param staticProperties A list of {@link org.apache.streampipes.model.staticproperty} elements.
   * @return this
   */
  public K requiredCollection(Label label, StaticProperty... staticProperties) {
    this.staticProperties.add(StaticProperties.collection(label, staticProperties));
    return me();
  }

  private FreeTextStaticProperty prepareFreeTextStaticProperty(String internalId, String label, String description,
                                                               String type) {
    return new FreeTextStaticProperty(internalId,
        label,
        description,
        URI.create(type));
  }

  private FreeTextStaticProperty prepareFreeTextStaticProperty(Label label, String type) {
    return prepareFreeTextStaticProperty(label.getInternalId(), label.getLabel(), label.getDescription(), type);
  }

  private List<StaticProperty> sortStaticProperties(List<StaticProperty> staticProperties) {
    for (int i = 0; i < staticProperties.size(); i++) {
      staticProperties.get(i).setIndex(i);
    }
    return staticProperties;
  }

  protected List<StaticProperty> getStaticProperties() {
    return sortStaticProperties(this.staticProperties);
  }


}
