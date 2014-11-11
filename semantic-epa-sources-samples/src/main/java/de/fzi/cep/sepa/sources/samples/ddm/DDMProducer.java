package de.fzi.cep.sepa.sources.samples.ddm;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class DDMProducer implements SemanticEventProducerDeclarer{

	@Override
	public SEP declareModel() {
		SEP sep = new SEP("/ddm", "DDM", "Derrick Drilling Machine", "", Utils.createDomain(Domain.DOMAIN_PROASENSE), new EventSource());
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/DDM_Icon" +"_HQ.png");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();
		
		//eventStreams.add(new GearLubeOilTemperature());
		//eventStreams.add(new Torque());
		eventStreams.add(new SpeedShaft());
		//eventStreams.add(new HookLoad());
		//eventStreams.add(new SwivelTemperature());
		return eventStreams;
	}

}
