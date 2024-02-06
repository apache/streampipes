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
import { OptionSourceDataArrayRows } from 'echarts/types/src/util/types';

export interface MapColumnsConfig extends DataTransformOption {
    fields: string[];
    groupBy: string;
}

export const MapTransform: ExternalDataTransform<MapColumnsConfig> = {
    type: 'sp:map',

    transform: function (
        params,
    ): ExternalDataTransformResultItem | ExternalDataTransformResultItem[] {
        const fields: string[] = params.config['fields'] as string[];
        const groupBy = params.config['groupBy'] as string;
        const upstream = params.upstream;

        if (groupBy !== undefined && fields.length > 1) {
            console.error(
                'Only a single field can be transformed when using groupBy strategy',
            );
        }
        if (!fields || fields.length === 0) {
            console.error(
                'At least one field must be provided to transform values',
            );
        }

        const dimsDef = upstream.cloneAllDimensionInfo();
        const data = upstream.cloneRawData() as OptionSourceDataArrayRows;
        let result: any[][];
        let resultDimensions: string[];

        if (!groupBy) {
            const indices = dimsDef
                .filter(dim => fields.indexOf(dim.name) > -1)
                .map(dim => dim.index);

            result = fields.map(() => []);
            resultDimensions = fields;

            data.forEach(row => {
                indices.forEach((index, fieldIndex) => {
                    result[fieldIndex].push(row[index]);
                });
            });
        } else {
            const groupByIndex = dimsDef.map(d => d.name).indexOf(groupBy);
            const valueIndex = dimsDef.map(d => d.name).indexOf(fields[0]);
            const values = data.map(row => row[groupByIndex]);
            const distinctValues = Array.from(new Set(values));
            resultDimensions = values as string[];
            result = distinctValues.map(() => []);
            data.forEach(row => {
                const key = row[groupByIndex] as any;
                const value = row[valueIndex] as any;
                result[distinctValues.indexOf(key)].push(value);
            });
        }
        return {
            dimensions: resultDimensions,
            data: result,
        };
    },
};
