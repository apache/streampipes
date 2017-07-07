package de.fzi.cep.sepa.storage.sparql;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.ElementHeader;
import de.fzi.cep.sepa.model.client.ontology.PrimitiveRange;
import de.fzi.cep.sepa.model.client.ontology.Property;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestPropertyDetails {

	public static void main(String[] args) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		//StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getProperty("http://sepa.event-processing.org/sepa#produces");
		//StorageManager.INSTANCE.getBackgroundKnowledgeStorage().deleteNamespace("test");
		//StorageManager.INSTANCE.getBackgroundKnowledgeStorage().updateProperty(new Property(new ElementHeader("http://test.org/test#test3", null), "label3", "description3", new PrimitiveRange(XSD._integer.toString())));
		StorageManager.INSTANCE.getBackgroundKnowledgeStorage().updateProperty(new Property(new ElementHeader("http://test.org/test#test2", null), "label5", "description5", new PrimitiveRange(XSD._integer.toString())));
		
	}
}
