/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.matching.v2;

import junit.framework.TestCase;
import org.junit.Test;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.random.RandomNumberStreamJson;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.Geo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSchemaMatch extends TestCase {

	@Test
	public void testPositiveSchemaMatch() {

		EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lng);
		
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

		EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lng);
		
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

		EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lng);
		
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

		EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lat);
		EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.lng);
		
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

		DataProcessorDescription requiredSepa = new AggregationController().declareModel();
		SpDataStream offeredStream = new RandomNumberStreamJson().declareModel(new RandomDataProducer().declareModel());
		
		EventSchema offeredSchema =  offeredStream.getEventSchema();
		EventSchema requiredSchema = requiredSepa.getSpDataStreams().get(0).getEventSchema();
		
		List<MatchingResultMessage> errorLog = new ArrayList<>();
		
		boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
		assertTrue(matches);
	}
}
