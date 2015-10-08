package de.fzi.cep.sepa.storage.impl;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.api.SepaInvocationStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class SepaInvocationStorageImpl implements SepaInvocationStorage {
	Logger LOG = LoggerFactory.getLogger(PipelineStorageImpl.class);
   
	@Override
	public String storeSepaInvocation(SepaInvocation sepaInvocation) {
		CouchDbClient dbClient = Utils.getCouchDbSepaInvocationClient();
        Response res = dbClient.save(sepaInvocation);

        dbClient.shutdown();
        
        return res.getId();
	}

	@Override
	public SepaInvocation getSepaInvovation(String sepaInvocationId) {
		CouchDbClient dbClient = Utils.getCouchDbSepaInvocationClient();

        try {
        	SepaInvocation sepaInvocation = dbClient.find(SepaInvocation.class, sepaInvocationId);
            dbClient.shutdown();
            return sepaInvocation;
        } catch (NoDocumentException e) {
			LOG.error("No invocation wit ID %s found", sepaInvocationId);
            return null;
        }
	}

}
