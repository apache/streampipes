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

import type {
    DataTransformOption,
    ExternalDataTransform,
    ExternalDataTransformResultItem,
} from 'echarts/types/src/data/helper/transform.d.ts';
import type { OptionSourceDataArrayRows } from 'echarts/types/src/util/types.d.ts';

export interface PieAggregateConfig extends DataTransformOption {
    field: string;
    topNEnabled: boolean;
    topN: number;
    othersLabel: string;
}

type PieEntry = {
    key: string;
    name: string;
    value: number;
};

export const PieAggregateTransform: ExternalDataTransform<PieAggregateConfig> =
    {
        type: 'sp:pie-aggregate',

        transform: function (
            params,
        ): ExternalDataTransformResultItem | ExternalDataTransformResultItem[] {
            const upstream = params.upstream;
            const field = params.config['field'] as string;
            const topNEnabled = !!params.config['topNEnabled'];
            const topN = Math.max(
                1,
                Math.round(Number(params.config['topN']) || 1),
            );
            const othersLabel =
                (params.config['othersLabel'] as string) || 'Others';
            const dimension = upstream.getDimensionInfo(field);

            if (!dimension) {
                return {
                    data: [],
                    dimensions: ['name', 'value'],
                };
            }

            const rows = upstream.cloneRawData() as OptionSourceDataArrayRows;
            const dimsDef = upstream.cloneAllDimensionInfo();
            const hasHeaderRow =
                rows.length > 0 &&
                Array.isArray(rows[0]) &&
                dimsDef.every((dim, index) => rows[0][index] === dim.name);
            const startIndex = hasHeaderRow ? 1 : 0;

            const grouped = new Map<string, PieEntry>();
            for (let i = startIndex; i < rows.length; i++) {
                const rawValue = rows[i][dimension.index];
                const key =
                    rawValue === null || rawValue === undefined
                        ? '__null__'
                        : String(rawValue);
                const name =
                    rawValue === null || rawValue === undefined
                        ? 'null'
                        : String(rawValue);
                const existing = grouped.get(key);
                if (existing) {
                    existing.value += 1;
                } else {
                    grouped.set(key, { key, name, value: 1 });
                }
            }

            let result = [...grouped.values()];

            if (topNEnabled) {
                const sorted = [...result].sort((a, b) => b.value - a.value);
                const kept = sorted.slice(0, topN);
                const remainder = sorted.slice(topN);
                const remainderSum = remainder.reduce(
                    (sum, item) => sum + item.value,
                    0,
                );
                result = kept;
                if (remainderSum > 0) {
                    result.push({
                        key: '__others__',
                        name: othersLabel,
                        value: remainderSum,
                    });
                }
            }

            return {
                data: result.map(item => [item.name, item.value]),
                dimensions: ['name', 'value'],
            };
        },
    };
