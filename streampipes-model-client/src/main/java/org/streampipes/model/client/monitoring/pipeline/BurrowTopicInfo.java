package org.streampipes.model.client.monitoring.pipeline;

public class BurrowTopicInfo extends AbstractBurrowInfo {

    private Long[] offsets;

    public BurrowTopicInfo(String error, String message, Long[] offsets) {
        super(error, message);
        this.offsets = offsets;
    }

    public BurrowTopicInfo() {
    }

    public Long[] getOffsets() {
        return offsets;
    }

    public void setOffsets(Long[] offsets) {
        this.offsets = offsets;
    }
}
