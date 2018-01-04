package org.streampipes.pe.processors.standalone.languagedetection;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.standalone.config.Config;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class LanguageDetectionController extends StandaloneEventProcessorDeclarerSingleton<LanguageDetectionParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		EventPropertyPrimitive e1 = EpRequirements.stringReq();
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		SpDataStream stream1 = new SpDataStream();
		stream1.setEventSchema(schema1);
		
		DataProcessorDescription desc = new DataProcessorDescription("sepa/langdetect", "Language Detection", "Detects the language of a textual property");
		desc.setIconUrl(Config.iconBaseUrl + "/Language_Detection_Icon_HQ.png");
		//TODO check if needed
		stream1.setUri(Config.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._string.toString(),
				"language", "", Utils.createURI(SO.Text));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		MappingProperty textMapping = new MappingPropertyUnary(URI.create(e1.getElementName()), "text", "text property: ", "");
		staticProperties.add(textMapping);
	
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public ConfiguredEventProcessor<LanguageDetectionParameters>
	onInvocation(DataProcessorInvocation sepa) {
		String textMapping = SepaUtils.getMappingPropertyName(sepa,
						"text");

		LanguageDetectionParameters staticParam = new LanguageDetectionParameters(sepa, textMapping);

		return new ConfiguredEventProcessor<>(staticParam, LanguageDetection::new);
	}

}
