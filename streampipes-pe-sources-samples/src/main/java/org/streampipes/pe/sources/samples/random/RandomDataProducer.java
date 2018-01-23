package org.streampipes.pe.sources.samples.random;

import com.google.gson.Gson;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.serializers.json.GsonSerializer;

import java.util.ArrayList;
import java.util.List;

public class RandomDataProducer implements SemanticEventProducerDeclarer {
	
	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source_random", "Random", "Random Event Producer");
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
		//streams.add(new RandomTextStream());
//		streams.add(new RandomNumberStreamJson());
//		streams.add(new RandomNumberStreamList());
//		streams.add(new ComplexRandomStream());
		streams.add(new RandomNumberStreamWildcard());
		//streams.add(new RandomNumberStreamThrift());
		//streams.add(new NestedListRandomNumberStream());
		//streams.add(new NestedRandomNumberStream());
	
	
		return streams;
	}

	public static void main(String[] args) {
			DataSourceDescription desc = new RandomDataProducer().declareModel();
		SpDataStream stream = new RandomNumberStreamWildcard().declareModel(desc);

		Gson gson = GsonSerializer.getGsonWithIds();
		System.out.println(gson.toJson(stream));


	}

}
