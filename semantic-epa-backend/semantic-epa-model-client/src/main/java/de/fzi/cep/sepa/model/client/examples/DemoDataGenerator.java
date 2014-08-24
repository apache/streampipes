package de.fzi.cep.sepa.model.client.examples;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.client.Domain;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SourceClient;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.RadioInput;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.client.input.TextInput;
import de.fzi.sepa.model.client.manager.SEPAManager;

public class DemoDataGenerator {

	public static void generateDemoData()
	{
		SEPAManager manager = SEPAManager.INSTANCE;
		
		//generate event sources
		//proasense
		List<SourceClient> sources = new ArrayList<SourceClient>();
		SourceClient ddm = SourceFactory.generateSourceMock("DDM", "Derrick Drilling Machine", createDomainList(Domain.DOMAIN_PROASENSE), "DDM_Icon");
		SourceClient ibop = SourceFactory.generateSourceMock("IBOP", "Internal Blowout Preventer", createDomainList(Domain.DOMAIN_PROASENSE));
		SourceClient weather = SourceFactory.generateSourceMock("Weather", "Weather Information", createDomainList(Domain.DOMAIN_PROASENSE, Domain.DOMAIN_PERSONAL_ASSISTANT), "Wetter_Icon");
		SourceClient drillBit = SourceFactory.generateSourceMock("DrillBit", "Drill String", createDomainList(Domain.DOMAIN_PROASENSE));
		
		//personalassistant
		SourceClient mobilePhone = SourceFactory.generateSourceMock("Mobile Phone", "Mobile Phone Events", createDomainList(Domain.DOMAIN_PERSONAL_ASSISTANT), "Mobile_Phone");
		SourceClient twitter = SourceFactory.generateSourceMock("Twitter", "Twitter Events", createDomainList(Domain.DOMAIN_PERSONAL_ASSISTANT), "Twitter_Icon");
		
		
		sources.add(ddm);
		sources.add(ibop);
		sources.add(weather);
		sources.add(drillBit);
		sources.add(mobilePhone);
		sources.add(twitter);
		
		manager.setStoredSources(sources);
		
		
		//generate event producers
		List<StreamClient> seps = new ArrayList<StreamClient>();
		
		StreamClient speedShaft = StreamFactory.generateSEPMock("DDM Speed Shaft", "", ddm.getElementId(), "DDM_Speed_Icon");
		StreamClient torque = StreamFactory.generateSEPMock("DDM Torque", "", ddm.getElementId(), "Torque_Icon");
		StreamClient hookLoad = StreamFactory.generateSEPMock("DDM Hook Load", "", ddm.getElementId(), "HookLoad_Icon");
		StreamClient gearboxTemperature = StreamFactory.generateSEPMock("DDM Gearbox Temperature", "", ddm.getElementId());
		StreamClient ddmSwivelTemperature = StreamFactory.generateSEPMock("DDM Swivel Temperature", "", ddm.getElementId());
		
		StreamClient ibopStatus = StreamFactory.generateSEPMock("IBOP Status", "", ibop.getElementId());
		
		StreamClient ambientTemperature = StreamFactory.generateSEPMock("Ambient Temperature", "", weather.getElementId(), "Temperature_Icon");
		StreamClient wind = StreamFactory.generateSEPMock("Wind conditions", "", weather.getElementId(), "Wind_Icon");
		
		StreamClient weightOnBit = StreamFactory.generateSEPMock("Weight On Bit", "", drillBit.getElementId());
		
		StreamClient battery = StreamFactory.generateSEPMock("Battery Level", "", mobilePhone.getElementId(), "Battery_Icon");
		StreamClient calendar = StreamFactory.generateSEPMock("Calendar Appointment Scheduled", "", mobilePhone.getElementId(), "Calendar_Appointment");
		StreamClient location = StreamFactory.generateSEPMock("Location", "current location", mobilePhone.getElementId(), "Location_Icon");
		StreamClient microphone = StreamFactory.generateSEPMock("Microphone", "publishes current sound level", mobilePhone.getElementId(), "Microphone_Icon");
		StreamClient tweet = StreamFactory.generateSEPMock("Tweet", "", twitter.getElementId(), "Tweet_Icon");
		StreamClient retweet = StreamFactory.generateSEPMock("Retweet", "", twitter.getElementId(), "Retweet_Icon");
		
		
		seps.add(speedShaft);
		seps.add(torque);
		seps.add(hookLoad);
		seps.add(gearboxTemperature);
		seps.add(ddmSwivelTemperature);
		seps.add(ibopStatus);
		seps.add(ambientTemperature);
		seps.add(weightOnBit);
		seps.add(battery);
		seps.add(calendar);
		seps.add(location);
		seps.add(microphone);
		seps.add(tweet);
		seps.add(retweet);
		seps.add(wind);
		
		manager.setStoredSEPs(seps);
	
		
		//generate epas
		List<SEPAClient> sepas = new ArrayList<SEPAClient>();
		
		SEPAClient drillingStart = SEPAFactory.generateSEPAMock("Drilling Start", "Detects start of a drilling process", createDomainList(Domain.DOMAIN_PROASENSE), "Drilling_Start");
		drillingStart.setInputNodes(1);
		
		
		SEPAClient drillingStop = SEPAFactory.generateSEPAMock("Drilling Stop", "Detects stop of a drilling process", createDomainList(Domain.DOMAIN_PROASENSE), "Drilling_Stop");
		drillingStop.setInputNodes(1);
		
		SEPAClient coolingDownStart = SEPAFactory.generateSEPAMock("Cooling Down Start", "Detects start of cooling down process", createDomainList(Domain.DOMAIN_PROASENSE), "Cooling_Down_Start");
		coolingDownStart.setInputNodes(1);
		
		
		SEPAClient coolingDownStop = SEPAFactory.generateSEPAMock("Cooling Down Stop", "Detects stop of a cooling down process", createDomainList(Domain.DOMAIN_PROASENSE), "Cooling_Down_Stop");
		coolingDownStop.setInputNodes(1);
		
		
		SEPAClient suddenIncreaseDecrease = SEPAFactory.generateSEPAMock("Sudden Increase/Decrease", "Detects increasing/decreasing numerical values", createDomainList(Domain.DOMAIN_PROASENSE), "Sudden_Increase_Decrease");
		suddenIncreaseDecrease.setInputNodes(1);
		
		List<Option> tempOptions = new ArrayList<Option>();
		tempOptions.add(new Option("increase"));
		tempOptions.add(new Option("decrease"));
		
		List<StaticProperty> properties = new ArrayList<StaticProperty>();
		
		StaticProperty sid1 = new StaticProperty("Operation", "select operation:", new RadioInput(tempOptions));	
		StaticProperty sid2 = new StaticProperty("Rate", "increase/decrease rate / percentage", new TextInput("Value", ""));
		properties.add(sid1);
		properties.add(sid2);
		suddenIncreaseDecrease.setStaticProperties(properties);
		
		
		SEPAClient simpleTextFilter = SEPAFactory.generateSEPAMock("Textual Filter", "Filter (operates on textual data)", createDomainList(Domain.DOMAIN_PERSONAL_ASSISTANT), "Textual_Filter_Icon");
		simpleTextFilter.setInputNodes(1);
		List<Option> textOptions = new ArrayList<Option>();
		textOptions.add(new Option("contains"));
		textOptions.add(new Option("matches"));
		
		List<StaticProperty> textFilterProperties = new ArrayList<StaticProperty>();
		
		StaticProperty sid3 = new StaticProperty("Operation", "select operation", new SelectFormInput(textOptions));
		StaticProperty sid4 = new StaticProperty("Keyword", "", new TextInput("Keyword", ""));
		textFilterProperties.add(sid3);
		textFilterProperties.add(sid4);
		simpleTextFilter.setStaticProperties(textFilterProperties);
		
		
		SEPAClient proximity = SEPAFactory.generateSEPAMock("Proximity", "Detects proximity between two location-based objects", createDomainList(Domain.DOMAIN_PERSONAL_ASSISTANT), "Proximity_Icon");
		SEPAClient sentiment = SEPAFactory.generateSEPAMock("Sentiment Detection", "calculates sentiment of textual data", createDomainList(Domain.DOMAIN_PERSONAL_ASSISTANT), "Sentiment_Detection_Icon");
		SEPAClient language = SEPAFactory.generateSEPAMock("Language Detection", "language detection on textual data properties", createDomainList(Domain.DOMAIN_PERSONAL_ASSISTANT), "Language_Detection_Icon");
		
		
		// basic epas
		SEPAClient and = SEPAFactory.generateSEPAMock("AND", "description", createDomainList(Domain.DOMAIN_INDEPENDENT), "And_Icon");
		and.setInputNodes(2);
		
		StaticProperty sid5 = new StaticProperty("Time Window", "", new TextInput("Time", ""));
		
		List<Option> andOptions = new ArrayList<Option>();
		andOptions.add(new Option("sec"));
		andOptions.add(new Option("min"));
		andOptions.add(new Option("hrs"));
		
		StaticProperty sid6 = new StaticProperty("Unit", "", new RadioInput(andOptions));
		
		List<StaticProperty> andProperties = new ArrayList<StaticProperty>();
		andProperties.add(sid5);
		andProperties.add(sid6);
		
		and.setStaticProperties(andProperties);
		
		SEPAClient or = SEPAFactory.generateSEPAMock("OR", "description", createDomainList(Domain.DOMAIN_INDEPENDENT), "Or_Icon");
		SEPAClient seq = SEPAFactory.generateSEPAMock("SEQUENCE", "description", createDomainList(Domain.DOMAIN_INDEPENDENT), "Sequence_Icon");
		SEPAClient timer = SEPAFactory.generateSEPAMock("Timer", "(time difference between two single events)", createDomainList(Domain.DOMAIN_INDEPENDENT), "Timer_Icon");
		SEPAClient counter = SEPAFactory.generateSEPAMock("Counter", "(counts occurrences / event level", createDomainList(Domain.DOMAIN_INDEPENDENT), "Counter_Icon");
		SEPAClient simpleNumericalFilter = SEPAFactory.generateSEPAMock("Numerical Filter", "description", createDomainList(Domain.DOMAIN_INDEPENDENT), "Numerical_Filter_Icon");
		SEPAClient aggregate = SEPAFactory.generateSEPAMock("Aggregation", "description", createDomainList(Domain.DOMAIN_INDEPENDENT), "Aggregation_Icon");
		
		sepas.add(drillingStart);
		sepas.add(drillingStop);
		sepas.add(coolingDownStart);
		sepas.add(coolingDownStop);
		sepas.add(suddenIncreaseDecrease);
		sepas.add(simpleTextFilter);
		sepas.add(proximity);
		sepas.add(sentiment);
		sepas.add(language);
		
		sepas.add(and);
		sepas.add(or);
		sepas.add(seq);
		sepas.add(timer);
		sepas.add(counter);
		sepas.add(simpleNumericalFilter);
		sepas.add(aggregate);
		
		manager.setStoredSEPAs(sepas);
	
	}
	
	private static List<String> createDomainList(Domain...domains)
	{
		List<String> result = new ArrayList<String>();
		for(Domain domain : domains)
		{
			result.add(domain.toString());
		}
		return result;
	}

}
