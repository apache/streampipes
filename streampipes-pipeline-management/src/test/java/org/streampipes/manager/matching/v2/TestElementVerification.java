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

import org.streampipes.model.graph.DataProcessorInvocation;
import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;

public class TestElementVerification extends TestCase {

	@Test
	public void testPositive() {
		
		RandomDataProducer producer = new RandomDataProducer();
		SpDataStream offer = producer.getEventStreams().get(0).declareModel(producer.declareModel());
		
		DataProcessorDescription requirement = (new AggregationController().declareModel());
		
		ElementVerification verifier = new ElementVerification();
		boolean match = verifier.verify(offer, new DataProcessorInvocation(requirement));
		
		verifier.getErrorLog().forEach(e -> System.out.println(e.getTitle()));
		assertTrue(match);
		
	}
}
