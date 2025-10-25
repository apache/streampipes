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

//package org.apache.streampipes.commons.prometheus.migration;
//
//import org.apache.streampipes.commons.prometheus.core.PrometheusStats;
//
///**
// * 迁移统计信息管理器
// * 继承统一的统计信息管理器，消除重复代码
// */
//public class MigrationStats extends PrometheusStats<MigrationMetrics> {
//
//    public double migrationDuration = 0;
//    public double migrationStatus = 0;
//    public double migrationCount = 0;
//
//    public MigrationStats(String id) {
//        super(id);
//    }
//
//    @Override
//    protected MigrationMetrics createMetrics() {
//        return new MigrationMetrics(id);
//    }
//
//    @Override
//    protected void updateMetrics() {
//        metrics.updateAllMetrics(migrationDuration, migrationStatus, migrationCount);
//    }
//
//    /**
//     * 更新迁移持续时间
//     * @param duration 持续时间
//     */
//    public void setMigrationDuration(double duration) {
//        this.migrationDuration = duration;
//        metrics.updateMigrationDuration(duration);
//    }
//
//    /**
//     * 更新迁移状态
//     * @param status 状态
//     */
//    public void setMigrationStatus(double status) {
//        this.migrationStatus = status;
//        metrics.updateMigrationStatus(status);
//    }
//
//    /**
//     * 更新迁移计数
//     * @param count 计数
//     */
//    public void setMigrationCount(double count) {
//        this.migrationCount = count;
//        metrics.updateMigrationCount(count);
//    }
//
//    /**
//     * 更新所有指标
//     * @param duration 持续时间
//     * @param status 状态
//     * @param count 计数
//     */
//    public void updateAllMetrics(double duration, double status, double count) {
//        this.migrationDuration = duration;
//        this.migrationStatus = status;
//        this.migrationCount = count;
//        metrics.updateAllMetrics(duration, status, count);
//    }
//
//    // Getters
//    public double getMigrationDuration() {
//        return migrationDuration;
//    }
//
//    public double getMigrationStatus() {
//        return migrationStatus;
//    }
//
//    public double getMigrationCount() {
//        return migrationCount;
//    }
//
//    /**
//     * 获取迁移统计信息（向后兼容）
//     * @param id 统计信息ID
//     * @return 统计信息
//     */
//    @SuppressWarnings("unchecked")
//    public static MigrationStats get(String id) {
//        return (MigrationStats) PrometheusStats.getStats(id);
//    }
//
//    /**
//     * 更新所有指标（向后兼容）
//     */
//    public static void metrics() {
//        PrometheusStats.updateAllMetrics();
//    }
//}
