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

import { BarSeriesOption, EChartsOption, LineSeriesOption } from 'echarts';
import { SeriesOption } from 'echarts/types/src/util/types';
import { Injectable } from '@angular/core';
import { TimeSeriesChartWidgetModel } from './model/time-series-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpBaseEchartsRenderer } from '../../../echarts-renderer/base-echarts-renderer';
import {
    GeneratedDataset,
    TagValue,
    WidgetSize,
} from '../../../models/dataset.model';
import { WidgetBaseAppearanceConfig } from '../../../models/dataview-dashboard.model';
import { ToolboxFeatureOption } from 'echarts/types/src/component/toolbox/featureManager';
import { ToolboxDataZoomFeatureOption } from 'echarts/types/src/component/toolbox/feature/DataZoom';

@Injectable({ providedIn: 'root' })
export class SpTimeseriesRendererService extends SpBaseEchartsRenderer<TimeSeriesChartWidgetModel> {
    applyOptions(
        generatedDataset: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: TimeSeriesChartWidgetModel,
        _widgetSize: WidgetSize,
    ): void {
        const axisOptions = this.axisGeneratorService.makeAxisOptions(
            widgetConfig.baseAppearanceConfig as WidgetBaseAppearanceConfig,
            'time',
            'value',
            1,
        );

        const finalSeries: SeriesOption[] = [];

        widgetConfig.visualizationConfig.selectedTimeSeriesChartProperties.forEach(
            field => {
                const sourceIndex = field.sourceIndex;
                const dataset = this.datasetUtilsService.findPreparedDataset(
                    generatedDataset,
                    sourceIndex,
                );
                for (
                    let i = dataset.meta.preparedDataStartIndex;
                    i <
                    dataset.meta.preparedDataStartIndex +
                        dataset.meta.preparedDataLength;
                    i++
                ) {
                    const rawDatasetDimensions = dataset.rawDataset.dimensions;
                    const groupIndex = i - dataset.meta.preparedDataStartIndex;
                    const tag = dataset.tagValues[groupIndex];
                    const displayName =
                        widgetConfig.visualizationConfig.displayName[
                            field.fullDbName + field.sourceIndex
                        ];
                    const seriesName =
                        dataset.groupedDatasets.length > 0
                            ? this.echartsUtilsService.toTagString(
                                  tag,
                                  displayName,
                              )
                            : displayName;
                    const fieldIndex = rawDatasetDimensions.indexOf(
                        field.fullDbName,
                    );
                    finalSeries.push(
                        this.makeSeries(
                            widgetConfig,
                            i,
                            groupIndex,
                            field,
                            fieldIndex,
                            seriesName,
                        ),
                    );
                }
            },
        );

        Object.assign(options, {
            series: finalSeries,
            dataset:
                this.datasetUtilsService.toEChartsDataset(generatedDataset),
            xAxis: axisOptions.xAxisOptions,
            yAxis: axisOptions.yAxisOptions,
            axisPointer: {
                show: true,
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross',
                },
            },
        });
    }

    makeSeries(
        widgetConfig: TimeSeriesChartWidgetModel,
        datasetIndex: number,
        groupIndex: number,
        field: DataExplorerField,
        fieldIndex: number,
        seriesName: string,
    ): SeriesOption {
        const seriesType = this.makeSeriesType(
            widgetConfig.visualizationConfig.displayType,
            field,
        );
        const color = this.colorizationService.makeColor(
            widgetConfig.visualizationConfig.chosenColor,
            field,
            groupIndex,
        );
        const series = {
            type: seriesType,
            large: true,
            animation: false,
            silent: true,
            color,
            name: seriesName,
            encode: {
                x: 0,
                y: fieldIndex,
            },
            datasetIndex,
        } as LineSeriesOption | BarSeriesOption;
        if (seriesType === 'line') {
            this.appendLineOptions(
                series as LineSeriesOption,
                widgetConfig,
                field,
            );
        }
        return series;
    }

    private makeSeriesType(
        displayTypes: Record<string, string>,
        field: DataExplorerField,
    ) {
        const type = this.getDisplayType(displayTypes, field);
        if (type === 'bar') {
            return 'bar';
        } else {
            return 'line';
        }
    }

    private getDisplayType(
        displayTypes: Record<string, string>,
        field: DataExplorerField,
    ): string {
        return displayTypes[field.fullDbName + field.sourceIndex];
    }

    private appendLineOptions(
        series: LineSeriesOption,
        conf: TimeSeriesChartWidgetModel,
        field: DataExplorerField,
    ) {
        const displayType = this.getDisplayType(
            conf.visualizationConfig.displayType,
            field,
        );
        if (displayType === 'lines') {
            series.showSymbol = false;
        } else if (displayType === 'normal_markers') {
            series.lineStyle = {
                width: 0,
            };
        }
    }

    getAdditionalToolboxItems(): Record<string, ToolboxFeatureOption> {
        return {
            dataZoom: {
                show: true,
                yAxisIndex: false,
            } as ToolboxDataZoomFeatureOption,
            restore: {
                show: true,
            },
        };
    }
}
