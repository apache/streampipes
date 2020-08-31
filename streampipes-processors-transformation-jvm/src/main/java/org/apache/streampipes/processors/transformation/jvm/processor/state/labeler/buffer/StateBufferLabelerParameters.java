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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.buffer;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class StateBufferLabelerParameters extends EventProcessorBindingParams {

    private String sensorListValueProperty;
    private String stateProperty;
    private String stateFilter;
    private String selectedOperation;
    private List<Integer> numberValues;
    private List<String> labelStrings;
    private List<String> comparators;

    public StateBufferLabelerParameters(DataProcessorInvocation graph,
                                        String sensorListValueProperty,
                                        String stateProperty,
                                        String stateFilter,
                                        String selectedOperation,
                                        List<Integer> numberValues,
                                        List<String> labelStrings,
                                        List<String> comparators) {
        super(graph);
        this.sensorListValueProperty = sensorListValueProperty;
        this.stateProperty = stateProperty;
        this.stateFilter = stateFilter;
        this.selectedOperation = selectedOperation;
        this.numberValues = numberValues;
        this.labelStrings = labelStrings;
        this.comparators = comparators;
    }

    public String getSensorListValueProperty() {
        return sensorListValueProperty;
    }

    public void setSensorListValueProperty(String sensorListValueProperty) {
        this.sensorListValueProperty = sensorListValueProperty;
    }

    public String getStateFilter() {
        return stateFilter;
    }

    public void setStateFilter(String stateFilter) {
        this.stateFilter = stateFilter;
    }

    public String getSelectedOperation() {
        return selectedOperation;
    }

    public void setSelectedOperation(String selectedOperation) {
        this.selectedOperation = selectedOperation;
    }

    public String getStateProperty() {
        return stateProperty;
    }

    public void setStateProperty(String stateProperty) {
        this.stateProperty = stateProperty;
    }

    public List<Integer> getNumberValues() {
        return numberValues;
    }

    public void setNumberValues(List<Integer> numberValues) {
        this.numberValues = numberValues;
    }

    public List<String> getLabelStrings() {
        return labelStrings;
    }

    public void setLabelStrings(List<String> labelStrings) {
        this.labelStrings = labelStrings;
    }

    public List<String> getComparators() {
        return comparators;
    }

    public void setComparators(List<String> comparators) {
        this.comparators = comparators;
    }
}
