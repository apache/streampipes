package org.streampipes.model.client.monitoring.pipeline;

public abstract class AbstractBurrowInfo {

    protected String error;
    protected String message;

    public AbstractBurrowInfo(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public AbstractBurrowInfo() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
