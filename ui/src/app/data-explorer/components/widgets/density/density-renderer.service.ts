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
import { SpBaseEchartsRenderer } from '../../../echarts-renderer/base-echarts-renderer';
import { CorrelationChartWidgetModel } from '../correlation-chart/model/correlation-chart-widget.model';
import {
    GeneratedDataset,
    PreparedDataset,
    WidgetSize,
} from '../../../models/dataset.model';
import { EChartsOption } from 'echarts';
import { scaleLinear } from 'd3-scale';
import { extent } from 'd3';
import { contourDensity } from 'd3-contour';
import { DataExplorerField } from '@streampipes/platform-services';
import { FieldUpdateInfo } from '../../../models/field-update.model';

@Injectable({ providedIn: 'root' })
export class SpDensityRendererService extends SpBaseEchartsRenderer<CorrelationChartWidgetModel> {
    applyOptions(
        datasets: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: CorrelationChartWidgetModel,
        widgetSize: WidgetSize,
    ): void {
        const xField = this.getXField(widgetConfig);
        const yField = this.getYField(widgetConfig);
        const dataset = this.datasetUtilsService.findPreparedDataset(
            datasets,
            xField.sourceIndex,
        );

        const data = this.prepareDataset(dataset, xField, yField);
        const stats = this.calculateStats(data);
        const xScale = this.getXScale(data, widgetSize);
        const yScale = this.getYScale(data, widgetSize);

        const contours = this.makeContourDensity(
            xScale,
            yScale,
            widgetSize,
            data,
        );
        const echartsData = this.toEchartsData(xScale, yScale, contours);
        const weightStats = this.getWeightStats(echartsData);

        Object.assign(options, {
            xAxis: {
                scale: true,
                min: Math.floor(stats.minX - 1),
                max: Math.ceil(stats.maxX + 1),
            },
            yAxis: {
                scale: true,
                min: Math.floor(stats.minY - 1),
                max: Math.ceil(stats.maxY + 1),
            },
            dataset: dataset.rawDataset,
            visualMap: {
                show: true,
                right: 'right',
                min: weightStats.min,
                max: weightStats.max,
                dimension: 0,
                seriesIndex: 1,
                calculable: true,
                inRange: {
                    color: ['white', 'yellow', 'red'],
                },
            },
            series: [
                {
                    name: dataset.rawDataset,
                    symbolSize: 5,
                    datasetIndex: 0,
                    encode: {
                        x: xField.fullDbName,
                        y: yField.fullDbName,
                        tooltip: [xField.fullDbName, yField.fullDbName],
                    },
                    type: 'scatter',
                },
                {
                    type: 'custom',
                    tooltip: {
                        show: false,
                    },
                    large: true,
                    silent: true,
                    renderItem: function (params, api) {
                        return {
                            type: 'group',
                            children: echartsData[params.dataIndex][1].map(
                                polygon => {
                                    const points = [];
                                    for (let i = 0; i < polygon.length; i++) {
                                        const point = [
                                            polygon[i][0],
                                            polygon[i][1],
                                        ];
                                        points.push(api.coord(point));
                                    }
                                    return {
                                        type: 'polygon',
                                        shape: {
                                            points: points,
                                        },
                                        style: api.style({
                                            fill: api.visual('color'),
                                            stroke: '#e0e0e0',
                                            lineWidth: 1,
                                        }),
                                    };
                                },
                            ),
                        };
                    },
                    data: echartsData,
                },
            ],
        });
    }

    public handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: CorrelationChartWidgetModel,
    ): void {
        this.fieldUpdateService.updateNumericField(
            widgetConfig.visualizationConfig.firstField,
            fieldUpdateInfo,
        );
        this.fieldUpdateService.updateNumericField(
            widgetConfig.visualizationConfig.secondField,
            fieldUpdateInfo,
        );
    }

    prepareDataset(
        dataset: PreparedDataset,
        xField: DataExplorerField,
        yField: DataExplorerField,
    ): any[] {
        const xIndex = this.getIndex(xField, dataset);
        const yIndex = this.getIndex(yField, dataset);
        const data = (dataset.rawDataset.source as any).map(row => {
            return {
                x: row[xIndex],
                y: row[yIndex],
            };
        });
        data.shift();
        return data;
    }

    calculateStats(data: any[]): {
        minX: number;
        minY: number;
        maxX: number;
        maxY: number;
    } {
        return data.reduce(
            (acc, item) => {
                acc.minX = Math.min(acc.minX, item.x);
                acc.maxX = Math.max(acc.maxX, item.x);
                acc.minY = Math.min(acc.minY, item.y);
                acc.maxY = Math.max(acc.maxY, item.y);
                return acc;
            },
            {
                minX: Infinity,
                maxX: -Infinity,
                minY: Infinity,
                maxY: -Infinity,
            },
        );
    }

    makeContourDensity(
        xScale: any,
        yScale: any,
        widgetSize: WidgetSize,
        data: any[],
    ) {
        return contourDensity()
            .x(d => xScale(d.x))
            .y(d => yScale(d.y))
            .size([widgetSize.width, widgetSize.height])
            .bandwidth(30)
            .thresholds(10)(data);
    }

    toEchartsData(xScale: any, yScale: any, contours: any) {
        return contours.map(contour => {
            return [
                contour.value,
                contour.coordinates.map(ring =>
                    ring[0].map(coord => [
                        xScale.invert(coord[0]),
                        yScale.invert(coord[1]),
                    ]),
                ),
            ];
        });
    }

    getWeightStats(echartsContours: any[]): { min: number; max: number } {
        const xValues = echartsContours.map(item => item[0]);

        const minValue = Math.min(...xValues);
        const maxValue = Math.max(...xValues);

        return { min: minValue, max: maxValue };
    }

    getXScale(data: any[], widgetSize: WidgetSize): any {
        return scaleLinear()
            .domain(extent(data, d => d.x) as [number, number])
            .rangeRound([0, widgetSize.width]);
    }

    getYScale(data: any, widgetSize: WidgetSize): any {
        return scaleLinear()
            .domain(extent(data, d => d.y) as [number, number])
            .rangeRound([widgetSize.height, 0]);
    }

    getIndex(field: DataExplorerField, dataset: PreparedDataset) {
        return dataset.rawDataset.dimensions.indexOf(field.fullDbName);
    }

    getXField(widgetConfig: CorrelationChartWidgetModel) {
        return widgetConfig.visualizationConfig.firstField;
    }

    getYField(widgetConfig: CorrelationChartWidgetModel) {
        return widgetConfig.visualizationConfig.secondField;
    }
}
