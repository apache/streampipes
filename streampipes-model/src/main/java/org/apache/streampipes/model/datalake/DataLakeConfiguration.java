package org.apache.streampipes.model.datalake;

import java.util.ArrayList;
import java.util.List;

public class DataLakeConfiguration {

    private Integer batchSize;
    private Integer flushDuration;

    private List<DataLakeRetentionPolicy> retentionPolicies;

    public DataLakeConfiguration() {
        this.batchSize = 2000;
        this.flushDuration = 500;
        this.retentionPolicies = new ArrayList<DataLakeRetentionPolicy>();
    }

    public DataLakeConfiguration(List<DataLakeRetentionPolicy> retentionPolicies) {
        this.batchSize = 2000;
        this.flushDuration = 500;
        this.retentionPolicies = retentionPolicies;
    }

    public DataLakeConfiguration(Integer batchSize, Integer flushDuration) {
        this.batchSize = batchSize;
        this.flushDuration = flushDuration;
        this.retentionPolicies = new ArrayList<DataLakeRetentionPolicy>();
    }

    public DataLakeConfiguration(Integer batchSize, Integer flushDuration, List<DataLakeRetentionPolicy> retentionPolicies) {
        this.batchSize = batchSize;
        this.flushDuration = flushDuration;
        this.retentionPolicies = retentionPolicies;
    }

    public List<DataLakeRetentionPolicy> getRetentionPolicies() {
        return retentionPolicies;
    }

    public void setRetentionPolicies(List<DataLakeRetentionPolicy> retentionPolicies) {
        this.retentionPolicies = retentionPolicies;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getFlushDuration() {
        return flushDuration;
    }

    public void setFlushDuration(Integer flushDuration) {
        this.flushDuration = flushDuration;
    }
}
