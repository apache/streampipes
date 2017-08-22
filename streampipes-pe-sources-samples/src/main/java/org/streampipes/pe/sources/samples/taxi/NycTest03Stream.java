package org.streampipes.pe.sources.samples.taxi;

import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.pe.sources.samples.config.SampleSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.commons.Utils;

import java.io.File;

public class NycTest03Stream extends AbstractNycStream{

	public NycTest03Stream()
	{
		super(NycSettings.test03Topic);
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = new EventStream();
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Taxi_Icon_2" +"_HQ.png");
		EventSchema schema = NycTaxiUtils.getEventSchema();

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.kafkaProtocol(NycSettings.test01Topic));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		stream.setEventSchema(schema);
		stream.setName("Test Stream 3");
		stream.setDescription("NYC Taxi Debs Test Stream 3");
		stream.setUri("test03");
		return stream;
	}

	@Override
	public void executeStream() {
		File file = new File(NycSettings.test03DatasetFilename);
		executeReplay(file);
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
}
