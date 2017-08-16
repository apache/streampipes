package org.streampipes.pe.processors.esper.distribution;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Distribution implements EventProcessor<DistributionParameters> {

	private Map<String, Integer> currentDistribution;
	private String propertyName;
	private int batchSize;
	private int eventCount = 0;
	private List<String> queue;
	
	private SpOutputCollector collector;

	private ObjectMapper mapper;
	
	
	@Override
	public void bind(DistributionParameters parameters,
			SpOutputCollector collector) {
		this.currentDistribution = new HashMap<String, Integer>();
		this.propertyName = parameters.getMappingProperty();
		this.batchSize = parameters.getTimeWindow();
		this.queue = new ArrayList<>();		
		this.collector = collector;
		mapper = new ObjectMapper();

	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		updateDistribution(event);
		System.out.println(toOutputFormat());
		collector.onEvent(mapper.convertValue(toOutputFormat(), new TypeReference<Map<String, Object>>() {}));
	}
	
	private void updateDistribution(Map<String, Object> event) {
		String currentKey = (String) event.get(propertyName);
		if (currentDistribution.containsKey(currentKey))
		{
			currentDistribution.put(currentKey, currentDistribution.get(currentKey) +1);
		}
		else
			currentDistribution.put(currentKey, 1);
		eventCount++;
		queue.add(currentKey);
		if (eventCount >= batchSize)
		{
			currentDistribution.put(queue.get(0), currentDistribution.get(queue.get(0)) -1);
			queue.remove(0);
			eventCount = batchSize;
		}
	}

//	private String toOutputFormat()
//	{
//		JsonArray array = new JsonArray();
//		for(String key : currentDistribution.keySet())
//		{
//			JsonObject object = new JsonObject();
//			object.addProperty("key", key);
//			object.addProperty("value", (int) Math.round((currentDistribution.get(key)/((double)eventCount)*100)));
//			array.add(object);
//		}
//		return array.toString();
//	}
	
	private DistributionResult toOutputFormat()
	{
		List<DistributionRow> rows = new ArrayList<DistributionRow>();
		for(String key : currentDistribution.keySet())
		{
			DistributionRow row = new DistributionRow(key, (int) Math.round((currentDistribution.get(key)/((double)eventCount)*100)));
			rows.add(row);
		}
		return new DistributionResult(rows);
	}

	@Override
	public void discard() {
		this.currentDistribution.clear();
	}

}
