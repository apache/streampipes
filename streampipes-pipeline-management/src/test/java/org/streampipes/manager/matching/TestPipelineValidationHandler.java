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

package org.streampipes.manager.matching;

import junit.framework.TestCase;
import org.junit.Test;

//import static org.assertj.core.api.Assertions.assertThat;

public class TestPipelineValidationHandler extends TestCase {

	@Test
	public void testPositivePipelineValidation() {

//		Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(),
//				new RandomNumberStreamJson(),
//				new AggregationController());
//
//		PipelineVerificationHandler handler;
//		try {
//			handler = new PipelineVerificationHandler(pipeline);
//			handler.validateConnection();
//		} catch (Exception e2) {
//			fail(e2.getMessage());
//		}
//
//		assertTrue(true);
	}

	@Test
	public void testNegativePipelineValidation() {

//		Pipeline pipeline = TestUtils.makePipeline(new RandomDataProducer(),
//				new RandomNumberStreamJson(),
//				new GeofencingController());
//
//		PipelineVerificationHandler handler = null;
//
//
//		try {
//			handler = new PipelineVerificationHandler(pipeline);
//		} catch (Exception e) {
//			assertTrue(false);
//		}
//
//		Throwable actual = ThrowableCaptor.captureThrowable(handler::validateConnection);

		//assertThat(actual).isInstanceOf(InvalidConnectionException.class);

	}
}
