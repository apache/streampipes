package de.fzi.cep.sepa.storage.impl;

import com.google.gson.JsonObject;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.user.User;
import de.fzi.cep.sepa.storage.util.Utils;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User Storage.
 * Handles operations on user including user-specified pipelines.
 *
 * Created by robin on 21.06.15.
 */
public class UserStorage {

    Logger LOG = LoggerFactory.getLogger(UserStorage.class);


    /**
     * Remove pipeline reference from user.
     * @param username
     * @param pipelineId
     */
    public void deletePipeline(String username, String pipelineId) {
        if (checkUser(username)) {
            User user = getUser(username);
            user.deletePipeline(pipelineId);
            updateUser(user);
        }
    }

    /**
     * Adding reference to user pipelines for given pipeline id.
     * @param pipelineId
     */
    public void addPipeline(String username, String pipelineId) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        if (!checkUser(username)) return;
        User user = getUser(username);
        user.addPipeline(pipelineId);
        dbClient.update(user);
        dbClient.shutdown();

    }

    public void addSource(String username, String elementId) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        if (!checkUser(username)) return;
        User user = getUser(username);
        user.addSource(elementId);
        dbClient.update(user);
        dbClient.shutdown();
    }

    public void addAction(String username, String elementId) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        if (!checkUser(username)) return;
        User user = getUser(username);
        user.addAction(elementId);
        dbClient.update(user);
        dbClient.shutdown();
    }

    public void addSepa(String username, String elementId) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        if (!checkUser(username)) return;
        User user = getUser(username);
        user.addSepa(elementId);
        dbClient.update(user);
        dbClient.shutdown();
    }

    public User getUser(String username) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        List<User> users = dbClient.view("users/username").key(username).includeDocs(true).query(User.class);
        if (users.size() != 1) LOG.error("None or to many users with matching username");
        dbClient.shutdown();
        return users.get(0);
    }

    public void storeUser(User user) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        dbClient.save(user);
        dbClient.shutdown();
    }

    public void updateUser(User user) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        dbClient.update(user);
        dbClient.shutdown();
    }

    /**
     *
     * @param username
     * @return True if user exists exactly once, false otherwise
     */
    public boolean checkUser(String username) {
        CouchDbClient dbClient = Utils.getCouchDbUserClient();
        List<User> users = dbClient.view("users/username").key(username).includeDocs(true).query(User.class);
        return users.size() == 1;
    }

    public static void main(String[] args) {
        UserStorage stor = new UserStorage();
        User user = stor.getUser("user");
        user.deletePipeline("f0471513-7d17-468c-a2fc-8a32aa8b126d");
        stor.updateUser(user);

    }

}
