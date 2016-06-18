package de.fzi.cep.sepa.manager.matching.v2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.messages.MatchingResultMessage;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;

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
		EventStream offer = producer.getEventStreams().get(0).declareModel(producer.declareModel());
		
		SepaDescription requirement = (new AggregationController().declareModel());
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		boolean match = new GroundingMatch().match(offer.getEventGrounding(), requirement.getSupportedGrounding(), errorLog);
		
		assertTrue(match);
		
	}
}
