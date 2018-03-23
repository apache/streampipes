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

import java.util.List;

import org.streampipes.manager.matching.v2.utils.MatchingUtils;
import org.streampipes.model.client.matching.MatchingResultMessage;
import org.streampipes.model.client.matching.MatchingResultType;
import org.streampipes.model.grounding.TransportFormat;

public class FormatMatch extends AbstractMatcher<TransportFormat, TransportFormat>{

	public FormatMatch() {
		super(MatchingResultType.FORMAT_MATCH);
	}

	@Override
	public boolean match(TransportFormat offer, TransportFormat requirement, List<MatchingResultMessage> errorLog) {
		return MatchingUtils.nullCheck(offer, requirement) ||
				requirement.getRdfType().containsAll(offer.getRdfType());
		
	}

}
