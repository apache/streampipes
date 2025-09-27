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

package org.apache.streampipes.commons.prometheus.SpStateLocker;

import io.prometheus.client.Gauge;
import org.apache.streampipes.commons.prometheus.core.PrometheusMetrics;

/**
 * SpStateLocker指标管理器
 * 继承统一的指标管理器，消除重复代码
 */
public class SpStateLockerMetrics extends PrometheusMetrics {
    
    // 指标名称常量
    private static final String SP_LOCK_ACQUIRED_COUNT = "sp_lock_acquired_count";
    private static final String SP_LOCK_TIMEOUT = "sp_lock_timeout";
    private static final String SP_LOCK_WAIT_SECONDS = "sp_lock_wait_seconds";
    private static final String SP_LOCK_HOLD_SECONDS = "sp_lock_hold_seconds";
    private static final String SP_LOCK_QUEUE_LENGTH = "sp_lock_queue_length";
    
    public SpStateLockerMetrics(String id) {
        super(id);
    }
    
    @Override
    protected void registerGauges() {
        registerGauge(SP_LOCK_ACQUIRED_COUNT, "The number of times the lock has been acquired");
        registerGauge(SP_LOCK_TIMEOUT, "The timeout duration for acquiring the lock in seconds");
        registerGauge(SP_LOCK_WAIT_SECONDS, "The time in seconds the lock has been waited for");
        registerGauge(SP_LOCK_HOLD_SECONDS, "The time in seconds the lock has been held");
        registerGauge(SP_LOCK_QUEUE_LENGTH, "The number of threads waiting for the lock");
    }
    
    /**
     * 更新锁获取计数指标
     * @param count 计数
     */
    public void updateLockAcquiredCount(double count) {
        setGaugeValue(SP_LOCK_ACQUIRED_COUNT, count);
    }
    
    /**
     * 更新锁超时指标
     * @param timeout 超时时间
     */
    public void updateLockTimeout(double timeout) {
        setGaugeValue(SP_LOCK_TIMEOUT, timeout);
    }
    
    /**
     * 更新锁等待时间指标
     * @param waitSeconds 等待时间
     */
    public void updateLockWaitSeconds(double waitSeconds) {
        setGaugeValue(SP_LOCK_WAIT_SECONDS, waitSeconds);
    }
    
    /**
     * 更新锁持有时间指标
     * @param holdSeconds 持有时间
     */
    public void updateLockHoldSeconds(double holdSeconds) {
        setGaugeValue(SP_LOCK_HOLD_SECONDS, holdSeconds);
    }
    
    /**
     * 更新锁队列长度指标
     * @param queueLength 队列长度
     */
    public void updateLockQueueLength(double queueLength) {
        setGaugeValue(SP_LOCK_QUEUE_LENGTH, queueLength);
    }
    
    /**
     * 更新所有指标
     * @param acquiredCount 获取计数
     * @param timeout 超时时间
     * @param waitSeconds 等待时间
     * @param holdSeconds 持有时间
     * @param queueLength 队列长度
     */
    public void updateAllMetrics(double acquiredCount, double timeout, double waitSeconds, double holdSeconds, double queueLength) {
        updateLockAcquiredCount(acquiredCount);
        updateLockTimeout(timeout);
        updateLockWaitSeconds(waitSeconds);
        updateLockHoldSeconds(holdSeconds);
        updateLockQueueLength(queueLength);
    }
    
    // Getters for backward compatibility
    public Gauge getSpLockAcquiredCountGauge() {
        return getGauge(SP_LOCK_ACQUIRED_COUNT);
    }
    
    public Gauge getSpLockTimeoutGauge() {
        return getGauge(SP_LOCK_TIMEOUT);
    }
    
    public Gauge getSpLockWaitSecondsGauge() {
        return getGauge(SP_LOCK_WAIT_SECONDS);
    }
    
    public Gauge getSpLockHoldSecondsGauge() {
        return getGauge(SP_LOCK_HOLD_SECONDS);
    }
    
    public Gauge getSpLockQueueLengthGauge() {
        return getGauge(SP_LOCK_QUEUE_LENGTH);
    }
}
