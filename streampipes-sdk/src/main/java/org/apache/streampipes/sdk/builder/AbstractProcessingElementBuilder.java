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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.MappingProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.sdk.helpers.CollectedStreamRequirements;
import org.apache.streampipes.sdk.helpers.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractProcessingElementBuilder<K extends
    AbstractProcessingElementBuilder<K, T>, T extends ConsumableStreamPipesEntity> extends
    AbstractConfigurablePipelineElementBuilder<K, T> {

  protected List<SpDataStream> streamRequirements;

  protected List<EventProperty> stream1Properties;
  protected List<EventProperty> stream2Properties;

  protected EventGrounding supportedGrounding;

  protected boolean stream1 = false;
  protected boolean stream2 = false;


  protected AbstractProcessingElementBuilder(String id, String label, String description, T element) {
    super(id, label, description, element);
    this.streamRequirements = new ArrayList<>();
    this.stream1Properties = new ArrayList<>();
    this.stream2Properties = new ArrayList<>();
    this.supportedGrounding = new EventGrounding();
  }

  protected AbstractProcessingElementBuilder(String id, T element) {
    super(id, element);
    this.streamRequirements = new ArrayList<>();
    this.stream1Properties = new ArrayList<>();
    this.stream2Properties = new ArrayList<>();
    this.supportedGrounding = new EventGrounding();
  }

  /**
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated
  public K requiredStream(SpDataStream stream) {
    this.streamRequirements.add(stream);
    return me();
  }

  /**
   * Set a new stream requirement by adding restrictions on this stream. Use
   * {@link StreamRequirementsBuilder} to create requirements for a single stream.
   *
   * @param streamRequirements A bundle of collected {@link CollectedStreamRequirements}
   * @return this
   */
  public K requiredStream(CollectedStreamRequirements streamRequirements) {

    this.streamRequirements.add(streamRequirements.getStreamRequirements());
    this.staticProperties.addAll(rewrite(streamRequirements.getMappingProperties(), this
        .streamRequirements.size()));

    return me();
  }

  private List<MappingProperty> rewrite(List<MappingProperty> mappingProperties, int index) {
    mappingProperties.forEach(mp -> mp.setRequirementSelector
        (getIndex(index) + PropertySelectorConstants.PROPERTY_DELIMITER + mp
            .getRequirementSelector()));
    return mappingProperties;
  }

  private String getIndex(int index) {
    return index == 1 ? PropertySelectorConstants.FIRST_REQUIREMENT_PREFIX :
        PropertySelectorConstants.SECOND_REQUIREMENT_PREFIX;
  }

  /**
   * @deprecated Use {@link #naryMappingPropertyWithoutRequirement(Label, PropertyScope)} instead.
   * @param internalName
   * @param label
   * @param description
   * @return
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K naryMappingPropertyWithoutRequirement(String internalName, String label, String
      description) {
    this.staticProperties.add(new MappingPropertyNary(internalName, label, description));
    return me();
  }

  /**
   * Adds a new {@link org.apache.streampipes.model.staticproperty.MappingPropertyNary}
   * to the pipeline element definition which is not linked to a specific input property.
   * Use this method if you want to present users a selection (in form of a Checkbox Group)
   * of all available input event properties.
   *
   * @param label         A human-readable label that is displayed to users in the StreamPipes UI.
   * @param propertyScope Only input event properties that match the
   *                      {@link org.apache.streampipes.model.schema.PropertyScope} are displayed.
   * @return
   */
  public K naryMappingPropertyWithoutRequirement(Label label, PropertyScope propertyScope) {
    MappingPropertyNary mp = new MappingPropertyNary(label.getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.staticProperties.add(mp);
    return me();
  }

  /**
   * @deprecated Use {@link #unaryMappingPropertyWithoutRequirement(Label)} instead.
   * Use this method if you want to present users a single-value selection of all available input
   * event properties.
   *
   * Adds a new {@link org.apache.streampipes.model.staticproperty.MappingPropertyUnary}
   * to the pipeline element definition which is not linked to a specific input property.
   *
   * @param label A human-readable label
   * @return this
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K unaryMappingPropertyWithoutRequirement(String internalName, String label, String
      description) {
    this.staticProperties.add(new MappingPropertyUnary(internalName, label, description));
    return me();
  }

  /**
   * @deprecated Use this method if you want to present users a single-value selection of all available input
   * event properties.
   *
   * Adds a new {@link org.apache.streampipes.model.staticproperty.MappingPropertyUnary}
   * to the pipeline element definition which is not linked to a specific input property.
   *
   * @param label
   * @return this
   *
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K unaryMappingPropertyWithoutRequirement(Label label) {
    this.staticProperties.add(
        new MappingPropertyUnary(label.getInternalId(), label.getLabel(), label.getDescription()));
    return me();
  }

  /**
   * Adds a new {@link org.apache.streampipes.model.staticproperty.MappingPropertyUnary}
   * to the pipeline element definition which is not linked to a specific input property.
   *
   * @param label         A human-readable label that is displayed to users in the StreamPipes UI.
   * @param propertyScope Only input event properties that match the
   *                      {@link org.apache.streampipes.model.schema.PropertyScope} are displayed.
   * @return this
   * Use this method if you want to present users a single-value selection of all available input
   * event properties.
   */
  public K unaryMappingPropertyWithoutRequirement(Label label, PropertyScope propertyScope) {
    MappingPropertyUnary mp = new MappingPropertyUnary(label.getInternalId(), label.getLabel(), label.getDescription());
    mp.setPropertyScope(propertyScope.name());
    this.staticProperties.add(mp);
    return me();
  }

  /**
   * Assigns supported transport formats to the pipeline elements that can be handled at runtime (e.g.,
   * JSON or XMl).
   *
   * @param format An arbitrary number of supported {@link org.apache.streampipes.model.grounding.TransportFormat}s. Use
   *               {@link org.apache.streampipes.sdk.helpers.SupportedFormats} to assign formats from some pre-defined
   *               ones or create your own by following the developer guide.
   * @return this
   */
  public K supportedFormats(TransportFormat... format) {
    return supportedFormats(Arrays.asList(format));
  }

  /**
   * Assigns supported transport formats to the pipeline elements that can be handled at runtime (e.g.,
   * JSON or XMl).
   *
   * @param formats A list of supported {@link org.apache.streampipes.model.grounding.TransportFormat}s. Use
   *                {@link org.apache.streampipes.sdk.helpers.SupportedFormats} to assign formats from some pre-defined
   *                ones or create your own by following the developer guide.
   * @return this
   */
  public K supportedFormats(List<TransportFormat> formats) {
    this.supportedGrounding.setTransportFormats(formats);
    return me();
  }

  /**
   * Assigns supported communication/transport protocols to the pipeline elements that can be handled at runtime (e.g.,
   * Kafka or JMS).
   *
   * @param protocol An arbitrary number of supported
   *                 {@link org.apache.streampipes.model.grounding.TransportProtocol}s.
   *                 Use {@link org.apache.streampipes.sdk.helpers.SupportedProtocols} to assign protocols
   *                 from some pre-defined ones or create your own by following the developer guide.
   * @return this
   */
  public K supportedProtocols(TransportProtocol... protocol) {
    return supportedProtocols(Arrays.asList(protocol));
  }

  /**
   * Assigns supported communication/transport protocols to the pipeline elements that can be handled at runtime (e.g.,
   * Kafka or JMS).
   *
   * @param protocols A list of supported {@link org.apache.streampipes.model.grounding.TransportProtocol}s.
   *                  Use {@link org.apache.streampipes.sdk.helpers.SupportedProtocols} to assign protocols
   *                  from some pre-defined ones or create your own by following the developer guide.
   * @return this
   */
  public K supportedProtocols(List<TransportProtocol> protocols) {
    this.supportedGrounding.setTransportProtocols(protocols);
    return me();
  }

  /**
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K setStream1() {
    stream1 = true;
    return me();
  }

  /**
   * @deprecated Use {@link #requiredStream(CollectedStreamRequirements)} instead
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public K setStream2() {
    stream2 = true;
    return me();
  }


  @Override
  public void prepareBuild() {
    this.elementDescription.setStaticProperties(getStaticProperties());

    if (stream1Properties.size() > 0 || stream1) {
      this.streamRequirements.add(buildStream(stream1Properties));
    }

    if (stream2Properties.size() > 0 || stream2) {
      this.streamRequirements.add(buildStream(stream2Properties));
    }

    this.elementDescription.setSupportedGrounding(supportedGrounding);

    for (int i = 0; i < streamRequirements.size(); i++) {
      streamRequirements.get(i).setIndex(i);
    }

    this.elementDescription.setSpDataStreams(streamRequirements);

  }

  private SpDataStream buildStream(List<EventProperty> streamProperties) {
    SpDataStream stream = new SpDataStream();
    stream.setEventSchema(new EventSchema(streamProperties));
    return stream;
  }

}
