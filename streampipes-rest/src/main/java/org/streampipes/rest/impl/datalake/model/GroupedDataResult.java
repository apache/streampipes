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

package org.streampipes.rest.impl.datalake.model;

import java.util.HashMap;
import java.util.Map;

public class GroupedDataResult {

    private int total;
    private Map<String, DataResult> dataResults;

    public GroupedDataResult() {
        this.total = 0;
    }

    public GroupedDataResult(int total, Map<String, DataResult> dataResults) {
        this.total = total;
        this.dataResults = dataResults;
    }

    public int getTotal() {
        return total;
    }

    public Map<String, DataResult> getDataResults() {
        return dataResults;
    }

    public void addDataResult(String index, DataResult dataResult) {
        if (dataResults == null) {
            dataResults = new HashMap<>();
        }
        dataResults.put(index, dataResult);
        this.total += 1;
    }
}
