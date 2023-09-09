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

import { SpBaseEchartsRenderer } from './sp-base-echarts-renderer';
import { HeatmapWidgetModel } from '../components/widgets/heatmap/model/heatmap-widget.model';
import { GeneratedDataset, TagValue } from '../models/dataset.model';
import { EChartsOption } from 'echarts';
import {
    DimensionDefinitionLoose,
    OptionDataValue,
    OptionSourceDataArrayRows,
} from 'echarts/types/src/util/types';

export class SpHeatmapRenderer extends SpBaseEchartsRenderer<HeatmapWidgetModel> {
    getType(): string {
        return 'heatmap';
    }

    applyOptions(
        datasets: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: HeatmapWidgetModel,
    ): void {
        this.basicOptions(options);

        const field = widgetConfig.visualizationConfig.selectedHeatProperty;
        const sourceIndex = field.sourceIndex;

        const rawDataset = datasets.dataset[sourceIndex];
        const rawDatasetSource: OptionSourceDataArrayRows =
            rawDataset.source as OptionSourceDataArrayRows;
        const tags = datasets.tagValues[sourceIndex];
        const heatIndex = rawDataset.dimensions.indexOf(field.fullDbName);
        rawDatasetSource.shift();
        rawDatasetSource.sort((a, b) => {
            const dateA = new Date(a[0]);
            const dateB = new Date(b[0]);
            return dateA.getTime() - dateB.getTime();
        });
        const transformedDataset = (
            rawDataset.source as OptionSourceDataArrayRows
        ).map((row, index) => {
            return [
                index,
                this.makeTag(rawDataset.dimensions, tags, row),
                row[heatIndex],
            ];
        });

        options.dataset = { source: transformedDataset };
        (options.xAxis as any).data = rawDatasetSource.map(s => {
            return new Date(s[0]).toLocaleString();
        });
        options.series = [
            {
                name: '',
                type: 'heatmap',
                datasetIndex: 0,
                encode: {
                    itemId: 0,
                    value: heatIndex,
                },
                label: {
                    show: widgetConfig.visualizationConfig.showLabelsProperty,
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowColor: 'rgba(0, 0, 0, 0.5)',
                    },
                },
            },
        ];
    }

    basicOptions(options: EChartsOption): void {
        options.tooltip = {};
        options.grid = {
            height: '80%',
            top: '80',
        };
        options.xAxis = {
            type: 'category',
            splitArea: {
                show: true,
            },
        };
        options.yAxis = {
            type: 'category',
            splitArea: {
                show: true,
            },
        };
        options.visualMap = {
            calculable: true,
            orient: 'horizontal',
            right: '5%',
            top: '20',
        };
    }

    private makeTag(
        dimensions: DimensionDefinitionLoose[],
        tags: TagValue[],
        row: Array<OptionDataValue>,
    ) {
        if (tags.length > 0) {
            const rowValues = [];
            tags[0].tagKeys.forEach(key => {
                const index = dimensions.indexOf(key);
                rowValues.push(row[index]);
            });
            return rowValues.toString();
        }
    }
}
