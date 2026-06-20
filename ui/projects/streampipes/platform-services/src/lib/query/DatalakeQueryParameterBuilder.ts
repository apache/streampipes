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
    FilterExpressionCondition,
    FilterExpressionGroup,
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
        this.queryParams = {};
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

    public withFill(fill: string | number): DatalakeQueryParameterBuilder {
        this.queryParams.fill = fill;

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
        const validFilters = filterConditions.filter(filter =>
            this.isValidFilter(filter),
        );
        const hasOrConnector = validFilters.some(
            (filter, index) => index > 0 && filter.chainingOperator === 'OR',
        );

        if (hasOrConnector) {
            this.queryParams.filterExpression = JSON.stringify(
                this.buildChainedExpression(validFilters),
            );
            delete this.queryParams.filter;
            return this;
        }

        const filters = validFilters.map(
            filter =>
                '[' +
                filter.field!.runtimeName +
                ';' +
                filter.operator +
                ';' +
                filter.value +
                ']',
        );

        if (filters.length > 0) {
            this.queryParams.filter = filters.toString();
        }

        return this;
    }

    public withFilterExpression(
        filterExpression: FilterExpressionGroup,
    ): DatalakeQueryParameterBuilder {
        this.queryParams.filterExpression = JSON.stringify(
            this.normalizeExpressionGroup(filterExpression),
        );
        delete this.queryParams.filter;

        return this;
    }

    private buildChainedExpression(
        filterConditions: SelectedFilter[],
    ): FilterExpressionGroup {
        let expression: FilterExpressionCondition | FilterExpressionGroup =
            this.toExpressionCondition(filterConditions[0]);

        for (let i = 1; i < filterConditions.length; i++) {
            const currentFilter = filterConditions[i];
            expression = {
                type: 'group',
                operator: currentFilter.chainingOperator ?? 'AND',
                children: [
                    expression,
                    this.toExpressionCondition(currentFilter),
                ],
            };
        }

        if (expression.type === 'condition') {
            return {
                type: 'group',
                operator: 'AND',
                children: [expression],
            };
        }

        return expression;
    }

    private toExpressionCondition(
        filter: SelectedFilter,
    ): FilterExpressionCondition {
        return {
            type: 'condition',
            field: filter.field!.runtimeName,
            operator: filter.operator,
            condition: this.normalizeConditionValue(filter.value),
        };
    }

    private normalizeConditionValue(value: any): any {
        if (
            typeof value === 'string' &&
            value.startsWith('"') &&
            value.endsWith('"') &&
            value.length >= 2
        ) {
            return value.substring(1, value.length - 1);
        }

        if (typeof value === 'string') {
            if (value.toLowerCase() === 'true') {
                return true;
            }

            if (value.toLowerCase() === 'false') {
                return false;
            }

            if (value !== '' && !Number.isNaN(Number(value))) {
                return Number(value);
            }
        }

        return value;
    }

    private normalizeExpressionGroup(
        group: FilterExpressionGroup,
    ): FilterExpressionGroup {
        return {
            type: 'group',
            operator: group.operator ?? 'AND',
            children: group.children.map(child =>
                child.type === 'group'
                    ? this.normalizeExpressionGroup(child)
                    : {
                          type: 'condition',
                          field: child.field,
                          operator: child.operator,
                          condition: this.normalizeConditionValue(
                              child.condition,
                          ),
                      },
            ),
        };
    }

    private isValidFilter(filter: SelectedFilter): boolean {
        const hasValue =
            filter.value !== undefined &&
            filter.value !== null &&
            filter.value !== '';
        return !!filter.field && !!filter.operator && hasValue;
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
