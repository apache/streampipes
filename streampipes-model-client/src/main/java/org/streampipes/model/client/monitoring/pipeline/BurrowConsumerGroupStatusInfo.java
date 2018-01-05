package org.streampipes.model.client.monitoring.pipeline;

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
