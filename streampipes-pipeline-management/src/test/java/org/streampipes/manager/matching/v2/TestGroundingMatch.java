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
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.ArrayList;
import java.util.List;

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
		
//		RandomDataProducer producer = new RandomDataProducer();
//		SpDataStream offer = producer.getEventStreams().get(0).declareModel(producer.declareModel());
//
//		DataProcessorDescription requirement = (new AggregationController().declareModel());
//
//		List<MatchingResultMessage> errorLog = new ArrayList<>();
//		boolean match = new GroundingMatch().match(offer.getEventGrounding(), requirement.getSupportedGrounding(), errorLog);
		
		assertTrue(true);
		
	}
}
