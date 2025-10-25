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

//package org.apache.streampipes.commons.prometheus.loadbalancer;
//
//import io.prometheus.client.Gauge;
//import org.apache.streampipes.commons.prometheus.core.PrometheusMetrics;
//
///**
// * 负载均衡器指标管理器
// * 继承统一的指标管理器，消除重复代码
// */
//public class LoadBalancerMetrics extends PrometheusMetrics {
//
//    // 指标名称常量
//    private static final String LB_EVALUATION_DURATION_SECONDS = "lb_evaluation_duration_seconds";
//    private static final String LB_STDDEV = "lb_stddev";
//    private static final String LB_IMBALANCE_RATIO = "lb_imbalance_ratio";
//
//    public LoadBalancerMetrics(String id) {
//        super(id);
//    }
//
//    @Override
//    protected void registerGauges() {
//        registerGauge(LB_EVALUATION_DURATION_SECONDS, "Duration of the load balancer evaluation in seconds");
//        registerGauge(LB_STDDEV, "Standard deviation of the load across services");
//        registerGauge(LB_IMBALANCE_RATIO, "Imbalance ratio of the load across services");
//    }
//
//    /**
//     * 更新评估持续时间指标
//     * @param duration 持续时间
//     */
//    public void updateEvaluationDuration(double duration) {
//        setGaugeValue(LB_EVALUATION_DURATION_SECONDS, duration);
//    }
//
//    /**
//     * 更新标准差指标
//     * @param stddev 标准差
//     */
//    public void updateStddev(double stddev) {
//        setGaugeValue(LB_STDDEV, stddev);
//    }
//
//    /**
//     * 更新不平衡比率指标
//     * @param ratio 不平衡比率
//     */
//    public void updateImbalanceRatio(double ratio) {
//        setGaugeValue(LB_IMBALANCE_RATIO, ratio);
//    }
//
//    /**
//     * 更新所有指标
//     * @param duration 持续时间
//     * @param stddev 标准差
//     * @param ratio 不平衡比率
//     */
//    public void updateAllMetrics(double duration, double stddev, double ratio) {
//        updateEvaluationDuration(duration);
//        updateStddev(stddev);
//        updateImbalanceRatio(ratio);
//    }
//
//    // Getters for backward compatibility
//    public Gauge getLbEvaluationDurationSecondsGauge() {
//        return getGauge(LB_EVALUATION_DURATION_SECONDS);
//    }
//
//    public Gauge getLbStddevGauge() {
//        return getGauge(LB_STDDEV);
//    }
//
//    public Gauge getLbImbalanceRadioGauge() {
//        return getGauge(LB_IMBALANCE_RATIO);
//    }
//}
