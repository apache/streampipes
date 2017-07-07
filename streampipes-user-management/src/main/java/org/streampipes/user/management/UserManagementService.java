package org.streampipes.user.management;

import org.streampipes.model.client.user.RegistrationData;
import org.streampipes.model.client.user.Role;
import org.streampipes.model.client.user.User;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.user.management.util.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

/**
 * Created by riemer on 07.07.2017.
 */
public class UserManagementService {

  public Boolean registerUser(RegistrationData data, Set<Role> roles) {
    org.streampipes.model.client.user.User user = new User(data
            .getUsername(), data.getEmail(), data.getPassword(), roles);

    try {
      String encryptedPassword = PasswordUtil.encryptPassword(data.getPassword());
      user.setPassword(encryptedPassword);
      StorageManager.INSTANCE.getUserStorageAPI().storeUser(user);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return false;
    }

    return true;
  }
}
