package org.streampipes.model.client.monitoring.pipeline;

/**
 * Created by riemer on 06.12.2016.
 */
public class BurrowConsumerInfo extends AbstractBurrowInfo{

    private String[] consumers;

    public BurrowConsumerInfo(String error, String message, String[] consumers) {
        super(error, message);
        this.consumers = consumers;
    }

    public BurrowConsumerInfo(String[] consumers) {
        this.consumers = consumers;
    }

    public BurrowConsumerInfo() {

    }

    public String[] getConsumers() {
        return consumers;
    }

    public void setConsumers(String[] consumers) {
        this.consumers = consumers;
    }
}
