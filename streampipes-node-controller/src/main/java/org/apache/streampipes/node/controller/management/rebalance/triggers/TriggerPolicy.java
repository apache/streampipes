package org.apache.streampipes.node.controller.management.rebalance.triggers;

public interface TriggerPolicy<T> {
    void addValue(T value);
    boolean isTriggered();
    void reset();
}
