package org.streampipes.storage.sparql;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.repository.RepositoryException;

import org.streampipes.model.client.ontology.ElementHeader;
import org.streampipes.model.client.ontology.PrimitiveRange;
import org.streampipes.model.client.ontology.Property;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.storage.controller.StorageManager;

public class TestPropertyDetails {

	public static void main(String[] args) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		//StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getProperty("http://sepa.event-processing.org/sepa#produces");
		//StorageManager.INSTANCE.getBackgroundKnowledgeStorage().deleteNamespace("test");
		//StorageManager.INSTANCE.getBackgroundKnowledgeStorage().updateProperty(new Property(new ElementHeader("http://test.org/test#test3", null), "label3", "description3", new PrimitiveRange(XSD._integer.toString())));
		StorageManager.INSTANCE.getBackgroundKnowledgeStorage().updateProperty(new Property(new ElementHeader("http://test.org/test#test2", null), "label5", "description5", new PrimitiveRange(XSD._integer.toString())));
		
	}
}
