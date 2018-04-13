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

import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.schema.EventPropertyList;

import java.util.ArrayList;
import java.util.List;

public class ListPropertyMatch implements Matcher<EventPropertyList, EventPropertyList> {

	@Override
	public boolean match(EventPropertyList offer, EventPropertyList requirement, List<MatchingResultMessage> errorLog) {
		if (requirement.getEventProperties() == null) {
			return false;
		} else if (requirement.getEventProperties().size() == 0) {
			if (requirement.getDomainProperties() != null && offer.getDomainProperties() != null && requirement.getDomainProperties().size() > 0) {
				return new DomainPropertyMatch().match(offer.getDomainProperties(), requirement.getDomainProperties(), errorLog);
			} else {
				return false;
			}
		} else {
			return new PropertyMatch().match(offer.getEventProperties().get(0),
					requirement.getEventProperties().get(0), errorLog);
		}
	}
}
