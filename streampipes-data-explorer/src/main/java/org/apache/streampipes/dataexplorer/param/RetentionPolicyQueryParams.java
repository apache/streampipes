package org.apache.streampipes.dataexplorer.param;

public class RetentionPolicyQueryParams extends QueryParams {
    private final String durationLiteral;

    public static RetentionPolicyQueryParams from(String index, String durationLiteral) {
        return new RetentionPolicyQueryParams(index, durationLiteral);
    }

    protected RetentionPolicyQueryParams(String index, String durationLiteral) {
        super(index);
        this.durationLiteral = durationLiteral;
    }

    public String getDurationLiteral() {
        return durationLiteral;
    }


}
