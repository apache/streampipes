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

package org.streampipes.model.util;

import org.streampipes.model.ApplicationLink;
import org.streampipes.model.SpDataSequence;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.model.grounding.TopicDefinition;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.grounding.WildcardTopicDefinition;
import org.streampipes.model.grounding.WildcardTopicMapping;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.CustomTransformOutputStrategy;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.KeepOutputStrategy;
import org.streampipes.model.output.ListOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.output.TransformOperation;
import org.streampipes.model.output.TransformOutputStrategy;
import org.streampipes.model.quality.Accuracy;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.quality.EventPropertyQualityRequirement;
import org.streampipes.model.quality.MeasurementCapability;
import org.streampipes.model.quality.MeasurementObject;
import org.streampipes.model.quality.Precision;
import org.streampipes.model.quality.Resolution;
import org.streampipes.model.schema.Enumeration;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.QuantitativeValue;
import org.streampipes.model.schema.ValueSpecification;
import org.streampipes.model.staticproperty.AnyStaticProperty;
import org.streampipes.model.staticproperty.CollectionStaticProperty;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyNary;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.MatchingStaticProperty;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.RemoteOneOfStaticProperty;
import org.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cloner {

  public OutputStrategy outputStrategy(OutputStrategy other) {
    if (other instanceof KeepOutputStrategy) {
      return new KeepOutputStrategy((KeepOutputStrategy) other);
    } else if (other instanceof FixedOutputStrategy) {
      return new FixedOutputStrategy((FixedOutputStrategy) other);
    } else if (other instanceof ListOutputStrategy) {
      return new ListOutputStrategy((ListOutputStrategy) other);
    } else if (other instanceof CustomOutputStrategy) {
      return new CustomOutputStrategy((CustomOutputStrategy) other);
    } else if (other instanceof TransformOutputStrategy) {
      return new TransformOutputStrategy((TransformOutputStrategy) other);
    } else if (other instanceof CustomTransformOutputStrategy) {
      return new CustomTransformOutputStrategy((CustomTransformOutputStrategy) other);
    } else {
      return new AppendOutputStrategy((AppendOutputStrategy) other);
    }
  }

  public StaticProperty staticProperty(StaticProperty o) {
    if (o instanceof FreeTextStaticProperty) {
      return new FreeTextStaticProperty((FreeTextStaticProperty) o);
    } else if (o instanceof OneOfStaticProperty) {
      return new OneOfStaticProperty((OneOfStaticProperty) o);
    } else if (o instanceof RemoteOneOfStaticProperty) {
      return new RemoteOneOfStaticProperty((RemoteOneOfStaticProperty) o);
    } else if (o instanceof MappingPropertyNary) {
      return new MappingPropertyNary((MappingPropertyNary) o);
    } else if (o instanceof DomainStaticProperty) {
      return new DomainStaticProperty((DomainStaticProperty) o);
    } else if (o instanceof AnyStaticProperty) {
      return new AnyStaticProperty((AnyStaticProperty) o);
    } else if (o instanceof CollectionStaticProperty) {
      return new CollectionStaticProperty((CollectionStaticProperty) o);
    } else if (o instanceof MatchingStaticProperty) {
      return new MatchingStaticProperty((MatchingStaticProperty) o);
    } else if (o instanceof RuntimeResolvableOneOfStaticProperty) {
      return new RuntimeResolvableOneOfStaticProperty((RuntimeResolvableOneOfStaticProperty) o);
    } else if (o instanceof RuntimeResolvableAnyStaticProperty) {
      return new RuntimeResolvableAnyStaticProperty((RuntimeResolvableAnyStaticProperty) o);
    } else {
      return new MappingPropertyUnary((MappingPropertyUnary) o);
    }

  }

  public List<TransportProtocol> protocols(List<TransportProtocol> protocols) {
    return protocols.stream().map(o -> protocol(o)).collect(Collectors.toList());
  }

  public TransportProtocol protocol(TransportProtocol protocol) {
    if (protocol instanceof KafkaTransportProtocol) {
      return new KafkaTransportProtocol((KafkaTransportProtocol) protocol);
    } else {
      return new JmsTransportProtocol((JmsTransportProtocol) protocol);
    }
  }

  public List<WildcardTopicMapping> wildcardTopics(List<WildcardTopicMapping> topicMappings) {
    if (topicMappings == null) {
      return new ArrayList<>();
    } else {
      return topicMappings.stream().map(t -> new WildcardTopicMapping(t)).collect(Collectors.toList());
    }
  }

  public EventProperty property(EventProperty o) {
    if (o instanceof EventPropertyPrimitive) {
      return new EventPropertyPrimitive((EventPropertyPrimitive) o);
    } else if (o instanceof EventPropertyList) {
      return new EventPropertyList((EventPropertyList) o);
    } else {
      return new EventPropertyNested((EventPropertyNested) o);
    }
  }

  public ValueSpecification valueSpecification(ValueSpecification o) {
    if (o instanceof QuantitativeValue) {
      return new QuantitativeValue((QuantitativeValue) o);
    } else {
      return new Enumeration((Enumeration) o);
    }
  }

  public EventPropertyQualityRequirement qualityreq(EventPropertyQualityRequirement o) {
    // TODO Auto-generated method stub
    return null;
  }

  public EventPropertyQualityDefinition qualitydef(EventPropertyQualityDefinition o) {
    if (o instanceof Accuracy) {
      return new Accuracy((Accuracy) o);
    } else if (o instanceof Precision) {
      return new Precision((Precision) o);
    } else {
      return new Resolution((Resolution) o);
    }
  }

  public List<SpDataSequence> seq(List<SpDataSequence> spDataStreams) {
    return spDataStreams.stream().map(s -> mapSequence(s)).collect(Collectors.toList());
  }

  public List<SpDataStream> streams(List<SpDataStream> spDataStreams) {
    return spDataStreams.stream().map(s -> new SpDataStream(s)).collect(Collectors.toList());
  }

  public SpDataSequence mapSequence(SpDataSequence seq) {
    if (seq instanceof SpDataStream) {
      return new SpDataStream((SpDataStream) seq);
    } else {
      return new SpDataSet((SpDataSet) seq);
    }
  }

  public SpDataStream stream(SpDataStream other) {
    return new SpDataStream(other);
  }

  public List<OutputStrategy> strategies(List<OutputStrategy> outputStrategies) {
    if (outputStrategies != null) {
      return outputStrategies.stream().map(o -> outputStrategy(o)).collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public List<StaticProperty> staticProperties(
          List<StaticProperty> staticProperties) {
    return staticProperties.stream().map(o -> staticProperty(o)).collect(Collectors.toList());
  }

  public List<TransportFormat> transportFormats(
          List<TransportFormat> transportFormats) {
    return transportFormats.stream().map(t -> new TransportFormat(t)).collect(Collectors.toList());
  }

  public List<EventProperty> properties(List<EventProperty> eventProperties) {
    return eventProperties.stream().map(o -> new Cloner().property(o)).collect(Collectors.toList());
  }

  public List<TransformOperation> transformOperations(List<TransformOperation> transformOperations) {
    return transformOperations.stream().map(o -> new TransformOperation(o)).collect(Collectors.toList());
  }

  public List<EventPropertyQualityRequirement> reqEpQualitities(
          List<EventPropertyQualityRequirement> requiresEventPropertyQualities) {
    return requiresEventPropertyQualities.stream().map(o -> new Cloner().qualityreq(o)).collect(Collectors.toList());
  }

  public List<EventPropertyQualityDefinition> provEpQualities(
          List<EventPropertyQualityDefinition> eventPropertyQualities) {
    return eventPropertyQualities.stream().map(o -> new Cloner().qualitydef(o)).collect(Collectors.toList());
  }

  public List<Option> options(List<Option> options) {
    return options.stream().map(o -> new Option(o)).collect(Collectors.toList());
  }

  public List<SupportedProperty> supportedProperties(
          List<SupportedProperty> supportedProperties) {
    return supportedProperties.stream().map(s -> new SupportedProperty(s)).collect(Collectors.toList());
  }

  public List<String> epaTypes(List<String> epaTypes) {
    return epaTypes;
  }

  public List<String> ecTypes(List<String> ecTypes) {
    return ecTypes;
  }

  public List<MeasurementCapability> mc(
          List<MeasurementCapability> measurementCapability) {
    return measurementCapability.stream().map(m -> new MeasurementCapability(m)).collect(Collectors.toList());
  }

  public List<MeasurementObject> mo(List<MeasurementObject> measurementObject) {
    return measurementObject.stream().map(m -> new MeasurementObject(m)).collect(Collectors.toList());
  }

  public List<ApplicationLink> al(List<ApplicationLink> applicationLinks) {
    return applicationLinks.stream().map(m -> new ApplicationLink(m)).collect(Collectors.toList());
  }

  public TopicDefinition topicDefinition(TopicDefinition topicDefinition) {
    if (topicDefinition instanceof SimpleTopicDefinition) {
      return new SimpleTopicDefinition((SimpleTopicDefinition) topicDefinition);
    } else {
      return new WildcardTopicDefinition((WildcardTopicDefinition) topicDefinition);
    }
  }
}
