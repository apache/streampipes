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

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DatalakeRestService } from '../apis/datalake-rest.service';
import {
    DataExplorerDataConfig,
    SourceConfig,
} from '../model/datalake/data-lake-query-config.model';
import { SpQueryResult } from '../model/gen/streampipes-model';
import { DatalakeQueryParameters } from '../model/datalake/DatalakeQueryParameters';
import { DatalakeQueryParameterBuilder } from './DatalakeQueryParameterBuilder';

@Injectable({
    providedIn: 'root',
})
export class DataViewQueryGeneratorService {
    constructor(protected dataLakeRestService: DatalakeRestService) {}

    generateObservables(
        startTime: number,
        endTime: number,
        dataConfig: DataExplorerDataConfig,
        maximumResultingEvents: number = -1,
    ): Observable<SpQueryResult>[] {
        return dataConfig.sourceConfigs.map(sourceConfig => {
            const dataLakeConfiguration = this.generateQuery(
                startTime,
                endTime,
                sourceConfig,
                dataConfig.ignoreMissingValues,
                maximumResultingEvents,
            );

            return this.dataLakeRestService.getData(
                sourceConfig.measureName,
                dataLakeConfiguration,
            );
        });
    }

    generateQuery(
        startTime: number,
        endTime: number,
        sourceConfig: SourceConfig,
        ignoreEventsWithMissingValues: boolean,
        maximumResultingEvents: number = -1,
    ): DatalakeQueryParameters {
        const queryBuilder = DatalakeQueryParameterBuilder.create(
            startTime,
            endTime,
        );
        const queryConfig = sourceConfig.queryConfig;

        queryBuilder.withColumnFilter(
            queryConfig.fields.filter(f => f.selected),
            sourceConfig.queryType === 'aggregated' ||
                sourceConfig.queryType === 'single',
        );

        if (sourceConfig.queryConfig.groupBy !== undefined) {
            const selectedGroupByFields =
                sourceConfig.queryConfig.groupBy.filter(
                    field => field.selected === true,
                );
            if (selectedGroupByFields.length > 0) {
                queryBuilder.withGrouping(selectedGroupByFields);
            }
        }

        if (queryConfig.selectedFilters.length > 0) {
            queryBuilder.withFilters(queryConfig.selectedFilters);
        }

        if (queryConfig.order) {
            queryBuilder.withOrdering(queryConfig.order);
        }

        if (sourceConfig.queryType === 'single') {
            queryBuilder.withLimit(1);
        } else if (sourceConfig.queryType === 'raw') {
            // raw query with paging
            queryBuilder.withPaging(queryConfig.page - 1, queryConfig.limit);
        } else {
            // aggregated query
            if (queryConfig.autoAggregate) {
                queryBuilder.withAutoAggregation();
            } else {
                queryBuilder.withAggregation(
                    queryConfig.aggregationTimeUnit,
                    queryConfig.aggregationValue,
                );
            }
        }

        if (ignoreEventsWithMissingValues) {
            queryBuilder.withMissingValueBehaviour('ignore');
        } else {
            queryBuilder.withMissingValueBehaviour('empty');
        }

        const dataLakeQueryParameter = queryBuilder.build();

        if (maximumResultingEvents !== -1) {
            queryBuilder.withMaximumAmountOfEvents(maximumResultingEvents);
        }

        return dataLakeQueryParameter;
    }
}
