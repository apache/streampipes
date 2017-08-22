package org.streampipes.model.client.monitoring.pipeline;

/**
 * Created by riemer on 06.12.2016.
 */
public class BurrowConsumerGroupStatusInfo extends AbstractBurrowInfo {

    private BurrowConsumerGroupStatus status;

    public BurrowConsumerGroupStatusInfo(String error, String message) {
        super(error, message);
    }

    public BurrowConsumerGroupStatusInfo() {
    }

    public BurrowConsumerGroupStatus getStatus() {
        return status;
    }

    public void setStatus(BurrowConsumerGroupStatus status) {
        this.status = status;
    }
}
