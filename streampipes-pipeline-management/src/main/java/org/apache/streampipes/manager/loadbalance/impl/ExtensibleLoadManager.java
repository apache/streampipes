package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.loadbalancer.LoadBalancerStats;
import org.apache.streampipes.commons.prometheus.migration.MigrationStats;
import org.apache.streampipes.extensions.api.locker.impl.SpStateLocker;
import org.apache.streampipes.manager.health.ServiceRegistrationManager;
import org.apache.streampipes.manager.loadbalance.*;
import org.apache.streampipes.manager.monitoring.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.ResourceUnit;
import org.apache.streampipes.model.loadbalancer.ResourceUnitStats;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;
import org.apache.streampipes.model.monitoring.MessageCounter;
import org.apache.streampipes.model.monitoring.SpMetricsEntry;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExtensibleLoadManager implements LoadBalancer {

    ExtensionServiceSelector selector;

    ServiceRegistrationManager serviceManager;

    LoadData loadData;

    LoadData historicalLoadData;

    PipelineMigrator migrator;

    public ExtensibleLoadManager(ExtensionServiceSelector selector, PipelineMigrator pipelineMigrator) {
        this.selector = selector;
        this.migrator = pipelineMigrator;
        serviceManager = new ServiceRegistrationManager(
                StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage());
        updateAll();
        historicalLoadData = new LoadData(new HashMap<>(),new HashMap<>());
    }

    @Override
    public SpServiceRegistration allocation(ResourceUnit<InvocableStreamPipesEntity> resourceUnit, List<SpServiceRegistration> serviceRegistrations, List<String> label) {
        //SpServiceRegistration spServiceRegistration = selector.select(serviceRegistrations, label);
        //PipelineRuntimeData.addSinkAndProcess(resourceUnit, spServiceRegistration);
        return selector.select(serviceRegistrations, label);
    }

    @Override
    public SpServiceRegistration allocationPe(ResourceUnit<AdapterDescription> resourceUnit, List<SpServiceRegistration> serviceRegistrations, List<String> label) {
        //SpServiceRegistration spServiceRegistration = selector.select(serviceRegistrations, label);
        //PipelineRuntimeData.addAdapter(resourceUnit, spServiceRegistration);
        return selector.select(serviceRegistrations, label);
    }

    public void stopPipeline(String pipelineId) {
        SpStateLocker.INSTANCE.tryLock(pipelineId, TimeUnit.SECONDS);
        try {
            PipelineRuntimeData.deleteSinkAndProcess(pipelineId);
        } finally {
            SpStateLocker.INSTANCE.unlock(pipelineId);
        }
    }

    public void stopAdapter(String adapterId) {
        SpStateLocker.INSTANCE.tryLock(adapterId, TimeUnit.SECONDS);
        try {
            PipelineRuntimeData.deleteAdapter(adapterId);
        } finally {
            SpStateLocker.INSTANCE.unlock(adapterId);
        }
    }

    public void updateAll() {
        historicalLoadData = loadData;
        loadData = new LoadData(getServiceUsage(),getResourceUnitStats());
    }

    private Map<String, ServiceLoadDataReport> getServiceUsage() {
        Map<String, ServiceLoadDataReport> map = new HashMap<>();
        ExtensionsLogProvider provider = ExtensionsLogProvider.INSTANCE;
        for (SpServiceRegistration registration : serviceManager.getAivServices()) {
            if (provider.getServiceLoadDataReports(registration.getSvcId()) != null) {
                map.put(registration.getSvcId(), provider.getServiceLoadDataReports(registration.getSvcId()));
            }
        }
        return map;
    }

    private Map<String, List<ResourceUnitStats>> getResourceUnitStats() {
        Map<String, List<ResourceUnitStats>> map = new HashMap<>();
        ExtensionsLogProvider provider = ExtensionsLogProvider.INSTANCE;
        PipelineRuntimeData.loadAll();
        for (Map.Entry<String, List<ResourceUnit<InvocableStreamPipesEntity>>> entry : PipelineRuntimeData.getSinksAndProcess().entrySet()) {
            for (ResourceUnit<InvocableStreamPipesEntity> resourceUnit : entry.getValue()) {
                ResourceUnitStats resourceUnitStats = new ResourceUnitStats(resourceUnit.getId());
                long countOut = 0L;
                long countIn = 0;
                long throughputIn = 0;
                long throughputOut = 0;
                for (InvocableStreamPipesEntity entity : resourceUnit.getElements()) {
                    SpMetricsEntry spMetricsEntry = provider.getMetricInfosForResource(entity.getElementId());
                    for (Map.Entry<String, MessageCounter> e : spMetricsEntry.getMessagesIn().entrySet()) {
                        countIn += e.getValue().getCounter();
                        throughputIn += e.getValue().getSize();
                    }
                    countOut += spMetricsEntry.getMessagesOut().getCounter();
                    throughputOut += spMetricsEntry.getMessagesOut().getSize();
                }
                resourceUnitStats.setEventRateIn((double) countIn );
                resourceUnitStats.setEventRateOut((double) countOut );
                resourceUnitStats.setEventThroughputIn((double) throughputIn );
                resourceUnitStats.setEventThroughputOut((double) throughputOut );
                if (!map.containsKey(resourceUnit.getServiceId())) {
                    map.put(resourceUnit.getServiceId(), new ArrayList<>());
                }

                map.get(resourceUnit.getServiceId()).add(resourceUnitStats);
            }
        }

        for (Map.Entry<String, List<ResourceUnit<AdapterDescription>>> entry : PipelineRuntimeData.getAdapter().entrySet()) {
            for (ResourceUnit<AdapterDescription> resourceUnit : entry.getValue()) {
                ResourceUnitStats resourceUnitStats = new ResourceUnitStats(resourceUnit.getId());
                long countOut = 0L;
                long countIn = 0;
                long throughputIn = 0;
                long throughputOut = 0;
                for (AdapterDescription entity : resourceUnit.getElements()) {
                    SpMetricsEntry spMetricsEntry = provider.getMetricInfosForResource(entity.getElementId());
                    for (Map.Entry<String, MessageCounter> e : spMetricsEntry.getMessagesIn().entrySet()) {
                        countIn += e.getValue().getCounter();
                        throughputIn += e.getValue().getSize();
                    }
                    countOut += spMetricsEntry.getMessagesOut().getCounter();
                    throughputOut += spMetricsEntry.getMessagesOut().getSize();
                }
                resourceUnitStats.setEventRateIn((double) countIn );
                resourceUnitStats.setEventRateOut((double) countOut );
                resourceUnitStats.setEventThroughputIn((double) throughputIn );
                resourceUnitStats.setEventThroughputOut((double) throughputOut );
                if (!map.containsKey(resourceUnit.getServiceId())) {
                    map.put(resourceUnit.getServiceId(), new ArrayList<>());
                }

                map.get(resourceUnit.getServiceId()).add(resourceUnitStats);

            }
        }
        PipelineRuntimeData.clearAll();
        return map;
    }

    public void doLoadShedding(){
        long t0 = System.nanoTime();
        migrator.doLoadShedding(serviceManager.getAivServices());
        List<Double> loadsAfter = serviceManager.getAllServices().stream()
                .map(s -> (double) ServiceLoadCalculator.calculateLoad(s.getSvcId()))
                .toList();
        double evalSeconds = (System.nanoTime() - t0) / 1e9;
        LoadBalancerStats stats = LoadBalancerStats.get("ExtensibleLoadManager");
        if (stats == null) {
            stats = new LoadBalancerStats("ExtensibleLoadManager");
        }
        stats.lbEvaluationDurationSeconds = evalSeconds;
        stats.lbStddev = stddev(loadsAfter);
        stats.lbImbalanceRatio = imbalance(loadsAfter);

        LoadBalancerStats.metrics();
    }

    static double avg(List<Double> xs){ return xs.isEmpty()?0:xs.stream().mapToDouble(d->d).average().orElse(0); }

    static double stddev(List<Double> xs){
        if (xs.size()<=1) return 0;
        double a = avg(xs);
        double v = xs.stream().mapToDouble(d->(d-a)*(d-a)).sum()/xs.size();
        return Math.sqrt(v);
    }

    static double imbalance(List<Double> xs){
        if (xs.isEmpty()) return 0;
        double max = xs.stream().mapToDouble(d->d).max().orElse(0);
        double min = xs.stream().mapToDouble(d->d).min().orElse(0);
        return min<=1e-6 ? 0 : max/min;
    }

    public LoadData getLoadData() {
        return loadData;
    }

    public LoadData getHistoricalLoadData() {
        return historicalLoadData;
    }
}
