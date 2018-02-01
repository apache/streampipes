package org.streampipes.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.pe.processors.esper.extract.ProjectController;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;

public class TestStreamMatch extends TestCase {

	@Test
	public void testPositiveStreamMatchWithIgnoredGrounding() {

		DataProcessorDescription requiredSepa = new AggregationController().declareModel();
		SpDataStream offeredStream = new RandomNumberStreamJson().declareModel(new RandomDataProducer().declareModel());
		
		SpDataStream requiredStream = requiredSepa.getSpDataStreams().get(0);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new StreamMatch().matchIgnoreGrounding(offeredStream, requiredStream, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testPositiveStreamMatchWithoutRequirementsIgnoredGrounding() {

		DataProcessorDescription requiredSepa = new ProjectController().declareModel();
		SpDataStream offeredStream = new RandomNumberStreamJson().declareModel(new RandomDataProducer().declareModel());
		
		SpDataStream requiredStream = requiredSepa.getSpDataStreams().get(0);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new StreamMatch().matchIgnoreGrounding(offeredStream, requiredStream, errorLog);
		assertTrue(matches);
	}
}
