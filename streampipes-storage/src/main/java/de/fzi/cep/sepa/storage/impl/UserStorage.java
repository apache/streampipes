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
public class UserStorage extends Storage<User> {

    Logger LOG = LoggerFactory.getLogger(UserStorage.class);

    public UserStorage() {
        super(User.class);
    }
    
    public List<User> getAllUsers()
    {
        List<User> users = getAll();
    	return users.stream().filter(u -> (u.getUsername() != null)).collect(Collectors.toList());
    }

    public User getUser(String username) {
        // TODO improve
        CouchDbClient dbClient = getCouchDbClient();
        List<User> users = dbClient.view("users/username").key(username).includeDocs(true).query(User.class);
        if (users.size() != 1) LOG.error("None or to many users with matching username");
        return users.get(0);
    }

    public void storeUser(User user) {
        add(user);
    }

    public void updateUser(User user) {
        update(user);
    }
    
    public boolean emailExists(String email)
    {
    	List<User> users = getAll();
    	return users
                .stream()
                .filter(u -> u.getEmail() != null)
                .anyMatch(u -> u.getEmail().equals(email));
    }
    
    public boolean usernameExists(String username)
    {
    	List<User> users = getAll();
    	return users
                .stream()
                .filter(u -> u.getUsername() != null)
                .anyMatch(u -> u.getUsername().equals(username));
    }
    
    /**
    *
    * @param username
    * @return True if user exists exactly once, false otherwise
    */
   public boolean checkUser(String username) {
       CouchDbClient dbClient = getCouchDbClient();
       List<User> users = dbClient.view("users/username").key(username).includeDocs(true).query(User.class);
       return users.size() == 1;
   }

    @Override
    protected CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbUserClient();
    }
}
