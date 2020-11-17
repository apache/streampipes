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

package org.apache.streampipes.serializers.jsonld;

import io.fogsy.empire.core.empire.util.EmpireAnnotationProvider;
import org.apache.streampipes.model.ApplicationLink;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamContainer;
import org.apache.streampipes.model.base.StreamPipesJsonLdContainer;
import org.apache.streampipes.model.connect.adapter.*;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.connect.grounding.FormatDescriptionList;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescriptionList;
import org.apache.streampipes.model.connect.guess.DomainPropertyProbability;
import org.apache.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.*;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.model.dashboard.DashboardWidgetModel;
import org.apache.streampipes.model.dashboard.VisualizablePipeline;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.graph.*;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.apache.streampipes.model.output.*;
import org.apache.streampipes.model.quality.*;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.schema.*;
import org.apache.streampipes.model.staticproperty.*;
import org.apache.streampipes.model.template.BoundPipelineElement;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateDescriptionContainer;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomAnnotationProvider implements EmpireAnnotationProvider {


  @Override
  public Collection<Class<?>> getClassesWithAnnotation(
          Class<? extends Annotation> arg0) {
    if (arg0.getName().equals("io.fogsy.empire.annotations.RdfsClass")) {
      return getAnnotatedClasses();
    } else {
      return Collections.emptyList();
    }
  }


  /**
   * Do not register abstract classes!!!
   * Just register classes with a default constructor
   */
  private List<Class<?>> getAnnotatedClasses() {
    return Arrays.asList(
            Accuracy.class,
            CodeInputStaticProperty.class,
            ColorPickerStaticProperty.class,
            CustomOutputStrategy.class,
            DataSinkDescription.class,
            DataProcessorInvocation.class,
            EventPropertyList.class,
            EventPropertyNested.class,
            EventPropertyPrimitive.class,
            EventSchema.class,
            ListOutputStrategy.class,
            MappingPropertyUnary.class,
            MappingPropertyNary.class,
            MatchingStaticProperty.class,
            FixedOutputStrategy.class,
            AppendOutputStrategy.class,
            SpDataStream.class,
            SpDataSet.class,
            EventPropertyQualityRequirement.class,
            EventStreamQualityRequirement.class,
            Frequency.class,
            Latency.class,
            MeasurementProperty.class,
            MeasurementRange.class,
            Precision.class,
            Resolution.class,
            EventGrounding.class,
            DataSourceDescription.class,
            DataProcessorDescription.class,
            KeepOutputStrategy.class,
            OneOfStaticProperty.class,
            RemoteOneOfStaticProperty.class,
            AnyStaticProperty.class,
            FreeTextStaticProperty.class,
            FileStaticProperty.class,
            Option.class,
            MappingProperty.class,
            DataSinkInvocation.class,
            TransportFormat.class,
            JmsTransportProtocol.class,
            KafkaTransportProtocol.class,
            MqttTransportProtocol.class,
            TransportProtocol.class,
            DomainStaticProperty.class,
            SupportedProperty.class,
            CollectionStaticProperty.class,
            MeasurementCapability.class,
            MeasurementObject.class,
            Enumeration.class,
            QuantitativeValue.class,
            ApplicationLink.class,
            ElementStatusInfoSettings.class,
            WildcardTopicDefinition.class,
            SimpleTopicDefinition.class,
            RuntimeResolvableAnyStaticProperty.class,
            RuntimeResolvableOneOfStaticProperty.class,
            TransformOutputStrategy.class,
            TransformOperation.class,
            CustomTransformOutputStrategy.class,
            SpecificAdapterSetDescription.class,
            SpecificAdapterStreamDescription.class,
            GenericAdapterStreamDescription.class,
            GenericAdapterSetDescription.class,
            AdapterDescriptionList.class,
            FormatDescription.class,
            FormatDescriptionList.class,
            DomainPropertyProbability.class,
            DomainPropertyProbabilityList.class,
            GuessSchema.class,
            ProtocolDescription.class,
            ProtocolDescriptionList.class,
            PipelineTemplateDescription.class,
            PipelineTemplateInvocation.class,
            BoundPipelineElement.class,
            SpDataStreamContainer.class,
            PipelineTemplateDescriptionContainer.class,
            DeleteRuleDescription.class,
            CreateNestedRuleDescription.class,
            MoveRuleDescription.class,
            RenameRuleDescription.class,
            UnitTransformRuleDescription.class,
            RemoveDuplicatesTransformationRuleDescription.class,
            AddValueTransformationRuleDescription.class,
            CorrectionValueTransformationRuleDescription.class,
            PropertyRenameRule.class,
            AddTimestampRuleDescription.class,
            PropertyRenameRule.class,
            TimestampTranfsformationRuleDescription.class,
            RuntimeOptionsRequest.class,
            RuntimeOptionsResponse.class,
            StaticPropertyAlternative.class,
            StaticPropertyAlternatives.class,
            StaticPropertyGroup.class,
            ConnectWorkerContainer.class,
            RuntimeOptionsResponse.class,
            EventRateTransformationRuleDescription.class,
            SecretStaticProperty.class,
            DashboardWidgetModel.class,
            UserDefinedOutputStrategy.class,
            VisualizablePipeline.class,
            DataExplorerWidgetModel.class,
            StreamPipesJsonLdContainer.class,
            DataLakeMeasure.class
    );
  }
}
