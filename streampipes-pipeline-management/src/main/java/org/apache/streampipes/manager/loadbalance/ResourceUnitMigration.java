package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.commons.exceptions.SpException;
import org.apache.streampipes.commons.prometheus.migration.MigrationStats;
import org.apache.streampipes.extensions.api.locker.impl.SpStateLocker;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.execution.http.DetachHttpRequest;
import org.apache.streampipes.manager.execution.http.InvokeHttpRequest;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnit;
import org.apache.streampipes.model.loadbalancer.PipelineInfo;
import org.apache.streampipes.model.loadbalancer.PipelineStates;
import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnitStats;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResourceUnitMigration {

    private static final Logger log = LoggerFactory.getLogger(ResourceUnitMigration.class);

    public static void migration(LoadBalanceResourceUnit<InvocableStreamPipesEntity> loadBalanceResourceUnit, SpServiceRegistration registration) {
        loadBalanceResourceUnit.setServiceId(registration.getSvcId());
        String pipelineId = loadBalanceResourceUnit.getPipelineId();
        MigrationStats stats = MigrationStats.get(pipelineId);
        if(stats==null){
            stats= new MigrationStats(pipelineId);
        }
        long t0 = System.nanoTime();
        PipelineInfo currentPipelineInfo = SpStateLocker.INSTANCE.setPipelineInfo(pipelineId, PipelineStates.START);
        stats.migrationStatus = 1;
        MigrationStats.metrics();

        currentPipelineInfo.setPipelineState(PipelineStates.SEPARATING);
        stats.migrationStatus = 2;
        MigrationStats.metrics();
        for (InvocableStreamPipesEntity pipesEntity : loadBalanceResourceUnit.getElements()) {
            String endpointUrl = pipesEntity.getSelectedEndpointUrl() + pipesEntity.getDetachPath();
            new DetachHttpRequest().execute(pipesEntity, endpointUrl, pipelineId);
        }
        currentPipelineInfo.setPipelineState(PipelineStates.SEPARATED);
        stats.migrationStatus = 3;
        MigrationStats.metrics();

        currentPipelineInfo.setPipelineState(PipelineStates.MIGRATING);
        stats.migrationStatus = 4;
        MigrationStats.metrics();
        for (InvocableStreamPipesEntity pipesEntity : loadBalanceResourceUnit.getElements()) {
            pipesEntity.setSelectedEndpointUrl(getSelectedEndpoint(pipesEntity, registration.getServiceUrl()));
            String endpointUrl = pipesEntity.getSelectedEndpointUrl();
            new InvokeHttpRequest().execute(pipesEntity, endpointUrl, pipelineId);
        }
        currentPipelineInfo.setPipelineState(PipelineStates.MIGRATED);
        stats.migrationStatus = 5;
        MigrationStats.metrics();

        Pipeline pipeline = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getPipeline(pipelineId);

        for (DataSinkInvocation dataSinkInvocation : pipeline.getActions()) {
            for (InvocableStreamPipesEntity entity : loadBalanceResourceUnit.getElements()) {
                if (dataSinkInvocation.getElementId().equals(entity.getElementId())) {
                    dataSinkInvocation.setSelectedEndpointUrl(entity.getSelectedEndpointUrl());
                }
            }
        }
        for (DataProcessorInvocation processorInvocation : pipeline.getSepas()) {
            for (InvocableStreamPipesEntity entity : loadBalanceResourceUnit.getElements()) {
                if (processorInvocation.getElementId().equals(entity.getElementId())) {
                    processorInvocation.setSelectedEndpointUrl(entity.getSelectedEndpointUrl());
                }
            }
        }
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updatePipeline(pipeline);
        currentPipelineInfo.setPipelineState(PipelineStates.FINISH);
        stats.migrationStatus = 6;
        stats.migrationDuration = (System.nanoTime() - t0) / 1_000_000_000.0;
        stats.migrationCount++;
        stats.migrationStatus = 0;
        MigrationStats.metrics();
    }

    public static void migrationForHealth(LoadBalanceResourceUnit<InvocableStreamPipesEntity> loadBalanceResourceUnit, SpServiceRegistration registration) {
        System.out.println("update loadBalanceResourceUnit to:"+registration.getHost());
        loadBalanceResourceUnit.setServiceId(registration.getSvcId());
        try {
            for (InvocableStreamPipesEntity pipesEntity : loadBalanceResourceUnit.getElements()) {
                pipesEntity.setSelectedEndpointUrl(getSelectedEndpoint(pipesEntity, registration.getServiceUrl()));
                String endpointUrl = pipesEntity.getSelectedEndpointUrl();
                new InvokeHttpRequest().execute(pipesEntity, endpointUrl, loadBalanceResourceUnit.getPipelineId());
            }

            Pipeline pipeline = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getPipeline(loadBalanceResourceUnit.getPipelineId());

            for (DataProcessorInvocation processorInvocation : pipeline.getSepas()) {
                for (InvocableStreamPipesEntity entity : loadBalanceResourceUnit.getElements()) {
                    if (processorInvocation.getElementId().equals(entity.getElementId())) {
                        processorInvocation.setSelectedEndpointUrl(entity.getSelectedEndpointUrl());
                    }
                }
            }

            for (DataSinkInvocation dataSinkInvocation : pipeline.getActions()) {
                for (InvocableStreamPipesEntity entity : loadBalanceResourceUnit.getElements()) {
                    if (dataSinkInvocation.getElementId().equals(entity.getElementId())) {
                        dataSinkInvocation.setSelectedEndpointUrl(entity.getSelectedEndpointUrl());
                    }
                }
            }
            StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updatePipeline(pipeline);
        } catch (Exception e){
            e.printStackTrace();
        }

    }



    public static void migration(SpServiceRegistration sourceService, double source, SpServiceRegistration targetService,double target) {
        PipelineRuntimeData.loadAll();
        List<LoadBalanceResourceUnit<InvocableStreamPipesEntity>> units = PipelineRuntimeData.getSinksAndProcess().get(sourceService.getSvcId());
        PipelineRuntimeData.clearAll();
        LoadData loadData = LoadManager.getLoadData();
        List<LoadBalanceResourceUnitStats> list = loadData.getResourceUnitStats(sourceService.getSvcId());
        if(list==null){
            list=new ArrayList<>();
        }
        list.sort((a, b) -> Double.compare(  b.eventRateOut, a.eventRateOut));

        List<LoadBalanceResourceUnitStats> list1 = loadData.getResourceUnitStats(targetService.getSvcId());
        if(list1==null){
            list1=new ArrayList<>();
        }
        double all =0;
        for(LoadBalanceResourceUnitStats stats:list){
            all +=  stats.eventRateOut;
        }
        double num=0;
        for(LoadBalanceResourceUnitStats stats:list1){
            num+=  stats.eventRateOut;

        }
        all += num;

        double tar=0;
        for(LoadBalanceResourceUnitStats loadBalanceResourceUnitStats : list){
            if(tar>=(((all-num)*(source-target)/(2*source)))){
                System.out.println("target:out "+tar);
                System.out.println("target:neet Tran:" + (((all-num)*(source-target)/(2*source))));
                break;
            }

            for(LoadBalanceResourceUnit<InvocableStreamPipesEntity> unit : units){
                if(unit.getId().equals(loadBalanceResourceUnitStats.getResourceId())){
                    try {
                        String tryLockPipelineId = unit.getPipelineId();
                        SpStateLocker.INSTANCE.tryLock(tryLockPipelineId, TimeUnit.SECONDS);
                        try {
                            migration(unit,targetService);
                            tar+= loadBalanceResourceUnitStats.eventRateOut;
                        } finally {
                            SpStateLocker.INSTANCE.unlock(tryLockPipelineId);
                        }
                    } catch (SpException e){
                        log.info(e.getMessage());
                    }

                }
            }
        }
    }

    private static String getSelectedEndpoint(InvocableStreamPipesEntity pipelineElement, String url) {
        return ExtensionsServiceEndpointUtils
                .getPipelineElementType(pipelineElement)
                .getInvocationUrl(url, pipelineElement.getAppId());
    }
}