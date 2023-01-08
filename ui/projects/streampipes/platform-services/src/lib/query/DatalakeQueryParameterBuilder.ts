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

import {
    FieldConfig,
    MissingValueBehaviour,
    SelectedFilter,
} from '../model/datalake/data-lake-query-config.model';
import { DatalakeQueryParameters } from '../model/datalake/DatalakeQueryParameters';

export class DatalakeQueryParameterBuilder {
    private queryParams: DatalakeQueryParameters;

    static create(
        startTime?: number,
        endTime?: number,
    ): DatalakeQueryParameterBuilder {
        return new DatalakeQueryParameterBuilder(startTime, endTime);
    }

    private constructor(startTime?: number, endTime?: number) {
        this.queryParams = new DatalakeQueryParameters();
        if (startTime) {
            this.queryParams.startDate = startTime;
        }
        if (endTime) {
            this.queryParams.endDate = endTime;
        }
    }

    public withMaximumAmountOfEvents(
        maximumAmountOfEvents: number,
    ): DatalakeQueryParameterBuilder {
        this.queryParams.maximumAmountOfEvents = maximumAmountOfEvents;
        return this;
    }

    public withCountOnly(): DatalakeQueryParameterBuilder {
        this.queryParams.countOnly = true;

        return this;
    }

    public withAutoAggregation() {
        this.queryParams.autoAggregate = true;

        return this;
    }

    public withAggregationFunction(aggregationFunction: string) {
        this.queryParams.aggregationFunction = aggregationFunction;

        return this;
    }

    public withAggregation(
        aggregationTimeUnit: string,
        aggregationTimeValue: number,
    ) {
        this.queryParams.timeInterval =
            aggregationTimeValue + aggregationTimeUnit;

        return this;
    }

    public withGrouping(groupBy: FieldConfig[]): DatalakeQueryParameterBuilder {
        const groupByRuntimeNames = groupBy.map(
            property => property.runtimeName,
        );
        this.queryParams.groupBy = groupByRuntimeNames.toString();
        return this;
    }

    public withPaging(
        page: number,
        limit: number,
    ): DatalakeQueryParameterBuilder {
        this.queryParams.page = page;
        this.queryParams.limit = limit;

        return this;
    }

    public withLimit(limit: number): DatalakeQueryParameterBuilder {
        this.queryParams.limit = limit;

        return this;
    }

    public withOrdering(order: string): DatalakeQueryParameterBuilder {
        this.queryParams.order = order;

        return this;
    }

    public withOffset(
        offset: number,
        limit: number,
    ): DatalakeQueryParameterBuilder {
        this.queryParams.offset = offset;
        this.queryParams.limit = limit;

        return this;
    }

    public withColumnFilter(
        columns: FieldConfig[],
        useAggregation: boolean,
    ): DatalakeQueryParameterBuilder {
        const finalColumns = [];
        columns.forEach(column => {
            if (!column.alias && !useAggregation) {
                finalColumns.push(column.runtimeName);
            } else {
                // replace display name, when * is used instead of the name of a single property
                const displayName =
                    column.runtimeName === '*' ? 'all' : column.runtimeName;

                column.aggregations.forEach(agg => {
                    finalColumns.push(
                        '[' +
                            column.runtimeName +
                            ';' +
                            agg +
                            ';' +
                            agg.toLowerCase() +
                            '_' +
                            displayName +
                            ']',
                    );
                });
            }
        });

        this.queryParams.columns = finalColumns.toString();

        return this;
    }

    public withFilters(
        filterConditions: SelectedFilter[],
    ): DatalakeQueryParameterBuilder {
        const filters = [];
        filterConditions.forEach(filter => {
            if (filter.field && filter.value && filter.operator) {
                filters.push(
                    '[' +
                        filter.field.runtimeName +
                        ';' +
                        filter.operator +
                        ';' +
                        filter.value +
                        ']',
                );
            }
        });

        if (filters.length > 0) {
            this.queryParams.filter = filters.toString();
        }

        return this;
    }

    public withMissingValueBehaviour(
        missingValueBehaviour: MissingValueBehaviour,
    ): DatalakeQueryParameterBuilder {
        this.queryParams.missingValueBehaviour = missingValueBehaviour;

        return this;
    }

    public build(): DatalakeQueryParameters {
        return this.queryParams;
    }
}
