package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.service.ElementServiceStats;
import org.apache.streampipes.manager.loadbalance.LoadBalancerConfig;
import org.apache.streampipes.manager.loadbalance.PipelineMigrator;
import org.apache.streampipes.manager.loadbalance.ResourceUnitMigration;
import org.apache.streampipes.manager.loadbalance.ServiceLoadCalculator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.*;

public class OverloadMigrator implements PipelineMigrator {


    @Override
    public void doLoadShedding(List<SpServiceRegistration> spServiceRegistrations) {
        if(spServiceRegistrations.isEmpty()){
            return;
        }

        PriorityQueue<Map.Entry<SpServiceRegistration, Float>> maxQueue = new PriorityQueue<>((a, b)-> Float.compare(b.getValue(),a.getValue()));

        PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minQueue = new PriorityQueue<>((a,b)-> Float.compare(a.getValue(),b.getValue()));

        for (SpServiceRegistration spServiceRegistration : spServiceRegistrations) {
            float a=ServiceLoadCalculator.calculateLoad(spServiceRegistration.getSvcId());
            System.out.println(spServiceRegistration.getSvcId()+":"+a);
            maxQueue.offer(Map.entry(spServiceRegistration,a));
            minQueue.offer(Map.entry(spServiceRegistration,a));
        }
        ElementServiceStats.metrics();
        Queue<Map.Entry<SpServiceRegistration,Float>> over = new LinkedList<>();
        while (!maxQueue.isEmpty() &&maxQueue.peek().getValue()> LoadBalancerConfig.OverloadedThresholdPercentage){
            over.offer(maxQueue.poll());
        }

        while (!over.isEmpty()&&!minQueue.isEmpty()&&over.peek().getKey()!=minQueue.peek().getKey()) {
            Map.Entry<SpServiceRegistration,Float> source= over.poll();
            Map.Entry<SpServiceRegistration,Float> target= minQueue.poll();
            ResourceUnitMigration.migration(source.getKey(),source.getValue(),target.getKey(),target.getValue());
        }
    }
}