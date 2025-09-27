package org.apache.streampipes.manager.loadbalance.impl;

import org.apache.streampipes.commons.prometheus.service.ElementServiceStats;
import org.apache.streampipes.manager.loadbalance.LoadBalancerConfig;
import org.apache.streampipes.manager.loadbalance.PipelineMigrator;
import org.apache.streampipes.manager.loadbalance.ResourceUnitMigration;
import org.apache.streampipes.manager.loadbalance.ServiceLoadCalculator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TransferMigrator implements PipelineMigrator {

    @Override
    public void doLoadShedding(List<SpServiceRegistration> spServiceRegistrations) {
        if(spServiceRegistrations.size()<=1){
            return;
        }
        List<Float> floats = new ArrayList<>();
        PriorityQueue<Map.Entry<SpServiceRegistration, Float>> queue = new PriorityQueue<>((a,b)-> Float.compare(b.getValue(),a.getValue()));

        PriorityQueue<Map.Entry<SpServiceRegistration, Float>> minQueue = new PriorityQueue<>((a,b)-> Float.compare(a.getValue(),b.getValue()));

        for (SpServiceRegistration spServiceRegistration : spServiceRegistrations) {
            float a=ServiceLoadCalculator.calculateLoad(spServiceRegistration.getSvcId());
            floats.add(a);
            System.out.println(spServiceRegistration.getSvcId()+":"+a);
            queue.offer(Map.entry(spServiceRegistration,a));
            minQueue.offer(Map.entry(spServiceRegistration,a));

        }

        ElementServiceStats.metrics();
        float avg = ServiceLoadCalculator.calculateAVG(floats);
        System.out.println("std: "+calculateVariance(floats,avg));
        System.out.println(LoadBalancerConfig.OverloadedThresholdPercentage);
        System.out.println();
        if(LoadBalancerConfig.LoadTargetStd<=calculateVariance(floats,avg)){
            while (!queue.isEmpty()&&!minQueue.isEmpty()&&queue.size()>spServiceRegistrations.size()/2) {
                if(queue.peek().getKey()==minQueue.peek().getKey()){
                    break;
                }
                Map.Entry<SpServiceRegistration,Float> source= queue.poll();
                Map.Entry<SpServiceRegistration,Float> target= minQueue.poll();
                ResourceUnitMigration.migration(source.getKey(),source.getValue(),target.getKey(),target.getValue());
            }
            return;
        }

        if(minQueue.peek().getValue()<LoadBalancerConfig.MinMigratorPercentage
                &&queue.peek().getValue()>minQueue.peek().getValue()+10){
            while (!queue.isEmpty()&&!minQueue.isEmpty()&&queue.size()>spServiceRegistrations.size()/2) {
                if(queue.peek().getKey()==minQueue.peek().getKey()){
                    break;
                }
                Map.Entry<SpServiceRegistration,Float> source= queue.poll();
                Map.Entry<SpServiceRegistration,Float> target= minQueue.poll();
                ResourceUnitMigration.migration(source.getKey(),source.getValue(),target.getKey(),target.getValue());
            }
            return;
        }

        if(queue.peek().getValue()>LoadBalancerConfig.OverloadedThresholdPercentage
                &&queue.peek().getValue()>minQueue.peek().getValue()+10){
            while (!queue.isEmpty()&&!minQueue.isEmpty()&&queue.size()>spServiceRegistrations.size()/2) {
                if(queue.peek().getKey()==minQueue.peek().getKey()){
                    break;
                }
                Map.Entry<SpServiceRegistration,Float> source= queue.poll();
                Map.Entry<SpServiceRegistration,Float> target= minQueue.poll();
                ResourceUnitMigration.migration(source.getKey(),source.getValue(),target.getKey(),target.getValue());
            }
        }


    }


    public static float calculateVariance(List<Float> data,float avg) {


        // Step 1: Calculate squared differences from the mean
        float squaredDiffSum = 0;
        for (float value : data) {
            float diff = value - avg;
            squaredDiffSum += diff * diff;
        }

        // Step 2: Divide by the number of elements
        return squaredDiffSum / data.size();
    }
}
