package org.streampipes.pe.sources.samples.taxi;

import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.MessageFormat;
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
	public SpDataStream declareModel(DataSourceDescription sep) {
		SpDataStream stream = new SpDataStream();
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
