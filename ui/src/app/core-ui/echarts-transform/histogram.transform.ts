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

import { bin } from 'd3-array';
import { OptionSourceDataArrayRows } from 'echarts/types/src/util/types';

export interface HistogramConfig extends DataTransformOption {
    field: string;
    autoBin: boolean;
    numberOfBins: number;
    autoDomain: boolean;
    domainMin: number;
    domainMax: number;
}

export const HistogramTransform: ExternalDataTransform<HistogramConfig> = {
    type: 'sp:histogram',

    transform: function (
        params,
    ): ExternalDataTransformResultItem | ExternalDataTransformResultItem[] {
        const upstream = params.upstream;
        const clonedData = upstream.cloneRawData();
        const field = params.config['field'];
        const autoBin = params.config['autoBin'];
        const numberOfBins = +params.config['numberOfBins'];
        const autoDomain = params.config['autoDomain'];
        const domainMin = +params.config['domainMin'];
        const domainMax = +params.config['domainMax'];
        const dimension = upstream.getDimensionInfo(field);
        const values = (clonedData as any).map(row => row[dimension.index]);
        values.shift();

        let bins = bin();
        if (!autoBin && numberOfBins) {
            bins = bins.thresholds(numberOfBins);
        }
        if (!autoDomain && domainMin && domainMax) {
            bins = bins.domain([domainMin, domainMax]);
        }
        const d3h = bins(values);
        const source: OptionSourceDataArrayRows = [];
        const hist: number[] = d3h.map(item => item.length);
        const binEdges = d3h.map(item => item.x0);
        if (d3h.length) {
            binEdges.push(d3h.at(-1).x1);
        }
        hist.forEach((val, index) => {
            source.push([binEdges[index] + '-' + binEdges[index + 1], val]);
        });

        return {
            data: source,
            dimensions: ['edge', 'hist'],
        };
    },
};
