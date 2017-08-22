package org.streampipes.pe.slack.sec;

import com.ullink.slack.simpleslackapi.SlackSession;

import java.util.List;

public class SlackNotificationParameters {
    private String authToken;
    private boolean sendToUser;
    private String userChannel;
    private List<String> properties;
    private SlackSession session;

    public SlackNotificationParameters(String authToken, boolean sendToUser, String userChannel, List<String> properties, SlackSession session) {
        this.authToken = authToken;
        this.sendToUser = sendToUser;
        this.userChannel = userChannel;
        this.properties = properties;
        this.session = session;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isSendToUser() {
        return sendToUser;
    }

    public void setSendToUser(boolean sendToUser) {
        this.sendToUser = sendToUser;
    }

    public String getUserChannel() {
        return userChannel;
    }

    public void setUserChannel(String userChannel) {
        this.userChannel = userChannel;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public SlackSession getSession() {
        return session;
    }

    public void setSession(SlackSession session) {
        this.session = session;
    }
}

