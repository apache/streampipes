/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.client.monitoring.pipeline;

public class BurrowConsumerGroupStatus {

    private String cluster;
    private String group;
    private String status;
    private boolean complete;
    private String[] partitions;

    private Integer partition_count;
    private Integer maxlag;
    private Integer totallag;

    public BurrowConsumerGroupStatus() {

    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String[] getPartitions() {
        return partitions;
    }

    public void setPartitions(String[] partitions) {
        this.partitions = partitions;
    }

    public Integer getPartition_count() {
        return partition_count;
    }

    public void setPartition_count(Integer partition_count) {
        this.partition_count = partition_count;
    }

    public Integer getMaxlag() {
        return maxlag;
    }

    public void setMaxlag(Integer maxlag) {
        this.maxlag = maxlag;
    }

    public Integer getTotallag() {
        return totallag;
    }

    public void setTotallag(Integer totallag) {
        this.totallag = totallag;
    }
}
