package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.service.ElementServiceStats;
import org.apache.streampipes.manager.loadbalance.LoadBalancerConfig;
import org.apache.streampipes.manager.loadbalance.PipelineMigrator;
import org.apache.streampipes.manager.loadbalance.ResourceUnitMigration;
import org.apache.streampipes.manager.loadbalance.ServiceLoadCalculator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.*;

public class ThresholdMigrator implements PipelineMigrator{



    @Override
    public void doLoadShedding(List<SpServiceRegistration> spServiceRegistrations) {
        if(spServiceRegistrations.size()<=1){
            return;
        }
        List<Float> floats = new ArrayList<>();
        PriorityQueue<Map.Entry<SpServiceRegistration, Float>> queue = new PriorityQueue<>((a,b)-> Float.compare(b.getValue(),a.getValue()));

        PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minQueue = new PriorityQueue<>((a,b)-> Float.compare(a.getValue(),b.getValue()));

        for (SpServiceRegistration spServiceRegistration : spServiceRegistrations) {
            float a=ServiceLoadCalculator. calculateLoad(spServiceRegistration.getSvcId());
            floats.add(a);
            queue.offer(Map.entry(spServiceRegistration,a));
            minQueue.offer(Map.entry(spServiceRegistration,a));
        }
        ElementServiceStats.metrics();
        float avg = ServiceLoadCalculator.calculateAVG(floats);
        Queue<Map.Entry<SpServiceRegistration, Float>> over = new LinkedList<>();
        while (!queue.isEmpty() &&queue.peek().getValue()> avg+LoadBalancerConfig.ThresholdMigratorPercentage
                &&over.size()<spServiceRegistrations.size()/2){
            over.offer(queue.poll());
        }

        while (!over.isEmpty()&&!minQueue.isEmpty()) {
            System.out.println("service "+over.peek().getKey().getSvcId()+" to "+minQueue.peek().getKey().getSvcId());
            Map.Entry<SpServiceRegistration,Float> source= over.poll();
            Map.Entry<SpServiceRegistration,Float> target= minQueue.poll();
            ResourceUnitMigration.migration(source.getKey(),source.getValue(),target.getKey(),target.getValue());
        }

        while (!queue.isEmpty()&&!minQueue.isEmpty()
                &&minQueue.peek().getValue()<LoadBalancerConfig.MinMigratorPercentage
                &&queue.peek().getValue()>minQueue.peek().getValue()+20
                &&queue.size()>spServiceRegistrations.size()/2){
            System.out.println("service "+queue.peek().getKey().getSvcId()+" to "+minQueue.peek().getKey().getSvcId());
            Map.Entry<SpServiceRegistration,Float> source= queue.poll();
            Map.Entry<SpServiceRegistration,Float> target= minQueue.poll();
            ResourceUnitMigration.migration(source.getKey(),source.getValue(),target.getKey(),target.getValue());
        }
        System.out.println("end");
    }
}