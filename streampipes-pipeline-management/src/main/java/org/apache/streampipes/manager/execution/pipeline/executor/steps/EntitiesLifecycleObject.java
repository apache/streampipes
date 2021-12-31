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
package org.apache.streampipes.manager.execution.pipeline.executor.steps;

import java.util.ArrayList;
import java.util.List;

public class EntitiesLifecycleObject<T> {

    private final List<T> entitiesToStart;

    private final List<T> entitiesToStop;

    private final List<T> entitiesToStore;

    private final List<T> entitiesToDelete;

    public EntitiesLifecycleObject(){
        entitiesToStart = new ArrayList<>();
        entitiesToStop = new ArrayList<>();
        entitiesToStore = new ArrayList<>();
        entitiesToDelete = new ArrayList<>();
    }

    public List<T> getEntitiesToStart() {
        return entitiesToStart;
    }

    public List<T> getEntitiesToStop() {
        return entitiesToStop;
    }

    public List<T> getEntitiesToStore() {
        return entitiesToStore;
    }

    public List<T> getEntitiesToDelete() {
        return entitiesToDelete;
    }
}
