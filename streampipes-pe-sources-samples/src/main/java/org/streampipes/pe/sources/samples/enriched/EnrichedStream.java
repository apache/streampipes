package org.streampipes.pe.sources.samples.enriched;
import java.util.Random;

import com.google.gson.JsonObject;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.pe.sources.samples.config.AkerVariables;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;
import org.streampipes.commons.Utils;

public class EnrichedStream implements DataStreamDeclarer {
	
	public JsonObject generateSampleEvent() {
		
		Random random = new Random();
		
		JsonObject obj = new JsonObject();
		obj.addProperty("timestamp", System.currentTimeMillis());
		obj.addProperty("eventName", "Cep");
		obj.addProperty("rpm", random.nextDouble());
		obj.addProperty("torque", random.nextDouble());
		obj.addProperty("hook_load", random.nextDouble());
		obj.addProperty("oil_temp_gearbox", random.nextDouble());
		obj.addProperty("pressure_gearbox", random.nextDouble());
		obj.addProperty("oil_temp_swivel", random.nextDouble());
		obj.addProperty("wob", random.nextDouble());
		obj.addProperty("temp_ambient", random.nextDouble());
		obj.addProperty("ram_pos_setpoint", random.nextDouble());
		obj.addProperty("ram_pos_measured", random.nextDouble());
		obj.addProperty("ram_vel_setpoint", random.nextDouble());
		obj.addProperty("ram_vel_measured", random.nextDouble());
		obj.addProperty("mru_pos", random.nextDouble());
		obj.addProperty("mru_vel", random.nextDouble());
		obj.addProperty("ibop", random.nextDouble());
		obj.addProperty("hoist_press_A", random.nextDouble());
		obj.addProperty("hoist_press_B", random.nextDouble());
		
		return obj;
	}
	
	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {

		SpDataStream stream = new SpDataStream();
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.Enriched.topic()));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		stream.setEventSchema(EnrichedUtils.getEnrichedSchema());
		stream.setName(AkerVariables.Enriched.eventName());
		stream.setDescription(AkerVariables.Enriched.description());
		stream.setUri(sep.getUri() + "/mhwirthenriched");

		return stream;
	}

	@Override
	public void executeStream() {
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
	
	
}
