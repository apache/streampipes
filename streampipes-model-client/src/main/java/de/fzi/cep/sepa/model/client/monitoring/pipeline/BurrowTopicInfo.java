package de.fzi.cep.sepa.model.client.monitoring.pipeline;

/**
 * Created by riemer on 06.12.2016.
 */
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
