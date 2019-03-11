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

package org.streampipes.serializers.jsonld;

import org.streampipes.empire.core.empire.util.EmpireAnnotationProvider;
import org.streampipes.model.ApplicationLink;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.SpDataStreamContainer;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.FormatDescriptionList;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.grounding.ProtocolDescriptionList;
import org.streampipes.model.connect.guess.DomainPropertyProbability;
import org.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.connect.rules.Schema.CreateNestedRuleDescription;
import org.streampipes.model.connect.rules.Schema.DeleteRuleDescription;
import org.streampipes.model.connect.rules.Schema.MoveRuleDescription;
import org.streampipes.model.connect.rules.Schema.RenameRuleDescription;
import org.streampipes.model.connect.rules.Stream.RemoveDuplicatesTransformationRuleDescription;
import org.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.streampipes.model.graph.*;
import org.streampipes.model.grounding.*;
import org.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.streampipes.model.output.*;
import org.streampipes.model.quality.*;
import org.streampipes.model.schema.*;
import org.streampipes.model.staticproperty.*;
import org.streampipes.model.template.BoundPipelineElement;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateDescriptionContainer;
import org.streampipes.model.template.PipelineTemplateInvocation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomAnnotationProvider implements EmpireAnnotationProvider {


  @Override
  public Collection<Class<?>> getClassesWithAnnotation(
          Class<? extends Annotation> arg0) {
    if (arg0.getName().equals("org.streampipes.empire.annotations.RdfsClass")) {
      return getAnnotatedClasses();
    } else {
      return Collections.emptyList();
    }
  }

  private List<Class<?>> getAnnotatedClasses() {
    return Arrays.asList(
            ListOutputStrategy.class,
            CustomOutputStrategy.class,
            MappingPropertyUnary.class,
            MappingPropertyNary.class,
            EventPropertyList.class,
            EventPropertyNested.class,
            EventSchema.class,
            EventPropertyPrimitive.class,
            MatchingStaticProperty.class,
            DataSinkDescription.class,
            DataProcessorInvocation.class,
            FixedOutputStrategy.class,
            AppendOutputStrategy.class,
            SpDataStream.class,
            SpDataSet.class,
            Accuracy.class,
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
            AdapterDescription.class,
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
            AddTimestampRuleDescription.class,
            PropertyRenameRule.class
    );
  }
}
