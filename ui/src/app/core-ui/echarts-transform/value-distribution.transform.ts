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
    DataTransformOption,
    ExternalDataTransform,
    ExternalDataTransformResultItem,
} from 'echarts/types/src/data/helper/transform';

export interface ValueDistributionConfig extends DataTransformOption {
    field: string;
    resolution: number;
}

export const ValueDistributionTransform: ExternalDataTransform<ValueDistributionConfig> =
    {
        type: 'sp:value-distribution',

        transform: function (
            params,
        ): ExternalDataTransformResultItem | ExternalDataTransformResultItem[] {
            const upstream = params.upstream;
            const clonedData = upstream.cloneRawData();
            const field = params.config['field'];
            const resolution = +params.config['resolution'];
            const dimension = upstream.getDimensionInfo(field);

            const dataResult = [];
            const allValues = (clonedData as any).map(
                row => row[dimension.index],
            );
            allValues.shift();
            const total = allValues.length;
            let currentCount = 0;
            allValues.sort((a, b) => a - b);
            let start = allValues[0];
            for (let i = 0; i < allValues.length; i++) {
                const value = allValues[i];
                if (value < start + +resolution) {
                    currentCount += 1;
                }
                if (value >= start + resolution || i + 1 === allValues.length) {
                    const currentRange =
                        '>' +
                        start.toFixed(2) +
                        (i + 1 < allValues.length
                            ? '<' + allValues[i + 1].toFixed(2)
                            : '');
                    dataResult.push([currentRange, 0, currentCount / total]);
                    currentCount = 0;
                    start = allValues[i + 1];
                }
            }

            return {
                data: dataResult,
                dimensions: ['category', 'group', field],
            };
        },
    };
