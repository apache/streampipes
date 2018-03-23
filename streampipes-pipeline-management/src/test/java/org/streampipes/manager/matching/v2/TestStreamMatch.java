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
