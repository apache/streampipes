package org.apache.streampipes.node.controller.management.rebalance.triggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class TrendTriggerPolicy<T extends Number> implements TriggerPolicy<T>{

    private Queue<T> history;
    private T threshold;
    private Comparator comparator;
    private int numberOfThresholdViolations;
    private static final Logger LOG =
            LoggerFactory.getLogger(TrendTriggerPolicy.class.getCanonicalName());

    public TrendTriggerPolicy(int length, Comparator comparator,T threshold, int numberOfThresholdViolations){
        this.history = new ArrayBlockingQueue<>(length);
        this.comparator = comparator;
        this.threshold = threshold;
        this.numberOfThresholdViolations = numberOfThresholdViolations;
    }

    public TrendTriggerPolicy(int length, Comparator comparator, T threshold){
        this(length, comparator, threshold, length);
    }

    @Override
    public void addValue(T value) {
        if(!this.history.offer(value)) {
            this.history.poll();
            this.history.offer(value);
        }
    }

    @Override
    public boolean isTriggered() {
        int numViolations = 0;
        switch (this.comparator){
            case GREATER:
                for(T value : this.history){
                    //TODO: Replace comparison with sth more robust than conversion into double values (either
                    // compare double or long values? Or compare BigDecimal values instead?)
                    if(value.doubleValue() > this.threshold.doubleValue()){
                        numViolations++;
                    }
                }
                break;
            case SMALLER:
                for(T value : this.history){
                    if(value.doubleValue() < this.threshold.doubleValue()){
                        numViolations++;
                    }
                }
                break;
        }
        if(numViolations >= this.numberOfThresholdViolations){
            LOG.info("Trend above threshold detected.");
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        this.history = new ArrayBlockingQueue<>(this.history.size());
    }

    //TODO: Remove -- Only for debugging purposes
    @Override
    public String toString() {
        return "TrendTriggerPolicy{" +
                "history=" + history +
                '}';
    }
}
