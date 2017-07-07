package org.streampipes.manager.matching.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.pe.algorithms.esper.aggregate.avg.AggregationController;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;

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
