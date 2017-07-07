package de.fzi.cep.sepa.manager.matching.v2.utils;

public class MatchingUtils {

	public static boolean nullCheck(Object offer, Object requirement) {
		return (offer == null) || (requirement == null);
	}
	
	public static boolean nullCheckRightNullDisallowed(Object offer, Object requirement) {
		return offer == null && requirement != null;
	}
	
	public static boolean nullCheckBothNullDisallowed(Object offer, Object requirement) {
		return offer != null && requirement != null;
	}
	
	public static boolean nullCheckReqAllowed(Object offer, Object requirement) {
		return requirement == null;
	}
}
