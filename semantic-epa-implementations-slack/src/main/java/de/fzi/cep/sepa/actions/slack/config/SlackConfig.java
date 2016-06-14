package de.fzi.cep.sepa.actions.slack.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class SlackConfig {
    public final static String serverUrl;
    public final static String iconBaseUrl;

    static {
        // TODO
        serverUrl = ClientConfiguration.INSTANCE.getActionUrl();
        iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/semantic-epa-backend/img";
    }

}
