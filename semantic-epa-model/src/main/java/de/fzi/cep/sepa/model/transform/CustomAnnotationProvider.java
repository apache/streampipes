package de.fzi.cep.sepa.model.transform;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.clarkparsia.empire.util.EmpireAnnotationProvider;

public class CustomAnnotationProvider implements EmpireAnnotationProvider{

	
	@Override
	public Collection<Class<?>> getClassesWithAnnotation(
			Class<? extends Annotation> arg0) {
		if (arg0.getName().equals("com.clarkparsia.empire.annotation.RdfsClass")) return CustomAnnotationProvider.getAnnotatedClasses();
		else return Collections.emptyList();
	}
	
	public static List<Class<?>> getAnnotatedClasses()
	{
		return Arrays.asList(
				de.fzi.cep.sepa.model.impl.output.ListOutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary.class, 
				de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList.class,
				de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested.class, 
				de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.MatchingStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.graph.SecDescription.class, 
				de.fzi.cep.sepa.model.impl.graph.SepaInvocation.class, 
				de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.EventStream.class, 
				de.fzi.cep.sepa.model.impl.quality.Accuracy.class, 
				de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition.class, 
				de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityRequirement.class, 
				de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition.class, 
				de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement.class, 
				de.fzi.cep.sepa.model.impl.quality.Frequency.class, 
				de.fzi.cep.sepa.model.impl.quality.Latency.class, 
				de.fzi.cep.sepa.model.impl.quality.MeasurementProperty.class, 
				de.fzi.cep.sepa.model.impl.quality.MeasurementRange.class, 
				de.fzi.cep.sepa.model.impl.quality.Precision.class, 
				de.fzi.cep.sepa.model.impl.quality.Resolution.class, 
				de.fzi.cep.sepa.model.impl.EventGrounding.class, 
				de.fzi.cep.sepa.model.impl.graph.SepDescription.class, 
				de.fzi.cep.sepa.model.impl.graph.SepaDescription.class, 
				de.fzi.cep.sepa.model.impl.output.OutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy.class,
				de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.AnyStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.Option.class, 
				de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty.class, 
				de.fzi.cep.sepa.model.impl.graph.SecInvocation.class,
				de.fzi.cep.sepa.model.impl.TransportFormat.class,
				de.fzi.cep.sepa.model.impl.JmsTransportProtocol.class,
				de.fzi.cep.sepa.model.impl.KafkaTransportProtocol.class,
				de.fzi.cep.sepa.model.impl.TransportProtocol.class,
				de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty.class,
				de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty.class
		);
	}

	public static String getAnnotatedClassesAsString() {
		String result = "";
		for(Class clazz : getAnnotatedClasses())
		{
			result += clazz.getCanonicalName();
		}
		return result;
	}

}
