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

package org.apache.streampipes.node.controller.management.offloading.model;

import org.apache.streampipes.node.controller.management.offloading.model.policies.OffloadingPolicy;
import org.apache.streampipes.node.controller.management.offloading.model.property.ResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.model.selection.SelectionStrategy;

public class OffloadingStrategy<T> {
    private SelectionStrategy selectionStrategy;
    private OffloadingPolicy<T> offloadingPolicy;
    private ResourceProperty<T> resourceProperty;

    public OffloadingStrategy(OffloadingPolicy offloadingPolicy, ResourceProperty resourceProperty,
                              SelectionStrategy selectionStrategy){
        this.offloadingPolicy = offloadingPolicy;
        this.resourceProperty = resourceProperty;
        this.selectionStrategy = selectionStrategy;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    public OffloadingPolicy<?> getOffloadingPolicy() {
        return offloadingPolicy;
    }

    public void setOffloadingPolicy(OffloadingPolicy<T> offloadingPolicy) {
        this.offloadingPolicy = offloadingPolicy;
    }

    public ResourceProperty<?> getResourceProperty() {
        return resourceProperty;
    }

    public void setResourceProperty(ResourceProperty<T> resourceProperty) {
        this.resourceProperty = resourceProperty;
    }

}
