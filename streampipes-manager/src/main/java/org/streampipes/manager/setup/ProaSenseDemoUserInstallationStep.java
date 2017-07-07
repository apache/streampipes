package org.streampipes.manager.setup;

import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.user.Role;
import org.streampipes.model.client.user.User;
import org.streampipes.storage.controller.StorageManager;

import java.util.*;

/**
 * Created by riemer on 04.11.2016.
 */
public class ProaSenseDemoUserInstallationStep implements InstallationStep {

    private static final String EMAIL_DOMAIN = "@proasense.eu";
    private static final String PWD = "1234";
    @Override
    public List<Message> install() {

        List<Message> responseMessages = new ArrayList<>();
        for(int i = 1; i <= 10; i++) {
            installUser("user" +i, Role.BUSINESS_ANALYST);
            responseMessages.add(Notifications.success("Creating user acoounts (Business Analyst)..."));
        }

        for(int i = 11; i <= 20; i++) {
            installUser("user" +i, Role.OPERATOR);
            responseMessages.add(Notifications.success("Creating user acoounts (Operator)..."));
        }

        return responseMessages;
    }

    private void installUser(String username, Role role) {
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        StorageManager.INSTANCE.getUserStorageAPI().storeUser(makeUser(username, roles));
    }

    private User makeUser(String username, Set<Role> roles) {
        return new User(username, username +EMAIL_DOMAIN, PWD, roles);
    }
}
