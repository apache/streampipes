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

package org.apache.streampipes.model.loadbalancer;

import java.time.LocalDateTime;

/**
 * Information about a pipeline for load balancing operations.
 */
public class PipelineInfo {

  private String lockKey;
  private PipelineStates pipelineState;
  private Thread currentThread;
  private LocalDateTime lockTime;
  private Integer waitCount;

  /**
   * Creates a new PipelineInfo instance.
   *
   * @param lockKey the lock key
   * @param pipelineState the pipeline state
   * @param currentThread the current thread
   * @param lockTime the lock time
   * @param waitCount the wait count
   */
  public PipelineInfo(String lockKey, PipelineStates pipelineState, Thread currentThread, LocalDateTime lockTime, Integer waitCount) {
    this.lockKey = lockKey;
    this.pipelineState = pipelineState;
    this.currentThread = currentThread;
    this.lockTime = lockTime;
    this.waitCount = waitCount;
  }

  /**
   * Gets the lock key.
   *
   * @return the lock key
   */
  public String getLockKey() {
    return lockKey;
  }

  /**
   * Sets the lock key.
   *
   * @param lockKey the lock key
   */
  public void setLockKey(String lockKey) {
    this.lockKey = lockKey;
  }

  /**
   * Gets the lock time.
   *
   * @return the lock time
   */
  public LocalDateTime getLockTime() {
    return lockTime;
  }

  /**
   * Sets the lock time.
   *
   * @param lockTime the lock time
   */
  public void setLockTime(LocalDateTime lockTime) {
    this.lockTime = lockTime;
  }

  /**
   * Gets the pipeline state.
   *
   * @return the pipeline state
   */
  public PipelineStates getPipelineState() {
    return pipelineState;
  }

  /**
   * Sets the pipeline state.
   *
   * @param pipelineState the pipeline state
   */
  public void setPipelineState(PipelineStates pipelineState) {
    this.pipelineState = pipelineState;
  }

  /**
   * Gets the current thread.
   *
   * @return the current thread
   */
  public Thread getCurrentThread() {
    return currentThread;
  }

  /**
   * Sets the current thread.
   *
   * @param currentThread the current thread
   */
  public void setCurrentThread(Thread currentThread) {
    this.currentThread = currentThread;
  }

  /**
   * Gets the wait count.
   *
   * @return the wait count
   */
  public Integer getWaitCount() {
    return waitCount;
  }

  /**
   * Sets the wait count.
   *
   * @param waitCount the wait count
   */
  public void setWaitCount(Integer waitCount) {
    this.waitCount = waitCount;
  }
}
