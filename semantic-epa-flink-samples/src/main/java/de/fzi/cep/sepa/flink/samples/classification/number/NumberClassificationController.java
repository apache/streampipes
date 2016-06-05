package de.fzi.cep.sepa.flink.samples.classification.number;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class NumberClassificationController extends AbstractFlinkAgentDeclarer<NumberClassificationParameters> {

	@Override
	public SepaDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventProperty e1 = EpRequirements.domainPropertyReq(SO.Number);
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("classification_number", "Flink Number classification", "Label your data. Based on Apache Flink.");
		
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
				"output_label", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Text")));
		
		outputStrategy.setEventProperties(appendProperties);
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	protected FlinkSepaRuntime<NumberClassificationParameters> getRuntime(SepaInvocation graph) {
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
				new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));

//		 return new NumberClassificationProgram(
//		 new NumberClassificationParameters(graph, propertyName,
//		 outputProperty, domainConceptData));
	}

}
