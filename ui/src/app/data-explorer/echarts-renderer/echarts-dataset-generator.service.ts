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
import {
    DataExplorerWidgetModel,
    SpQueryResult,
} from '@streampipes/platform-services';
import {
    GeneratedDataset,
    PreparedDataset,
    TagValue,
} from '../models/dataset.model';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';

/**
 * The dataset generator generates Echarts datasets as follows:
 * From the Query API, we get one query result per configured data source.
 * First, we add one dataset for each query result to the datasets array, which are the raw datasets.
 * Then, we apply the initial transforms, which can round values or narrow the available columns.
 * If the result is a grouped result (tags are present), we add one dataset per group.
 * In the end, the final datasets (prior to transforms added by the charts itself) are called preparedDataset
 * Meta information about start and end indices are present in the meta object.
 *
 */

@Injectable({ providedIn: 'root' })
export class EchartsDatasetGeneratorService<T extends DataExplorerWidgetModel> {
    toDataset(
        queryResults: SpQueryResult[],
        widgetConfig: T,
        getInitialTransforms: (
            widgetConfig: T,
            index: number,
        ) => DataTransformOption[],
    ): GeneratedDataset {
        const datasetInfo: GeneratedDataset = {
            preparedDatasets: [],
        };

        let currentDatasetIndexPointer = 0;

        queryResults.forEach((queryResult, sourceIndex) => {
            const prepared = this.processQueryResult(
                queryResult,
                sourceIndex,
                currentDatasetIndexPointer,
                widgetConfig,
                getInitialTransforms,
            );
            datasetInfo.preparedDatasets.push(prepared);
            currentDatasetIndexPointer = this.increaseDatasetIndex(
                prepared,
                currentDatasetIndexPointer,
            );
        });
        return datasetInfo;
    }

    private processQueryResult(
        queryResult: SpQueryResult,
        index: number,
        currentDatasetIndexPointer: number,
        widgetConfig: T,
        getInitialTransforms: (
            widgetConfig: T,
            index: number,
        ) => DataTransformOption[],
    ): PreparedDataset {
        const data = [
            queryResult.headers,
            ...queryResult.allDataSeries.flatMap(series => series.rows),
        ];
        const initialTransforms = getInitialTransforms(widgetConfig, index).map(
            transform => ({
                fromDatasetIndex: currentDatasetIndexPointer,
                transform,
            }),
        );

        const tags = this.extractTags(queryResult);
        const groupTransforms = tags.map(tag => ({
            fromDatasetIndex:
                currentDatasetIndexPointer + initialTransforms.length,
            transform: this.createTagFilterTransform(tag),
        }));

        const preparedDataStartIndex =
            currentDatasetIndexPointer +
            initialTransforms.length +
            (groupTransforms.length > 0 ? 1 : 0);

        const preparedDataLength =
            groupTransforms.length > 0 ? groupTransforms.length : 1;

        return {
            sourceIndex: index,
            tagValues: tags,
            groupedDatasets: groupTransforms,
            rawDataset: { source: data, dimensions: queryResult.headers },
            initialTransformDatasets: initialTransforms,
            meta: {
                preparedDataStartIndex,
                preparedDataLength,
            },
        };
    }

    increaseDatasetIndex(
        preparedDataset: PreparedDataset,
        currentDatasetIndex: number,
    ): number {
        return (
            currentDatasetIndex +
            1 + // raw dataset for each query result
            preparedDataset.initialTransformDatasets.length +
            preparedDataset.groupedDatasets.length +
            1
        ); // next index
    }

    private extractTags(queryResult: SpQueryResult): TagValue[] {
        return queryResult.allDataSeries
            .flatMap(series => series.tags ?? [])
            .map(kv => ({
                tagKeys: Object.keys(kv),
                values: Object.values(kv),
            }));
    }

    private createTagFilterTransform(tag: TagValue): DataTransformOption {
        return {
            type: 'filter',
            config: {
                and: tag.tagKeys.map((key, i) => ({
                    dimension: key,
                    value: tag.values[i],
                })),
            },
        };
    }
}
