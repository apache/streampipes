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

package org.apache.streampipes.commons.prometheus.loadbalancer;

import org.apache.streampipes.commons.prometheus.core.PrometheusStats;

/**
 * 负载均衡器统计信息管理器
 * 继承统一的统计信息管理器，消除重复代码
 */
public class LoadBalancerStats extends PrometheusStats<LoadBalancerMetrics> {
    
    public double lbEvaluationDurationSeconds = 0;
    public double lbStddev = 0;
    public double lbImbalanceRatio = 0;

    public LoadBalancerStats(String id) {
        super(id);
    }
    
    @Override
    protected LoadBalancerMetrics createMetrics() {
        return new LoadBalancerMetrics(id);
    }
    
    @Override
    protected void updateMetrics() {
        metrics.updateAllMetrics(lbEvaluationDurationSeconds, lbStddev, lbImbalanceRatio);
    }
    
    /**
     * 更新评估持续时间
     * @param duration 持续时间
     */
    public void setLbEvaluationDurationSeconds(double duration) {
        this.lbEvaluationDurationSeconds = duration;
        metrics.updateEvaluationDuration(duration);
    }
    
    /**
     * 更新标准差
     * @param stddev 标准差
     */
    public void setLbStddev(double stddev) {
        this.lbStddev = stddev;
        metrics.updateStddev(stddev);
    }
    
    /**
     * 更新不平衡比率
     * @param ratio 不平衡比率
     */
    public void setLbImbalanceRatio(double ratio) {
        this.lbImbalanceRatio = ratio;
        metrics.updateImbalanceRatio(ratio);
    }
    
    /**
     * 更新所有指标
     * @param duration 持续时间
     * @param stddev 标准差
     * @param ratio 不平衡比率
     */
    public void updateAllMetrics(double duration, double stddev, double ratio) {
        this.lbEvaluationDurationSeconds = duration;
        this.lbStddev = stddev;
        this.lbImbalanceRatio = ratio;
        metrics.updateAllMetrics(duration, stddev, ratio);
    }
    
    // Getters
    public double getLbEvaluationDurationSeconds() {
        return lbEvaluationDurationSeconds;
    }
    
    public double getLbStddev() {
        return lbStddev;
    }
    
    public double getLbImbalanceRatio() {
        return lbImbalanceRatio;
    }
    
    /**
     * 获取负载均衡器统计信息（向后兼容）
     * @param id 统计信息ID
     * @return 统计信息
     */
    @SuppressWarnings("unchecked")
    public static LoadBalancerStats get(String id) {
        return (LoadBalancerStats) PrometheusStats.getStats(id);
    }
    
    /**
     * 更新所有指标（向后兼容）
     */
    public static void metrics() {
        PrometheusStats.updateAllMetrics();
    }
}
