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
package org.apache.streampipes.node.controller.management.offloading.policies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MultiOccurrenceThresholdViolationPolicy<T extends Number> implements OffloadingPolicy<T> {
    private static final Logger LOG =
            LoggerFactory.getLogger(MultiOccurrenceThresholdViolationPolicy.class.getCanonicalName());

    private Queue<T> history;
    private final T threshold;
    private final Comparator comparator;
    private final int numberOfThresholdViolations;

    public MultiOccurrenceThresholdViolationPolicy(int length, Comparator comparator, T threshold,
                                                   int numberOfThresholdViolations){
        this.history = new ArrayBlockingQueue<>(length);
        this.comparator = comparator;
        this.threshold = threshold;
        this.numberOfThresholdViolations = numberOfThresholdViolations;
    }

    public MultiOccurrenceThresholdViolationPolicy(int length, Comparator comparator, T threshold){
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
    public boolean isViolated() {
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
            LOG.info("Threshold violation detected");
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
