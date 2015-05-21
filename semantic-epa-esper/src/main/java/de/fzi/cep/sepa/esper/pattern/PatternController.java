package de.fzi.cep.sepa.esper.pattern;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.StandardTransportFormat;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MatchingStaticProperty;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;

public class PatternController extends EsperDeclarer<PatternParameters>{

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public SEPA declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventStream stream1 = new EventStream();
		EventStream stream2 = new EventStream();
		
		SEPA desc = new SEPA("/sepa/pattern", "Pattern Detector", "Detects AND/OR/SEQUENCE-based patterns", "", "/sepa/pattern", domains);
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/And_Icon_HQ.png");
		
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream2.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		desc.addEventStream(stream2);
		
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy());
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("operation", "Select Operation");
		operation.addOption(new Option("AND"));
		operation.addOption(new Option("OR"));
		operation.addOption(new Option("SEQUENCE"));
		staticProperties.add(operation);
		
		OneOfStaticProperty timeWindowUnit = new OneOfStaticProperty("time unit", "select time unit");
		timeWindowUnit.addOption(new Option("sec"));
		timeWindowUnit.addOption(new Option("min"));
		timeWindowUnit.addOption(new Option("hrs"));
		staticProperties.add(timeWindowUnit);
		
		FreeTextStaticProperty duration = new FreeTextStaticProperty("duration", "select time");
		staticProperties.add(duration);
		
		staticProperties.add(new MatchingStaticProperty("select matching", ""));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

}
