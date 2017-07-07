package de.fzi.cep.sepa.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.esper.project.extract.ProjectController;
import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;

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
