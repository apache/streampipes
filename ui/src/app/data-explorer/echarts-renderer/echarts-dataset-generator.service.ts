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
import { GeneratedDataset, Indices, TagValue } from '../models/dataset.model';
import { DatasetOption } from 'echarts/types/dist/shared';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';

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
        const datasets: DatasetOption[] = [];
        let initialTransformsCount = 0;

        const indices: Indices = {
            rawDataStartIndices: [],
            rawDataEndIndices: [],
            preparedDataStartIndices: [],
            preparedDataEndIndices: [],
        };

        queryResults.forEach((queryResult, index) => {
            this.processQueryResult(
                queryResult,
                index,
                datasets,
                indices,
                widgetConfig,
                getInitialTransforms,
            );
        });

        initialTransformsCount = datasets.filter(
            dataset => 'transform' in dataset,
        ).length;

        return {
            dataset: datasets,
            tagValues: this.calculateTagValues(queryResults),
            indices,
            initialTransformsCount,
        };
    }

    private processQueryResult(
        queryResult: SpQueryResult,
        index: number,
        datasets: DatasetOption[],
        indices: Indices,
        widgetConfig: T,
        getInitialTransforms: (
            widgetConfig: T,
            index: number,
        ) => DataTransformOption[],
    ) {
        const initialTransforms = getInitialTransforms(widgetConfig, index);
        const currentDatasetSize = datasets.length;

        const data = [
            queryResult.headers,
            ...queryResult.allDataSeries.flatMap(series => series.rows),
        ];
        datasets.push({ source: data, dimensions: queryResult.headers });

        const transformsStartIndex = datasets.length;
        if (initialTransforms.length > 0) {
            datasets.push(
                ...initialTransforms.map(transform => ({
                    fromDatasetIndex: currentDatasetSize,
                    transform,
                })),
            );
        }

        const tags = this.extractTags(queryResult);
        datasets.push(
            ...tags.map(tag => ({
                fromDatasetIndex: currentDatasetSize + initialTransforms.length,
                transform: this.createTagFilterTransform(tag),
            })),
        );

        const transformsEndIndex = datasets.length;
        this.updateIndices(
            indices,
            currentDatasetSize,
            initialTransforms.length,
            tags.length,
            transformsStartIndex,
            transformsEndIndex,
        );
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

    private updateIndices(
        indices: Indices,
        currentDatasetSize: number,
        initialTransformsLength: number,
        tagsLength: number,
        transformsStartIndex: number,
        transformsEndIndex: number,
    ) {
        indices.rawDataStartIndices.push(
            currentDatasetSize + initialTransformsLength,
        );
        indices.rawDataEndIndices.push(
            currentDatasetSize + initialTransformsLength + tagsLength,
        );
        indices.preparedDataStartIndices.push(transformsStartIndex);
        indices.preparedDataEndIndices.push(transformsEndIndex);
    }

    private calculateTagValues(queryResults: SpQueryResult[]): TagValue[][] {
        return queryResults.map(queryResult => this.extractTags(queryResult));
    }
}
