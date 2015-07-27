package de.fzi.cep.sepa.manager.matching;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.storage.controller.StorageManager;
import junit.framework.Assert;

public class QualityMatcherTest {

	@Test
	public void test() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		String randomnumber = "http://localhost:8089/twitter/sample";
		String count = "http://localhost:8090f1e71a34-9fdb-44d6-ab98-9714aa83d0ed"; 
		String timestamp = "http://localhost:8090/7f5f49c9-0f34-4d4e-a3d8-95338c0c813a";
		
//		Assert.assertTrue(new QualityMatcher().matchesStreamQuality(randomnumber, count));
//		Assert.assertTrue(new QualityMatcher().matchesStreamQuality(randomnumber, timestamp));
	}

}
