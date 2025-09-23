package org.apache.streampipes.commons.prometheus.service;

import java.util.HashMap;
import java.util.Map;

public class ElementServiceStats {
    public static final  Map<String,ElementServiceMetrics> elementServiceMetricsMap = new HashMap<>();

    public static final Map<String,ElementServiceStats> elementServiceStats = new HashMap<>();

    public String Id;
    public double cpuUsage = 0.0;
    public double memoryUsage = 0.0;
    public double weight = 1.0;
    public double systemLoad = 0.0;
    public double historicalSystemLoad = 0.0;
    public double currentSystemLoad = 0.0;

    public ElementServiceStats(String id){
        if(!elementServiceStats.containsKey(id)) {
            elementServiceStats.put(id, this);
            elementServiceMetricsMap.put(id, new ElementServiceMetrics(id.substring(id.length()-6)));
            this.Id = id;
        }
    }

    public void remove(){
        elementServiceStats.remove(this.Id);
        elementServiceMetricsMap.remove(this.Id).remove();
    }

    public static void metricsByReport(String serviceId,double cpuUsage,double memoryUsage,double weight){
        ElementServiceStats stats = elementServiceStats.get(serviceId);
        if(stats==null){
            stats=new ElementServiceStats(serviceId);
        }
        stats.cpuUsage=cpuUsage;
        stats.memoryUsage=memoryUsage;
        stats.weight=weight;
        // Immediately write values to the corresponding Prometheus gauges
        ElementServiceMetrics metrics = elementServiceMetricsMap.get(serviceId);
        if (metrics != null) {
            metrics.cpuUsageGauge.set(stats.cpuUsage);
            metrics.memoryUsageGauge.set(stats.memoryUsage);
            metrics.weightGauge.set(stats.weight);
        }
        ElementServiceMetrics.serviceCount.set(elementServiceStats.size());
    }

    public static void metrics(){
        for(Map.Entry<String,ElementServiceStats> e : elementServiceStats.entrySet()) {
            ElementServiceStats stats = e.getValue();
            ElementServiceMetrics metrics = elementServiceMetricsMap.get(stats.Id);
            if (metrics == null) {
                String currentServiceId = e.getKey();
                System.out.println(currentServiceId);
                continue;
            }
            metrics.cpuUsageGauge.set(stats.cpuUsage);
            metrics.memoryUsageGauge.set(stats.memoryUsage);
            metrics.weightGauge.set(stats.weight);
            metrics.historicalSystemLoadGauge.set(stats.historicalSystemLoad);
            metrics.systemLoadGauge.set(stats.systemLoad);
            metrics.currentSystemLoadGauge.set(stats.currentSystemLoad);
        }
        ElementServiceMetrics.serviceCount.set(elementServiceStats.size());
    }
}
