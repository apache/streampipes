package org.streampipes.pe.sources.samples.drillbit;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

public class DrillBitProducer implements SemanticEventProducerDeclarer {


	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source_drillBit", "Drill Bit", "Drill Bit");
		//sep.setIconUrl(SourcesConfig.iconBaseUrl + "/DDM_Icon" +"_HQ.png");
		return sep;
	}

	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		List<DataStreamDeclarer> eventStreams = new ArrayList<DataStreamDeclarer>();
		
		eventStreams.add(new WeightOnBit());
		return eventStreams;
	}
}
