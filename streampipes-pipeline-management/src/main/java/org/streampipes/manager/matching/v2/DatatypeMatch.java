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
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

public class DatatypeMatch extends AbstractMatcher<String, String> {

	public DatatypeMatch() {
		super(MatchingResultType.DATATYPE_MATCH);
	}

	@Override
	public boolean match(String offer, String requirement, List<MatchingResultMessage> errorLog) {
		
		boolean match = MatchingUtils.nullCheckReqAllowed(offer, requirement)
				|| requirement.equals(offer) 
				|| subClassOf(offer, requirement) 
				|| MatchingUtils.nullCheck(offer, requirement);
		
		if (!match) buildErrorMessage(errorLog, requirement);
		return match;
	}
	
	private boolean subClassOf(String offer, String requirement) {
		if (!requirement.equals(SO.Number)) return false;
		else {
			if (offer.equals(XSD._integer.toString())
					|| offer.equals(XSD._long.toString()) 
					|| offer.equals(XSD._double.toString()) 
					|| offer.equals(XSD._float.toString())) 
				return true;
		}
		return false;
	}

}
