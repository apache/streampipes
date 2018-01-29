package org.streampipes.storage.couchdb.impl;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Storage<T> {

    private Class<T> targetClass;

    public Storage(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public Optional<T> getItem(String key) {
        try {
            CouchDbClient dbClient = getCouchDbClient();
            T result = dbClient.find(targetClass, key);
            dbClient.shutdown();
            return Optional.of(result);
        } catch (NoDocumentException e) {
            return Optional.empty();
        }
    }

    public List<T> getAll() {
        CouchDbClient dbClient = getCouchDbClient();
        List<T> allResults = dbClient.view("_all_docs")
                .includeDocs(true)
                .query(targetClass);
        dbClient.shutdown();

        if (allResults != null)
            return allResults;
        else {
            return Collections.emptyList();
        }
    }

    public boolean add(T item) {
        CouchDbClient dbClient = getCouchDbClient();
        Response response = dbClient.save(item);
        dbClient.shutdown();
        if (response.getError() != null)
            return false;
        return true;
    }


    public boolean delete(String key) {
        try {
            CouchDbClient dbClient = getCouchDbClient();
            T result = dbClient.find(targetClass, key);
            dbClient.remove(result);
            dbClient.shutdown();
            return true;
        } catch (NoDocumentException e) {
            return false;
        }
    }

    public boolean update(T item) {
        try {
            CouchDbClient dbClient = getCouchDbClient();
            dbClient.update(item);
            dbClient.shutdown();
            return true;
        } catch (NoDocumentException e) {
            return false;
        }
    }

    public T getWithNullIfEmpty(String key) {
        Optional<T> resultOpt = getItem(key);
        if (resultOpt.isPresent()) {
            return resultOpt.get();
        } else {
            return null;
        }
    }

    protected abstract CouchDbClient getCouchDbClient();

}
