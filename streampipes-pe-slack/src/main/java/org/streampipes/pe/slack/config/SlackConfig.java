package org.streampipes.pe.slack.config;

import org.streampipes.commons.config.ClientConfiguration;

public class SlackConfig {
    public final static String serverUrl;
    public final static String iconBaseUrl;

    static {
        // TODO
        serverUrl = ClientConfiguration.INSTANCE.getActionUrl();
        iconBaseUrl = ClientConfiguration.INSTANCE.getIconUrl() +"/img";
    }

}
