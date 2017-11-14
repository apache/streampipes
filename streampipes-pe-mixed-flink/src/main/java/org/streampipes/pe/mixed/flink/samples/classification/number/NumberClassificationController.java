package org.streampipes.pe.mixed.flink.samples.classification.number;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.staticproperty.CollectionStaticProperty;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.commons.Utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NumberClassificationController extends AbstractFlinkAgentDeclarer<NumberClassificationParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventProperty e1 = EpRequirements.domainPropertyReq(SO.Number);
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		SpDataStream stream1 = new SpDataStream();
		stream1.setEventSchema(schema1);

		DataProcessorDescription desc = new DataProcessorDescription("classification_number", "Flink Number classification", "Label your data. Based on Apache Flink.");
		desc.setIconUrl(FlinkConfig.getIconUrl("classification-icon"));
		desc.addEventStream(stream1);
		
		List<StaticProperty> staticProperties = new ArrayList<>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "to_classify", "Field to classify", "Name of the field that should be classified"));
		
		List<SupportedProperty> supportedProperties = new ArrayList<>();
		supportedProperties.add(new SupportedProperty(SO.MinValue, true));
		supportedProperties.add(new SupportedProperty(SO.MaxValue, true));
		supportedProperties.add(new SupportedProperty(SO.Text, true));
		
		DomainStaticProperty dsp = new DomainStaticProperty("Class", "Label for range", "Define a value", supportedProperties);
	
		CollectionStaticProperty classificationOptions = new CollectionStaticProperty("classification_options", "Classification options", "", Arrays.asList(dsp), "de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty");
		staticProperties.add(classificationOptions);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

		List<EventProperty> appendProperties = new ArrayList<EventProperty>();
		appendProperties.add(new EventPropertyPrimitive(XSD._string.toString(),
				"output_label", "", Utils.createURI("http://schema.org/Text")));
		
		outputStrategy.setEventProperties(appendProperties);
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	protected FlinkSepaRuntime<NumberClassificationParameters> getRuntime(DataProcessorInvocation graph) {
		CollectionStaticProperty collection = SepaUtils.getStaticPropertyByInternalName(graph, "classification_options",
				CollectionStaticProperty.class);
		String propertyName = SepaUtils.getMappingPropertyName(graph, "to_classify");

		String outputProperty = ((AppendOutputStrategy) graph.getOutputStrategies().get(0)).getEventProperties().get(0)
				.getRuntimeName();

		List<DomainStaticProperty> domainConcepts = collection.getMembers().stream().map(m -> (DomainStaticProperty) m)
				.collect(Collectors.toList());

		List<DataClassification> domainConceptData = domainConcepts.stream()
				.map(m -> new DataClassification(
						Double.parseDouble(SepaUtils.getSupportedPropertyValue(m, SO.MinValue)),
						Double.parseDouble(SepaUtils.getSupportedPropertyValue(m, SO.MaxValue)),
						SepaUtils.getSupportedPropertyValue(m, SO.Text)))
				 .collect(Collectors.toList());

		// TODO find a better solution
//		List<DataClassification> domainConceptData = new ArrayList<>();
//		List<SupportedProperty> supportedProperties = domainConcepts.get(0).getSupportedProperties();
//		for (int i = 0; i < supportedProperties.size() - 2; i = i + 3) {
//			domainConceptData.add(new DataClassification(Double.parseDouble(supportedProperties.get(i).getValue()),
//					Double.parseDouble(supportedProperties.get(i + 1).getValue()),
//					supportedProperties.get(i + 2).getValue()));
//		}

		return new NumberClassificationProgram(
				new NumberClassificationParameters(graph, propertyName, outputProperty, domainConceptData),
				new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
						FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));

//		 return new NumberClassificationProgram(
//		 new NumberClassificationParameters(graph, propertyName,
//		 outputProperty, domainConceptData));
	}

}
