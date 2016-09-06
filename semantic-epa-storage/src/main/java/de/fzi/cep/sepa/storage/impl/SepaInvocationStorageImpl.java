package de.fzi.cep.sepa.storage.impl;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.api.SepaInvocationStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class SepaInvocationStorageImpl extends Storage<SepaInvocation> implements SepaInvocationStorage {
    Logger LOG = LoggerFactory.getLogger(PipelineStorageImpl.class);

    public SepaInvocationStorageImpl() {
        super(SepaInvocation.class);
    }

    @Override
    public Response storeSepaInvocation(SepaInvocation sepaInvocation) {
        CouchDbClient dbClient = getCouchDbClient();
        Response response = dbClient.save(sepaInvocation);
        dbClient.shutdown();
        return response;
    }

    @Override
    public SepaInvocation getSepaInvovation(String sepaInvocationId) {
        // TODO return optional instead of null
        return getWithNullIfEmpty(sepaInvocationId);
    }

    @Override
    public boolean removeSepaInvovation(String sepaInvocationId, String sepaInvocationRev) {
        try {
            CouchDbClient dbClient = getCouchDbClient();
            dbClient.remove(sepaInvocationId, sepaInvocationRev);
            dbClient.shutdown();
            return true;
        } catch (NoDocumentException e) {
            return false;
        }
    }

    @Override
    protected CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbSepaInvocationClient();
    }
}
