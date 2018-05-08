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

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;

import java.net.URI;
import java.util.List;

public class DomainPropertyMatch extends AbstractMatcher<List<URI>, List<URI>> {

	public DomainPropertyMatch() {
		super(MatchingResultType.DOMAIN_PROPERTY_MATCH);
	}

	@Override
	public boolean match(List<URI> offer, List<URI> requirement, List<MatchingResultMessage> errorLog) {
		if (offer == null && ((requirement != null) && requirement.size() > 0)) {
			return false;
		}
		boolean match = MatchingUtils.nullCheck(offer, requirement) ||
				requirement
				.stream()
				.allMatch(req -> offer
						.stream()
						.anyMatch(of -> req
								.toString()
								.equals(of.toString())));
		
		if (!match) buildErrorMessage(errorLog, buildText(requirement));
		return match;
	}

	private String buildText(List<URI> requirement) {
		if (requirement == null || requirement.size() == 0) return "-";
		else return "Required domain property: " +requirement.get(0).toString();
	}

}
