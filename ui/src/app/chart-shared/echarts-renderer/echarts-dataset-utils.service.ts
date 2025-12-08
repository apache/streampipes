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
    GeneratedDataset,
    PreparedDataset,
    TagValue,
} from '../models/dataset.model';
import { DatasetOption } from 'echarts/types/dist/shared';

@Injectable({ providedIn: 'root' })
export class EchartsDatasetUtilsService {
    toEChartsDataset(
        generatedDataset: GeneratedDataset,
        sourceIndexFilters: number[] = [],
    ): DatasetOption[] {
        const datasets: DatasetOption[] = [];
        generatedDataset.preparedDatasets.forEach((pd, index) => {
            if (
                sourceIndexFilters.length === 0 ||
                sourceIndexFilters.indexOf(index) > -1
            ) {
                datasets.push(
                    pd.rawDataset,
                    ...pd.initialTransformDatasets,
                    ...pd.groupedDatasets,
                );
            }
        });
        return datasets;
    }

    getTags(datasets: GeneratedDataset, sourceIndex: number): TagValue[] {
        const dataset = this.findPreparedDataset(datasets, sourceIndex);
        return dataset.tagValues;
    }

    findPreparedDataset(
        datasets: GeneratedDataset,
        sourceIndex: number,
    ): PreparedDataset {
        return datasets.preparedDatasets.find(
            p => p.sourceIndex === sourceIndex,
        );
    }
}
