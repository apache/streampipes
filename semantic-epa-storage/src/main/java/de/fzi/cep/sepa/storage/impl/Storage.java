package de.fzi.cep.sepa.storage.impl;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by riemer on 30.08.2016.
 */
public abstract class Storage<T> {

    protected CouchDbClient dbClient;
    private Class<T> targetClass;

    public Storage(CouchDbClient dbClient, Class<T> targetClass) {
        this.dbClient = dbClient;
        this.targetClass = targetClass;
    }

    public Optional<T> getItem(String key) {
        try {
            T result = dbClient.find(targetClass, key);
            dbClient.shutdown();
            return Optional.of(result);
        } catch (NoDocumentException e) {
            return Optional.empty();
        }
    }

    public List<T> getAll() {
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
        Response response = dbClient.save(item);
        dbClient.shutdown();
        if (response.getError() != null)
            return false;
        return true;
    }


    public boolean delete(String key) {
        try {
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
}
