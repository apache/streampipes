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
package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.manager.loadbalance.impl.*;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.ResourceUnit;

import java.util.List;

public class LoadManager {

    private static LoadBalancer loadBalancer;
    public static void init(){
        Environment e= Environments.getEnvironment();
        ExtensionServiceSelector selector;
        PipelineMigrator migrator;

        LoadBalancerConfig.LoadTargetStd = e.getLoadTargetStd().getValueOrDefault();
        LoadBalancerConfig.CPUResourceWeigh = e.getCpuResourceWeight().getValueOrDefault();
        LoadBalancerConfig.ThresholdMigratorPercentage = e.getThresholdMigratorPercentage().getValueOrDefault();
        LoadBalancerConfig.MinMigratorPercentage = e.getMinMigratorPercentage().getValueOrDefault();
        LoadBalancerConfig.OverloadedThresholdPercentage = e.getOverloadedThresholdPercentage().getValueOrDefault();
        LoadBalancerConfig.HistoryResourcePercentage = e.getHistoryResourcePercentage().getValueOrDefault();
        LoadBalancerConfig.MemoryResourceWeight = e.getMemoryResourceWeight().getValueOrDefault();
        LoadBalancerConfig.DirMemoryResourceWeight = e.getDirMemoryResourceWeight().getValueOrDefault();


        if(e.getSelector().getValueOrDefault().equals("WeightedRandomSelector")){
            selector=new WeightedRandomSelector();
        }else if (e.getSelector().getValueOrDefault().equals("MinimumLoadSelector")){
            selector = new MinimumLoadSelector();
        }else {
            selector = new WeightedFirstSelector();
        }

        if (e.getMigrator().getValueOrDefault().equals("TransferMigrator")){
            migrator = new TransferMigrator();
        }else if(e.getMigrator().getValueOrDefault().equals("OverloadMigrator")) {
            migrator = new OverloadMigrator();
        }else {
            migrator = new ThresholdMigrator();
        }
        LoadManager.loadBalancer=new ExtensibleLoadManager(selector,migrator);
    }
    public static SpServiceRegistration allocation(ResourceUnit<InvocableStreamPipesEntity> resourceUnit, List<SpServiceRegistration> serviceRegistrations, List<String> label){
        return loadBalancer.allocation(resourceUnit,serviceRegistrations,label);
    }

    public static SpServiceRegistration allocation(ResourceUnit<AdapterDescription> resourceUnit, List<SpServiceRegistration> serviceRegistrations){
        return loadBalancer.allocationPe(resourceUnit,serviceRegistrations,resourceUnit.getLabels());
    }

    public static LoadData getLoadData(){
        return loadBalancer.getLoadData();
    }

    public static LoadData getHistoricalLoadData(){
        return loadBalancer.getHistoricalLoadData();
    }

    public static void stopPipeline(String pipId) {
        loadBalancer.stopPipeline(pipId);
    }

    public static void stopAdapter(String pipId) {
        loadBalancer.stopAdapter(pipId);
    }

    public static void doLoadShedding(){
        loadBalancer.doLoadShedding();
    }

    public static void updateAll(){
        loadBalancer.updateAll();
    }
}

