package de.fzi.cep.sepa.manager.setup;

import de.fzi.cep.sepa.model.client.messages.Message;
import de.fzi.cep.sepa.model.client.messages.Notifications;
import de.fzi.cep.sepa.model.client.user.Role;
import de.fzi.cep.sepa.model.client.user.User;
import de.fzi.cep.sepa.storage.controller.StorageManager;

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
