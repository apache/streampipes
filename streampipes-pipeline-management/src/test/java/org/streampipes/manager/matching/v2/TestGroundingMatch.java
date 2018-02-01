package org.streampipes.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;

public class TestGroundingMatch extends TestCase {

	@Test
	public void testPositiveGroundingMatch() {
		
		TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
		TransportProtocol requirementProtocol = TestUtils.kafkaProtocol();
		
		TransportFormat offerFormat = TestUtils.jsonFormat();
		TransportFormat requirementFormat = TestUtils.jsonFormat();
		
		EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
		EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativeGroundingMatchProtocol() {
		
		TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
		TransportProtocol requirementProtocol = TestUtils.jmsProtocol();
		
		TransportFormat offerFormat = TestUtils.jsonFormat();
		TransportFormat requirementFormat = TestUtils.jsonFormat();
		
		EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
		EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testNegativeGroundingMatchFormat() {
		
		TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
		TransportProtocol requirementProtocol = TestUtils.kafkaProtocol();
		
		TransportFormat offerFormat = TestUtils.jsonFormat();
		TransportFormat requirementFormat = TestUtils.thriftFormat();
		
		EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
		EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testNegativeGroundingMatchBoth() {
		
		TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
		TransportProtocol requirementProtocol = TestUtils.jmsProtocol();
		
		TransportFormat offerFormat = TestUtils.jsonFormat();
		TransportFormat requirementFormat = TestUtils.thriftFormat();
		
		EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
		EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testPositiveGroundingMatchWithRealEpa() {
		
		RandomDataProducer producer = new RandomDataProducer();
		SpDataStream offer = producer.getEventStreams().get(0).declareModel(producer.declareModel());
		
		DataProcessorDescription requirement = (new AggregationController().declareModel());
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		boolean match = new GroundingMatch().match(offer.getEventGrounding(), requirement.getSupportedGrounding(), errorLog);
		
		assertTrue(match);
		
	}
}
