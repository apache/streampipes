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

import org.apache.streampipes.commons.prometheus.core.PrometheusStats;

/**
 * SpStateLocker统计信息管理器
 * 继承统一的统计信息管理器，消除重复代码
 */
public class SpStateLockerStats extends PrometheusStats<SpStateLockerMetrics> {
    
    public double lockAcquiredCount = 0.0;
    public double lockTimeout = 0.0;
    public double lockWaitSeconds = 0.0;
    public double lockHoldSeconds = 0.0;
    public double lockQueueLength = 0.0;

    public SpStateLockerStats(String id) {
        super(id);
    }
    
    @Override
    protected SpStateLockerMetrics createMetrics() {
        return new SpStateLockerMetrics(id);
    }
    
    @Override
    protected void updateMetrics() {
        metrics.updateAllMetrics(lockAcquiredCount, lockTimeout, lockWaitSeconds, lockHoldSeconds, lockQueueLength);
    }
    
    /**
     * 更新锁获取计数
     * @param count 计数
     */
    public void setLockAcquiredCount(double count) {
        this.lockAcquiredCount = count;
        metrics.updateLockAcquiredCount(count);
    }
    
    /**
     * 更新锁超时
     * @param timeout 超时时间
     */
    public void setLockTimeout(double timeout) {
        this.lockTimeout = timeout;
        metrics.updateLockTimeout(timeout);
    }
    
    /**
     * 更新锁等待时间
     * @param waitSeconds 等待时间
     */
    public void setLockWaitSeconds(double waitSeconds) {
        this.lockWaitSeconds = waitSeconds;
        metrics.updateLockWaitSeconds(waitSeconds);
    }
    
    /**
     * 更新锁持有时间
     * @param holdSeconds 持有时间
     */
    public void setLockHoldSeconds(double holdSeconds) {
        this.lockHoldSeconds = holdSeconds;
        metrics.updateLockHoldSeconds(holdSeconds);
    }
    
    /**
     * 更新锁队列长度
     * @param queueLength 队列长度
     */
    public void setLockQueueLength(double queueLength) {
        this.lockQueueLength = queueLength;
        metrics.updateLockQueueLength(queueLength);
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
        this.lockAcquiredCount = acquiredCount;
        this.lockTimeout = timeout;
        this.lockWaitSeconds = waitSeconds;
        this.lockHoldSeconds = holdSeconds;
        this.lockQueueLength = queueLength;
        metrics.updateAllMetrics(acquiredCount, timeout, waitSeconds, holdSeconds, queueLength);
    }
    
    // Getters
    public double getLockAcquiredCount() {
        return lockAcquiredCount;
    }
    
    public double getLockTimeout() {
        return lockTimeout;
    }
    
    public double getLockWaitSeconds() {
        return lockWaitSeconds;
    }
    
    public double getLockHoldSeconds() {
        return lockHoldSeconds;
    }
    
    public double getLockQueueLength() {
        return lockQueueLength;
    }
    
    /**
     * 获取SpStateLocker统计信息（向后兼容）
     * @param id 统计信息ID
     * @return 统计信息
     */
    @SuppressWarnings("unchecked")
    public static SpStateLockerStats get(String id) {
        return (SpStateLockerStats) PrometheusStats.getStats(id);
    }
    
    /**
     * 更新所有指标（向后兼容）
     */
    public static void metrics() {
        PrometheusStats.updateAllMetrics();
    }
}
