package de.fzi.cep.sepa.manager.matching.v2;

import java.util.List;

import de.fzi.cep.sepa.manager.matching.v2.utils.MatchingUtils;
import de.fzi.cep.sepa.model.client.matching.MatchingResultMessage;
import de.fzi.cep.sepa.model.client.matching.MatchingResultType;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

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
