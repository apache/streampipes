package de.fzi.cep.sepa.manager.matching;

import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.sparql.QueryExecutor;

public class QualityMatcher {

	public static final String SEPA = "http://sepa.event-processing.org/sepa#";

	public boolean matches(EventStream left, EventStream right) {
		boolean matches = true;

		// first check stream qualities
		if (matchesStreamQuality(left.getElementId(), right.getElementId())) {
			// then check property qualities
			if (!matchesPropertyQualities(left, right)) {
				matches = false;
			}
		
		} else {
			matches = false;
		}

		return matches;

	}

	public boolean matchesStreamQuality(String left, String right) {
		boolean matches = true;

		String query = queryStreamQuality(left, right);
		QueryExecutor qe = new QueryExecutor(StorageManager.INSTANCE.getConnection(), query);
		TupleQueryResult te;
		try {
			te = qe.execute();

			int i = 0;
			while (te.hasNext()) {
				BindingSet b = te.next();
				System.out.println(
						"left: " + b.getValue("left").toString() + " right: " + b.getValue("right").toString());
				i++;
			}
			if (i != 1) {
				matches = false;
			}

		} catch (QueryEvaluationException | RepositoryException | MalformedQueryException e) {
			e.printStackTrace();
			matches = false;
		}

		return matches;

	}

	public boolean matchesPropertyQualities(EventStream left, EventStream right) {
		boolean matches = true;

		// TODO add handling for list properties and nested properties 
		for (EventProperty rightProperty : right.getEventSchema().getEventProperties()) {

			for (EventProperty leftProperty : left.getEventSchema().getEventProperties()) {
				// TODO fix the get(0) in this line (this can cause a runtime error)
				if (rightProperty.getSubClassOf().get(0).equals(leftProperty.getSubClassOf())) {
					for (EventPropertyQualityRequirement rightPropertyQualityRequirement : rightProperty
							.getRequiresEventPropertyQualities()) {
						boolean tmp = false;
						for (EventPropertyQualityDefinition leftPropertyQualityDefinition : leftProperty
								.getEventPropertyQualities()) {
							if (rightPropertyQualityRequirement.fulfilled(leftPropertyQualityDefinition)) {
								tmp = true;
							}
						}
						
						if (!tmp) {
							matches = false;
						}
					}
				}
			}
		}

		return matches;
	}

	private static String getPrefix() {
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX sepa:<http://sepa.event-processing.org/sepa#>\n")
				.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");

		return builder.toString();
	}

	private String queryStreamQuality(String left, String right) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix());
		builder.append(query);
		builder.append(filterLeft(left));
		builder.append(filterRight(right));
		builder.append(queryEnding);

		return builder.toString();

	}

	private String query() {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix());
		builder.append(query);
		builder.append(queryEnding);

		return builder.toString();
	}

	private String filterLeft(String left) {
		return "FILTER ( ?left = <" + left + ">) \n";
	}

	private String filterRight(String right) {
		return "FILTER ( ?right = <" + right + ">) \n";
	}

	private static String query = "SELECT DISTINCT  ?left ?right WHERE { \n" + "   ?left rdf:type sepa:EventStream .\n"
			+ "   ?rightStream rdf:type sepa:EventStream .\n"
			+ "   ?left sepa:hasEventStreamQualityDefinition ?leftQuality .\n"
			+ "   ?right sepa:requiresEventStreamQuality ?rightQuality . \n"
			+ "   ?leftQuality sepa:hasQuantityValue ?leftValue .\n" + "   ?leftQuality rdf:type ?leftType .\n" + "\n"
			+ "\n" + "   OPTIONAL { \n" + "      ?rightQuality sepa:maximumEventStreamQuality ?maxQuality .\n"
			+ "      ?maxQuality sepa:hasQuantityValue ?maxValue .\n" + "      ?maxQuality rdf:type ?maxType .\n"
			+ "      FILTER ( ?maxType = ?leftType )\n" + "   }\n" + "   \n" + "   OPTIONAL { \n"
			+ "      ?rightQuality sepa:minimumEventStreamQuality ?minQuality .\n"
			+ "      ?minQuality sepa:hasQuantityValue ?minValue .\n" + "      ?minQuality rdf:type ?minType .\n"
			+ "      FILTER ( ?minType = ?leftType )\n" + "   }\n" + "   \n" + "   FILTER (\n"
			+ "      bound(?minQuality) && bound(?maxQuality) && ?maxValue >= ?leftValue && ?minValue <= ?leftValue\n"
			+ "      || bound(?minQuality) && !bound(?maxQuality) && ?minValue <= ?leftValue\n"
			+ "      || !bound(?minQuality) && bound(?maxQuality) && ?maxValue >= ?leftValue\n" + "   )\n";

	private static String queryEnding = "}";

}

// private boolean matchesStreamQuality(EventStream left, EventStream right) {
// List<EventStreamQualityRequirement> requires =
// right.getRequiresEventStreamQualities();
//
// if (requires == null) {
// return true;
// } else {
// List<EventStreamQualityDefinition> provides =
// left.getHasEventStreamQualities();
//
// for (EventStreamQualityRequirement er : requires) {
// er.getMaximumStreamQuality()
// }
// }
//
// return false;
// }
