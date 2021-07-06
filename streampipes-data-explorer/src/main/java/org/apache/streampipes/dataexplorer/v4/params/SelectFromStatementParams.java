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

package org.apache.streampipes.dataexplorer.v4.params;

import javax.annotation.Nullable;

public class SelectFromStatementParams extends QueryParamsV4 {

    private final String selectedColumns;
    private final String aggregationFunction;

    public static SelectFromStatementParams from(String measurementID, @Nullable String columns, @Nullable String aggregationFunction) {
        return new SelectFromStatementParams(measurementID, columns, aggregationFunction);
    }

    public SelectFromStatementParams(String measurementID) {
        super(measurementID);
        this.selectedColumns = "*";
        this.aggregationFunction = null;
    }

    public SelectFromStatementParams(String measurementID, String columns, String aggregationFunction) {
        super(measurementID);

        if (columns != null) {
            this.selectedColumns = columns;
        } else {
            this.selectedColumns = "*";
        }

        this.aggregationFunction = aggregationFunction;
    }

    public String getSelectedColumns() {
        return selectedColumns;
    }

    public String getAggregationFunction() {
        return aggregationFunction;
    }
}
