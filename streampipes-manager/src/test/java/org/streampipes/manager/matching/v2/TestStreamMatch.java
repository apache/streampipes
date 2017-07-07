package org.streampipes.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.pe.algorithms.esper.aggregate.avg.AggregationController;
import org.streampipes.pe.algorithms.esper.extract.ProjectController;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;

public class TestStreamMatch extends TestCase {

	@Test
	public void testPositiveStreamMatchWithIgnoredGrounding() {

		SepaDescription requiredSepa = new AggregationController().declareModel();
		EventStream offeredStream = new RandomNumberStreamJson().declareModel(new RandomDataProducer().declareModel());
		
		EventStream requiredStream = requiredSepa.getEventStreams().get(0);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new StreamMatch().matchIgnoreGrounding(offeredStream, requiredStream, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testPositiveStreamMatchWithoutRequirementsIgnoredGrounding() {

		SepaDescription requiredSepa = new ProjectController().declareModel();
		EventStream offeredStream = new RandomNumberStreamJson().declareModel(new RandomDataProducer().declareModel());
		
		EventStream requiredStream = requiredSepa.getEventStreams().get(0);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new StreamMatch().matchIgnoreGrounding(offeredStream, requiredStream, errorLog);
		assertTrue(matches);
	}
}
