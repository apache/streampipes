package de.fzi.cep.sepa.manager.matching.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.sdk.stream.EpProperties;
import de.fzi.cep.sepa.sdk.epa.EpRequirements;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomNumberStreamJson;

public class TestSchemaMatch extends TestCase {

	@Test
	public void testPositiveSchemaMatch() {

		EventPropertyPrimitive offer1 = EpProperties.integerEp("timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp("timestamp", Geo.lng);
		
		EventPropertyPrimitive requirement1 = EpRequirements.integerReq();
		EventPropertyPrimitive requirement2 = EpRequirements.integerReq();
		
		EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
		EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativeSchemaMatch() {

		EventPropertyPrimitive offer1 = EpProperties.integerEp("timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp("timestamp", Geo.lng);
		
		EventPropertyPrimitive requirement1 = EpRequirements.integerReq();
		EventPropertyPrimitive requirement2 = EpRequirements.stringReq();
		
		EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
		EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testNegativeSchemaMatchDomain() {

		EventPropertyPrimitive offer1 = EpProperties.integerEp("timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp("timestamp", Geo.lng);
		
		EventPropertyPrimitive requirement1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventPropertyPrimitive requirement2 = EpRequirements.stringReq();
		
		EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
		EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
		assertFalse(matches);
	}
	
	@Test
	public void testPositiveSchemaMatchDomain() {

		EventPropertyPrimitive offer1 = EpProperties.integerEp("timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp("timestamp", Geo.lng);
		
		EventPropertyPrimitive requirement1 = EpRequirements.domainPropertyReq(Geo.lat);
		EventPropertyPrimitive requirement2 = EpRequirements.integerReq();
		
		EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
		EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
		assertTrue(matches);
	}
	
	@Test
	public void testPositiveSchemaMatchWithRealSchema() {

		SepaDescription requiredSepa = new AggregationController().declareModel();
		EventStream offeredStream = new RandomNumberStreamJson().declareModel(new RandomDataProducer().declareModel());
		
		EventSchema offeredSchema =  offeredStream.getEventSchema();
		EventSchema requiredSchema = requiredSepa.getEventStreams().get(0).getEventSchema();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
		assertTrue(matches);
	}
}
