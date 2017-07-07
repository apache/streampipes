package org.streampipes.pe.sources.mhwirth.ddm;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.mhwirth.config.SourcesConfig;

public class DDMProducer implements SemanticEventProducerDeclarer{

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source-ddm", "DDM", "Derrick Drilling Machine");
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/DDM_Icon" +"_HQ.png");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();
		
		eventStreams.add(new GearLubeOilTemperature());
		eventStreams.add(new Torque());
		eventStreams.add(new SpeedShaft());
		eventStreams.add(new HookLoad());
		eventStreams.add(new SwivelTemperature());
		eventStreams.add(new GearboxPressure());
		return eventStreams;
	}

}
