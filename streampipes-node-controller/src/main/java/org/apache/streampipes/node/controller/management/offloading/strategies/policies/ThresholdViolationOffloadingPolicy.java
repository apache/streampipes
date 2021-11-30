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
package org.apache.streampipes.node.controller.management.offloading.strategies.policies;

import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThresholdViolationOffloadingPolicy<T extends Comparable<T>> implements OffloadingPolicy<T> {
    private static final Logger LOG =
            LoggerFactory.getLogger(ThresholdViolationOffloadingPolicy.class.getCanonicalName());

    private Queue<T> history;
    private final int length;
    private final T threshold;
    private final Comparator comparator;
    private final int numberOfThresholdViolations;

    private int numPreviousViolations = 0;

    public ThresholdViolationOffloadingPolicy(int length, Comparator comparator, T threshold,
                                              int numberOfThresholdViolations){
        this.length = length;
        this.history = new ArrayBlockingQueue<>(length);
        this.comparator = comparator;
        this.threshold = threshold;
        this.numberOfThresholdViolations = numberOfThresholdViolations;
    }

    public ThresholdViolationOffloadingPolicy(int length, Comparator comparator, T threshold){
        this(length, comparator, threshold, length);
    }

    public ThresholdViolationOffloadingPolicy(Comparator comparator, T threshold) {
        this(1, comparator, threshold);
    }

    @Override
    public void addValue(T value) {
        if(!this.history.offer(value)) {
            this.history.poll();
            this.history.offer(value);
            //TODO: Only for logging; can be removed later
//            if(value.compareTo(this.threshold) > 0){
//                int numViolations = 0;
//                for(T val : this.history){
//                    if(val.compareTo(this.threshold) > 0){
//                        numViolations++;
//                    }
//                }
//                EvaluationLogger.getInstance().logMQTT("Offloading", "policy violation #" + numViolations);
//            }
        }
    }

    @Override
    public boolean isViolated() {
        int numViolations = 0;
        switch (this.comparator){
            case GREATER:
                for(T value : this.history){
                    if(value.compareTo(this.threshold) > 0){
                        numViolations++;
                    }
                }
                break;
            case SMALLER:
                for(T value : this.history){
                    if(value.compareTo(this.threshold) < 0){
                        numViolations++;
                    }
                }
                break;
        }

        if (numViolations > numPreviousViolations) {
            EvaluationLogger.getInstance().logMQTT("Offloading", "policy violation #" + numViolations);
            numPreviousViolations = numViolations;
        }

        if(numViolations >= this.numberOfThresholdViolations){
            LOG.info("Threshold violation detected");
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        LOG.info("Reset history queue to length: " + this.history.size());
        this.history = new ArrayBlockingQueue<>(this.history.size());
        LOG.info("Reset number previous violation counter from: " + numPreviousViolations + "-> to: " + 0);
        this.numPreviousViolations = 0;
    }

    //TODO: Remove -- Only for debugging purposes
    @Override
    public String toString() {
        return "TrendTriggerPolicy{" +
                "history=" + history +
                '}';
    }
}
