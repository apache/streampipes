package org.streampipes.storage.impl;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.storage.api.SepaInvocationStorage;
import org.streampipes.storage.util.Utils;

public class SepaInvocationStorageImpl extends Storage<DataProcessorInvocation> implements SepaInvocationStorage {
    Logger LOG = LoggerFactory.getLogger(PipelineStorageImpl.class);

    public SepaInvocationStorageImpl() {
        super(DataProcessorInvocation.class);
    }

    @Override
    public Response storeSepaInvocation(DataProcessorInvocation dataProcessorInvocation) {
        CouchDbClient dbClient = getCouchDbClient();
        Response response = dbClient.save(dataProcessorInvocation);
        dbClient.shutdown();
        return response;
    }

    @Override
    public DataProcessorInvocation getSepaInvovation(String sepaInvocationId) {
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
