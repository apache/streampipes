package org.apache.streampipes.node.controller.management.rebalance.triggers;

public class ThresholdTriggerPolicy<T extends Number> extends TrendTriggerPolicy<T>{
    public ThresholdTriggerPolicy(Comparator comparator, T threshold) {
        super(1, comparator, threshold, 1);
    }
}
