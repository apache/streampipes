package de.fzi.cep.sepa.esper.distribution;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class DistributionController extends FlatEpDeclarer<DistributionParameters>{

	@Override
	public SepaDescription declareModel() {
			
		EventStream stream1 = new EventStream();
		
		EventSchema schema1 = new EventSchema();
		EventPropertyPrimitive p1 = EpRequirements.stringReq();
		schema1.addEventProperty(p1);
		
		SepaDescription desc = new SepaDescription("distribution", "Distribution", "Computes current value distribution");

		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(schema1);
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		EventPropertyList outputProperty = new EventPropertyList();
		outputProperty.setRuntimeName("rows");
		
		EventPropertyPrimitive key = new EventPropertyPrimitive(XSD._string.toString(), "key", "", Utils.createURI(SO.Text));
		EventPropertyPrimitive value = new EventPropertyPrimitive(XSD._integer.toString(), "value", "", Utils.createURI(SO.Number));
		//EventPropertyNested innerProperties = new EventPropertyNested("result", Arrays.asList(key, value));
		
		outputProperty.setEventProperties(Arrays.asList(key, value));
		
		strategies.add(new FixedOutputStrategy(Arrays.asList(outputProperty)));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty rpmThreshold = new FreeTextStaticProperty("batchWindow", "Batch size", "");
		staticProperties.add(rpmThreshold);

		staticProperties.add(new MappingPropertyUnary(URI.create(p1.getElementName()), "mapping", "Property mapping", ""));

		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		int timeWindow = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(sepa, "batchWindow"));
		
		String mapping = SepaUtils.getMappingPropertyName(sepa, "mapping");
		
		DistributionParameters staticParam = new DistributionParameters(
				sepa, 
				timeWindow,
				mapping);
	
		try {
			invokeEPRuntime(staticParam, Distribution::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}

}
