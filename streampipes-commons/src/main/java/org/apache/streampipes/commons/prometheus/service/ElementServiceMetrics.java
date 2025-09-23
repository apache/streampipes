package org.apache.streampipes.commons.prometheus.service;

import io.prometheus.client.Gauge;
import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

public class ElementServiceMetrics {

    public final Gauge cpuUsageGauge;
    public final Gauge memoryUsageGauge;
    public final Gauge weightGauge;
    public final Gauge systemLoadGauge;
    public final Gauge historicalSystemLoadGauge;
    public final Gauge currentSystemLoadGauge;

    public static final Gauge serviceCount =
            StreamPipesCollectorRegistry.registerGauge(
                    "serviceCount",
                    "serviceCount");

    public void remove(){
        cpuUsageGauge.set(0);
        StreamPipesCollectorRegistry.remove(cpuUsageGauge);
        memoryUsageGauge.set(0);
        StreamPipesCollectorRegistry.remove(memoryUsageGauge);
        weightGauge.set(0);
        StreamPipesCollectorRegistry.remove(weightGauge);
        systemLoadGauge.set(0);
        StreamPipesCollectorRegistry.remove(systemLoadGauge);
        historicalSystemLoadGauge.set(0);
        StreamPipesCollectorRegistry.remove(historicalSystemLoadGauge);
        currentSystemLoadGauge.set(0);
        StreamPipesCollectorRegistry.remove(currentSystemLoadGauge);
    }

    public ElementServiceMetrics(String id) {
        cpuUsageGauge = StreamPipesCollectorRegistry.registerGauge(
                "cpu_usage_" + id,
                "CPU usage percentage "+ id
        );
        memoryUsageGauge = StreamPipesCollectorRegistry.registerGauge(
                "memory_usage_" + id,
                "Memory usage in bytes "+ id
        );
        weightGauge = StreamPipesCollectorRegistry.registerGauge(
                "weight_" + id,
                "Weight of remaining available resources for service"+ id
        );
        systemLoadGauge = StreamPipesCollectorRegistry.registerGauge(
                "system_load_" + id,
                "System load average over the last minute "+ id
        );
        historicalSystemLoadGauge = StreamPipesCollectorRegistry.registerGauge(
                "historical_system_load_" + id,
                "Historical system load average "+ id
        );
        currentSystemLoadGauge = StreamPipesCollectorRegistry.registerGauge(
                "current_system_load_" + id,
                "Current system load average "+ id
        );
    }
}
