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

import { SpBaseEchartsRenderer } from '../../../echarts-renderer/base-echarts-renderer';
import { StatusHeatmapWidgetModel } from './model/status-heatmap-widget.model';
import { GeneratedDataset, TagValue } from '../../../models/dataset.model';
import { EChartsOption } from 'echarts';
import {
    DimensionDefinitionLoose,
    OptionDataValue,
    OptionSourceDataArrayRows,
} from 'echarts/types/src/util/types';
import { Injectable } from '@angular/core';
import { FieldUpdateInfo } from '../../../models/field-update.model';
import { ColorMappingService } from '../../../services/color-mapping.service';

@Injectable({ providedIn: 'root' })
export class SpStatusHeatmapRendererService extends SpBaseEchartsRenderer<StatusHeatmapWidgetModel> {
    constructor(private colorMappingService: ColorMappingService) {
        super();
    }
    applyOptions(
        generatedDataset: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: StatusHeatmapWidgetModel,
    ): void {
        this.basicOptions(options);

        const field = widgetConfig.visualizationConfig.selectedProperty;
        const sourceIndex = field.sourceIndex;

        const rawDataset = this.datasetUtilsService.findPreparedDataset(
            generatedDataset,
            sourceIndex,
        );
        const rawDatasetSource: OptionSourceDataArrayRows = rawDataset
            .rawDataset.source as OptionSourceDataArrayRows;
        const tags = rawDataset.tagValues;
        const statusIndex = rawDataset.rawDataset.dimensions.indexOf(
            field.fullDbName,
        );

        const colorMapping =
            widgetConfig.visualizationConfig.colorMappingsStatusHeatmap;

        rawDatasetSource.shift();
        rawDatasetSource.sort((a, b) => {
            return new Date(a[0]).getTime() - new Date(b[0]).getTime();
        });

        const uniqueValues = [
            ...new Set(rawDatasetSource.map(row => row[statusIndex])),
        ];
        const valueMapping = new Map(
            uniqueValues.map((val, index) => [val, index]),
        );

        const transformedDataset = rawDatasetSource.map((row, index) => {
            let statusValue = row[statusIndex];

            if (typeof statusValue === 'boolean') {
                statusValue = statusValue ? 1 : 0;
            } else if (
                typeof statusValue === 'string' ||
                typeof statusValue === 'number'
            ) {
                statusValue = valueMapping.get(statusValue) ?? null;
            }

            return [
                index,
                this.makeTag(rawDataset.rawDataset.dimensions, tags, row),
                statusValue,
            ];
        });

        options.dataset = { source: transformedDataset };

        (options.xAxis as any).data = rawDatasetSource.map(s => {
            return new Date(s[0]).toLocaleString();
        });

        options.tooltip = {
            formatter: params => {
                const timestamp = rawDatasetSource[params.data[0]][0];
                const statusValue = params.value[2];
                const originalValue = uniqueValues[statusValue];

                const statusLabel =
                    colorMapping.find(c => c.value === originalValue.toString())
                        ?.label || originalValue;

                return `${params.marker} ${new Date(timestamp).toLocaleString()}<br/>Status: <b>${statusLabel}</b>`;
            },
        };

        const dynamicPieces = uniqueValues.map((val, index) => ({
            value: index,
            label:
                colorMapping.find(c => c.value === val.toString())?.label ||
                val.toString(),
            color:
                colorMapping.find(c => c.value === val.toString())?.color ||
                this.colorMappingService.getDefaultColor(index),
        }));

        options.visualMap = {
            type: 'piecewise',
            pieces: dynamicPieces,
            orient: 'horizontal',
            right: '5%',
            top: '20',
        };

        options.legend = {
            type: 'scroll',
        };

        options.series = [
            {
                name: '',
                type: 'heatmap',
                datasetIndex: 0,
                encode: {
                    itemId: 0,
                    value: 2,
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

    public handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: StatusHeatmapWidgetModel,
    ): void {
        this.fieldUpdateService.updateAnyField(
            widgetConfig.visualizationConfig.selectedProperty,
            fieldUpdateInfo,
        );
    }

    basicOptions(options: EChartsOption): void {
        options.grid = {
            height: '80%',
            top: '80',
        };
        options.xAxis = {
            type: 'category',
            splitArea: { show: true },
        };
        options.yAxis = {
            type: 'category',
            splitArea: { show: true },
        };
    }

    private makeTag(
        dimensions: DimensionDefinitionLoose[],
        tags: TagValue[],
        row: Array<OptionDataValue>,
    ) {
        if (tags.length > 0) {
            return tags[0].tagKeys
                .map(key => {
                    const index = dimensions.indexOf(key);
                    return row[index];
                })
                .toString();
        }
    }
}
