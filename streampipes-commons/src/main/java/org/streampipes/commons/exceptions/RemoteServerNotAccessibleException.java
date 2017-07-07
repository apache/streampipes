package org.streampipes.commons.exceptions;

public class RemoteServerNotAccessibleException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String serverUrl;

    public RemoteServerNotAccessibleException(String message, String serverUrl) {
        super(message);
        this.serverUrl = serverUrl;
    }

    public RemoteServerNotAccessibleException(RemoteServerNotAccessibleException e) {
        super(e.getMessage());
        this.serverUrl = e.getServerUrl();
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
