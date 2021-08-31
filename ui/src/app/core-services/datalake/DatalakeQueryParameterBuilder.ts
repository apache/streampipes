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

import { DatalakeQueryParameters } from './DatalakeQueryParameters';
import { FilterCondition } from '../../data-explorer/components/widgets/pie/model/pie-chart-widget.model';

export class DatalakeQueryParameterBuilder {

  private queryParams: DatalakeQueryParameters;

  static create(startTime?: number,
                endTime?: number): DatalakeQueryParameterBuilder {

    return new DatalakeQueryParameterBuilder(startTime, endTime);
  }


  private constructor(startTime?: number,
                      endTime?: number) {
    this.queryParams = new DatalakeQueryParameters();
    this.queryParams.startDate = startTime;
    this.queryParams.endDate = endTime;
  }

  public withCountOnly(): DatalakeQueryParameterBuilder {
    this.queryParams.countOnly = true;

    return this;
  }

  public withAutoAggregation(aggregationFunction: string) {
    this.queryParams.autoAggregate = true;
    this.queryParams.aggregationFunction = aggregationFunction;

    return this;
  }

  public withAggregationFunction(aggregationFunction: string) {
    this.queryParams.aggregationFunction = aggregationFunction;

    return this;
  }

  public withAggregation(aggregationFunction: string,
                         aggregationTimeUnit: string,
                         aggregationTimeValue: number) {
    this.queryParams.aggregationFunction = aggregationFunction;
    this.queryParams.timeInterval = aggregationTimeValue + aggregationTimeUnit;

    return this;
  }

  public withGrouping(groupBy: string,
                      aggregationFunction: string,
                      aggregationTimeUnit: string,
                      aggregationTimeValue: number): DatalakeQueryParameterBuilder {

    this.queryParams.groupBy = groupBy;
    this.queryParams.aggregationFunction = aggregationFunction;
    this.queryParams.timeInterval = aggregationTimeValue + aggregationTimeUnit;

    return this;
  }

  public withPaging(page: number,
                    limit: number): DatalakeQueryParameterBuilder {
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

  public withOffset(offset: number,
                    limit: number): DatalakeQueryParameterBuilder {
    this.queryParams.offset = offset;
    this.queryParams.limit = limit;

    return this;
  }

  public withColumnFilter(columns: string[]): DatalakeQueryParameterBuilder {
    this.queryParams.columns = columns.toString();

    return this;
  }

  public withFilters(filterConditions: FilterCondition[]): DatalakeQueryParameterBuilder {
    const filters = filterConditions.map(f => '[' + f.field + ',' + f.operator + ',' + f.condition + ']');
    this.queryParams.filter = filters.toString();

    return this;
  }

  public build(): DatalakeQueryParameters {
    return this.queryParams;
  }
}
