/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.client.monitoring.pipeline;

public class BurrowConsumerGroupStatus {

  private String cluster;
  private String group;
  private String status;
  private boolean complete;
  private String[] partitions;

  private Integer partitionCount;
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

  public Integer getPartitionCount() {
    return partitionCount;
  }

  public void setPartitionCount(Integer partitionCount) {
    this.partitionCount = partitionCount;
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
