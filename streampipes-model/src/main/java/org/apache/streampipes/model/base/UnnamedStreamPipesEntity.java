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

package org.apache.streampipes.model.base;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.RdfId;
import io.fogsy.empire.annotations.RdfProperty;
import org.apache.streampipes.model.shared.annotation.TsIgnore;
import org.apache.streampipes.model.connect.guess.DomainPropertyProbability;
import org.apache.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.dashboard.DashboardEntity;
import org.apache.streampipes.model.dashboard.DashboardWidgetSettings;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.output.TransformOperation;
import org.apache.streampipes.model.quality.*;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.ValueSpecification;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.PropertyValueSpecification;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.SupportedProperty;
import org.apache.streampipes.model.template.BoundPipelineElement;
import org.apache.streampipes.model.template.PipelineTemplateDescriptionContainer;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.model.util.RdfIdGenerator;
import org.apache.streampipes.vocabulary.StreamPipes;

/**
 * unnamed SEPA elements (that do not require any readable identifier)
 */
@JsonSubTypes({
        @JsonSubTypes.Type(StreamPipesJsonLdContainer.class),
        @JsonSubTypes.Type(DomainPropertyProbability.class),
        @JsonSubTypes.Type(DomainPropertyProbabilityList.class),
        @JsonSubTypes.Type(GuessSchema.class),
        @JsonSubTypes.Type(TransformationRuleDescription.class),
        @JsonSubTypes.Type(ConnectWorkerContainer.class),
        @JsonSubTypes.Type(DashboardEntity.class),
        @JsonSubTypes.Type(DashboardWidgetSettings.class),
        @JsonSubTypes.Type(DataLakeMeasure.class),
        @JsonSubTypes.Type(EventGrounding.class),
        @JsonSubTypes.Type(TopicDefinition.class),
        @JsonSubTypes.Type(TransportFormat.class),
        @JsonSubTypes.Type(TransportProtocol.class),
        @JsonSubTypes.Type(WildcardTopicMapping.class),
        @JsonSubTypes.Type(ElementStatusInfoSettings.class),
        @JsonSubTypes.Type(OutputStrategy.class),
        @JsonSubTypes.Type(PropertyRenameRule.class),
        @JsonSubTypes.Type(TransformOperation.class),
        @JsonSubTypes.Type(EventPropertyQualityRequirement.class),
        @JsonSubTypes.Type(EventStreamQualityRequirement.class),
        @JsonSubTypes.Type(MeasurementCapability.class),
        @JsonSubTypes.Type(MeasurementObject.class),
        @JsonSubTypes.Type(MeasurementProperty.class),
        @JsonSubTypes.Type(RuntimeOptionsRequest.class),
        @JsonSubTypes.Type(EventProperty.class),
        @JsonSubTypes.Type(EventSchema.class),
        @JsonSubTypes.Type(ValueSpecification.class),
        @JsonSubTypes.Type(Option.class),
        @JsonSubTypes.Type(PropertyValueSpecification.class),
        @JsonSubTypes.Type(StaticProperty.class),
        @JsonSubTypes.Type(SupportedProperty.class),
        @JsonSubTypes.Type(BoundPipelineElement.class),
        @JsonSubTypes.Type(PipelineTemplateDescriptionContainer.class),
        @JsonSubTypes.Type(PipelineTemplateInvocation.class),
})
public abstract class UnnamedStreamPipesEntity extends AbstractStreamPipesEntity {

  private static final long serialVersionUID = 8051137255998890188L;

  @RdfId
  @RdfProperty(StreamPipes.HAS_ELEMENT_NAME)
  @TsIgnore
  private String elementId;


  public UnnamedStreamPipesEntity() {
    super();
    this.elementId = RdfIdGenerator.makeRdfId(this);
  }

  public UnnamedStreamPipesEntity(UnnamedStreamPipesEntity other) {
    this();
  }

  public UnnamedStreamPipesEntity(String elementId) {
    super();
    this.elementId = elementId;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }
}
