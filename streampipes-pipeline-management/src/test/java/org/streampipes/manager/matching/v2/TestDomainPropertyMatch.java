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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.vocabulary.Geo;

public class TestDomainPropertyMatch extends TestCase {

	@Test
	public void testPositiveDomainPropertyMatch() {

		List<URI> offeredDomainProperty = buildDomainProperties(Geo.lat);
		List<URI> requiredDomainProperty = buildDomainProperties(Geo.lat);
		
		List<MatchingResultMessage> resultMessage = new ArrayList<>();
		
		boolean matches = new DomainPropertyMatch().match(offeredDomainProperty, requiredDomainProperty, resultMessage);
		assertTrue(matches);
	}
	
	@Test
	public void testNegativeDomainPropertyMatch() {

		List<URI> offeredDomainProperty = buildDomainProperties(Geo.lat);
		List<URI> requiredDomainProperty = buildDomainProperties(Geo.lng);
		
		List<MatchingResultMessage> resultMessage = new ArrayList<>();
		
		boolean matches = new DomainPropertyMatch().match(offeredDomainProperty, requiredDomainProperty, resultMessage);
		assertFalse(matches);
	}
	
	private List<URI> buildDomainProperties(String name) {
		return Arrays.asList(URI.create(name));
	}
}
