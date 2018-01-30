package org.streampipes.storage.couchdb.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.client.user.User;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Storage.
 * Handles operations on user including user-specified pipelines.
 *
 *
 */
public class UserStorage extends AbstractDao<User> {

    Logger LOG = LoggerFactory.getLogger(UserStorage.class);

    public UserStorage() {
        super(Utils.getCouchDbUserClient(), User.class);
    }
    
    public List<User> getAllUsers()
    {
        List<User> users = findAll();
    	return users.stream().collect(Collectors.toList());
    }

    public User getUser(String email) {
        // TODO improve
        List<User> users = couchDbClient.view("users/username").key(email).includeDocs(true).query(User.class);
        if (users.size() != 1) {
            LOG.error("None or to many users with matching username");
        }
        return users.get(0);
    }

    public void storeUser(User user) {
        persist(user);
    }

    public void updateUser(User user) {
        update(user);
    }
    
    public boolean emailExists(String email)
    {
    	List<User> users = findAll();
    	return users
                .stream()
                .filter(u -> u.getEmail() != null)
                .anyMatch(u -> u.getEmail().equals(email));
    }

    /**
    *
    * @param username
    * @return True if user exists exactly once, false otherwise
    */
   public boolean checkUser(String username) {
       List<User> users = couchDbClient
               .view("users/username")
               .key(username)
               .includeDocs(true)
               .query(User.class);

       return users.size() == 1;
   }

}
