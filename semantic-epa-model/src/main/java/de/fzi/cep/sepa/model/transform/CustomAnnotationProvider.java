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
				de.fzi.cep.sepa.model.impl.MappingPropertyUnary.class, 
				de.fzi.cep.sepa.model.impl.MappingPropertyNary.class, 
				de.fzi.cep.sepa.model.impl.EventPropertyList.class,
				de.fzi.cep.sepa.model.impl.EventPropertyNested.class, 
				de.fzi.cep.sepa.model.impl.EventPropertyPrimitive.class, 
				de.fzi.cep.sepa.model.impl.MatchingStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.graph.SEC.class, 
				de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph.class, 
				de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.EventStream.class, 
				de.fzi.cep.sepa.model.impl.EventGrounding.class, 
				de.fzi.cep.sepa.model.impl.graph.SEP.class, 
				de.fzi.cep.sepa.model.impl.graph.SEPA.class, 
				de.fzi.cep.sepa.model.impl.output.OutputStrategy.class, 
				de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy.class,
				de.fzi.cep.sepa.model.impl.StaticProperty.class, 
				de.fzi.cep.sepa.model.impl.OneOfStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.AnyStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.FreeTextStaticProperty.class, 
				de.fzi.cep.sepa.model.impl.Option.class, 
				de.fzi.cep.sepa.model.impl.MappingProperty.class, 
				de.fzi.cep.sepa.model.impl.graph.SECInvocationGraph.class
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
