package de.fzi.cep.sepa.storage.impl;

import de.fzi.cep.sepa.model.client.user.User;
import de.fzi.cep.sepa.storage.util.Utils;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Storage.
 * Handles operations on user including user-specified pipelines.
 *
 * Created by robin on 21.06.15.
 */
public class UserStorage {

    Logger LOG = LoggerFactory.getLogger(UserStorage.class);
    
    public List<User> getAllUsers()
    {
    	CouchDbClient dbClient = Utils.getCouchDbUserClient();
    	List<User> users = dbClient.view("_all_docs")
   			  .includeDocs(true)
   			  .query(User.class);
    	return users.stream().filter(u -> (u.getUsername() != null)).collect(Collectors.toList());
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
       //User user = stor.getUser("user");
       //user.deletePipeline("f0471513-7d17-468c-a2fc-8a32aa8b126d");
       //stor.updateUser(user);
       System.out.println(stor.getUser("riemer@fzi.de").getUsername());
   }

}
