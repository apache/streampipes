package org.streampipes.pe.sources.samples.mobile;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class MobileStreamProducer implements SemanticEventProducerDeclarer {

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source/mobile", "Mobile phone events", "Mobile phone event producer");
		sep.setIconUrl(SourcesConfig.iconBaseUrl + "/Mobile_Phone" +"_HQ.png");
		
		return sep;
	}

	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		List<DataStreamDeclarer> streams = new ArrayList<DataStreamDeclarer>();
			
		streams.add(new MobileLocationStream());	
			
		return streams;
	}

}
