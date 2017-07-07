package org.streampipes.pe.processors.esper.number;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.CollectionStaticProperty;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.impl.staticproperty.SupportedProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.runtime.flat.declarer.FlatEpDeclarer;
import org.streampipes.commons.Utils;

public class NumberClassificationController extends FlatEpDeclarer<NumberClassificationParameters> {

	@Override
	public SepaDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventProperty e1 = EpRequirements.domainPropertyReq(SO.Number);
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("classification_number", "Number Classification", "Labels data based on a defined data range");
		
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
		desc.setStaticProperties(staticProperties);
		desc.setOutputStrategies(strategies);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {

		CollectionStaticProperty collection = SepaUtils.getStaticPropertyByInternalName(sepa, "classification_options",
				CollectionStaticProperty.class);
		String propertyName = SepaUtils.getMappingPropertyName(sepa, "to_classify");

		String outputProperty = ((AppendOutputStrategy) sepa.getOutputStrategies().get(0)).getEventProperties().get(0).getRuntimeName();

		List<DomainStaticProperty> domainConcepts = collection.getMembers().stream().map(m -> (DomainStaticProperty) m)
				.collect(Collectors.toList());
		
		List<DataClassification> domainConceptData = domainConcepts.stream()
				.map(m -> new DataClassification(Double.parseDouble(SepaUtils.getSupportedPropertyValue(m, SO.MinValue)),
						Double.parseDouble(SepaUtils.getSupportedPropertyValue(m, SO.MaxValue)),
						SepaUtils.getSupportedPropertyValue(m, SO.Text)))
				.collect(Collectors.toList());

		NumberClassificationParameters staticParam = new NumberClassificationParameters(sepa, propertyName, outputProperty,
				domainConceptData);

		return submit(staticParam, NumberClassification::new, sepa);

	}

}
