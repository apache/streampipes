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

package org.apache.streampipes.commons.prometheus.migration;

import io.prometheus.client.Gauge;
import org.apache.streampipes.commons.prometheus.core.PrometheusMetrics;

/**
 * 迁移指标管理器
 * 继承统一的指标管理器，消除重复代码
 */
public class MigrationMetrics extends PrometheusMetrics {
    
    // 指标名称常量
    private static final String MIGRATION_DURATION = "migration_duration";
    private static final String MIGRATION_STATUS = "migration_status";
    private static final String MIGRATION_COUNT = "migration_count";
    
    public MigrationMetrics(String id) {
        super(id);
    }
    
    @Override
    protected void registerGauges() {
        registerGauge(MIGRATION_DURATION, "Duration of the migration in seconds");
        registerGauge(MIGRATION_STATUS, "Status of the migration (0:STOP; 1:START; 2:SEPARATING; 3:SEPARATED; 4:MIGRATING; 5:MIGRATED; 6:FINISH)");
        registerGauge(MIGRATION_COUNT, "Number of migrations performed");
    }
    
    /**
     * 更新迁移持续时间指标
     * @param duration 持续时间
     */
    public void updateMigrationDuration(double duration) {
        setGaugeValue(MIGRATION_DURATION, duration);
    }
    
    /**
     * 更新迁移状态指标
     * @param status 状态
     */
    public void updateMigrationStatus(double status) {
        setGaugeValue(MIGRATION_STATUS, status);
    }
    
    /**
     * 更新迁移计数指标
     * @param count 计数
     */
    public void updateMigrationCount(double count) {
        setGaugeValue(MIGRATION_COUNT, count);
    }
    
    /**
     * 更新所有指标
     * @param duration 持续时间
     * @param status 状态
     * @param count 计数
     */
    public void updateAllMetrics(double duration, double status, double count) {
        updateMigrationDuration(duration);
        updateMigrationStatus(status);
        updateMigrationCount(count);
    }
    
    // Getters for backward compatibility
    public Gauge getMigrationDurationGauge() {
        return getGauge(MIGRATION_DURATION);
    }
    
    public Gauge getMigrationStatusGauge() {
        return getGauge(MIGRATION_STATUS);
    }
    
    public Gauge getMigrationCountGauge() {
        return getGauge(MIGRATION_COUNT);
    }
}
