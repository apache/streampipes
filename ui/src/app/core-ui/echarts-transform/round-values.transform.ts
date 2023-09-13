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
import { DataExplorerField } from '@streampipes/platform-services';
import { OptionSourceDataArrayRows } from 'echarts/types/src/util/types';

export interface RoundValuesConfig extends DataTransformOption {
    fields: DataExplorerField[];
    roundingValue: number;
}

export const RoundValuesTransform: ExternalDataTransform<RoundValuesConfig> = {
    type: 'sp:round',

    transform: function (
        params,
    ): ExternalDataTransformResultItem | ExternalDataTransformResultItem[] {
        const fields: string[] = (
            params.config['fields'] as DataExplorerField[]
        )
            .filter(f => f.fieldCharacteristics.numeric)
            .map(f => f.fullDbName);
        const roundingValue = +params.config['roundingValue'];
        const upstream = params.upstream;

        const dimsDef = upstream.cloneAllDimensionInfo();

        const roundColumnIndices = dimsDef
            .filter(dim => fields.indexOf(dim.name) > -1)
            .map(dim => dim.index);

        const data = upstream.cloneRawData() as OptionSourceDataArrayRows;
        const result = data.map((row, index) => {
            if (index == 0) {
                return row;
            } else {
                return row.map((value, index) => {
                    if (roundColumnIndices.indexOf(index) > -1) {
                        return (
                            Math.round((value as number) / roundingValue) *
                            roundingValue
                        );
                    } else {
                        return value;
                    }
                });
            }
        });
        result.shift();
        return {
            dimensions: upstream.cloneAllDimensionInfo(),
            data: result,
        };
    },
};
