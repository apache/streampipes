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
import org.streampipes.model.grounding.TransportProtocol;

public class ProtocolMatch extends AbstractMatcher<TransportProtocol, TransportProtocol>{

	public ProtocolMatch() {
		super(MatchingResultType.PROTOCOL_MATCH);
	}

	@Override
	public boolean match(TransportProtocol offer, TransportProtocol requirement, List<MatchingResultMessage> errorLog) {
		
		boolean matches = MatchingUtils.nullCheck(offer, requirement) ||
				canonicalName(requirement).equals(canonicalName(offer));
		
		if (!matches) buildErrorMessage(errorLog, "protocol");
		return matches;
	}
	
	private String canonicalName(TransportProtocol protocol) {
		return protocol.getClass().getCanonicalName();
	}

}
