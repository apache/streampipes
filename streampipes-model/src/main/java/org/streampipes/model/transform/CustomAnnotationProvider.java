package org.streampipes.model.transform;

import org.streampipes.empire.core.empire.util.EmpireAnnotationProvider;
import org.streampipes.model.impl.ApplicationLink;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.ElementStatusInfoSettings;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.KafkaTransportProtocol;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.model.impl.eventproperty.Enumeration;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.eventproperty.QuantitativeValue;
import org.streampipes.model.impl.eventproperty.ValueSpecification;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.CustomOutputStrategy;
import org.streampipes.model.impl.output.FixedOutputStrategy;
import org.streampipes.model.impl.output.ListOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.output.RenameOutputStrategy;
import org.streampipes.model.impl.output.ReplaceOutputStrategy;
import org.streampipes.model.impl.output.UriPropertyMapping;
import org.streampipes.model.impl.quality.Accuracy;
import org.streampipes.model.impl.quality.EventPropertyQualityDefinition;
import org.streampipes.model.impl.quality.EventPropertyQualityRequirement;
import org.streampipes.model.impl.quality.EventStreamQualityDefinition;
import org.streampipes.model.impl.quality.EventStreamQualityRequirement;
import org.streampipes.model.impl.quality.Frequency;
import org.streampipes.model.impl.quality.Latency;
import org.streampipes.model.impl.quality.MeasurementCapability;
import org.streampipes.model.impl.quality.MeasurementObject;
import org.streampipes.model.impl.quality.MeasurementProperty;
import org.streampipes.model.impl.quality.MeasurementRange;
import org.streampipes.model.impl.quality.Precision;
import org.streampipes.model.impl.quality.Resolution;
import org.streampipes.model.impl.staticproperty.AnyStaticProperty;
import org.streampipes.model.impl.staticproperty.CollectionStaticProperty;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyNary;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.MatchingStaticProperty;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.RemoteOneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.impl.staticproperty.SupportedProperty;

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
            EventPropertyPrimitive.class,
            MatchingStaticProperty.class,
            SecDescription.class,
            SepaInvocation.class,
            FixedOutputStrategy.class,
            AppendOutputStrategy.class,
            EventStream.class,
            Accuracy.class,
            EventPropertyQualityDefinition.class,
            EventPropertyQualityRequirement.class,
            EventStreamQualityDefinition.class,
            EventStreamQualityRequirement.class,
            Frequency.class,
            Latency.class,
            MeasurementProperty.class,
            MeasurementRange.class,
            Precision.class,
            Resolution.class,
            EventGrounding.class,
            SepDescription.class,
            SepaDescription.class,
            OutputStrategy.class,
            RenameOutputStrategy.class,
            StaticProperty.class,
            OneOfStaticProperty.class,
            RemoteOneOfStaticProperty.class,
            AnyStaticProperty.class,
            FreeTextStaticProperty.class,
            Option.class,
            MappingProperty.class,
            SecInvocation.class,
            TransportFormat.class,
            JmsTransportProtocol.class,
            KafkaTransportProtocol.class,
            TransportProtocol.class,
            DomainStaticProperty.class,
            SupportedProperty.class,
            CollectionStaticProperty.class,
            EcType.class,
            EpaType.class,
            ReplaceOutputStrategy.class,
            UriPropertyMapping.class,
            MeasurementCapability.class,
            MeasurementObject.class,
            ValueSpecification.class,
            Enumeration.class,
            QuantitativeValue.class,
            ApplicationLink.class,
            ElementStatusInfoSettings.class
    );
  }
}
