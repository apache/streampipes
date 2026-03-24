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
    BarSeriesOption,
    EChartsOption,
    LineSeriesOption,
    ScatterSeriesOption,
} from 'echarts';
import type { SeriesOption } from 'echarts/types/src/util/types.d.ts';
import { Injectable } from '@angular/core';
import { TimeSeriesChartWidgetModel } from './model/time-series-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpBaseEchartsRenderer } from '../../../echarts-renderer/base-echarts-renderer';
import { GeneratedDataset, WidgetSize } from '../../../models/dataset.model';
import {
    AxisConfig,
    WidgetBaseAppearanceConfig,
} from '../../../models/dataview-dashboard.model';
import type { ToolboxFeatureOption } from 'echarts/types/src/component/toolbox/featureManager.d.ts';
import type { ToolboxDataZoomFeatureOption } from 'echarts/types/src/component/toolbox/feature/DataZoom.d.ts';
import { XAXisOption, YAXisOption } from 'echarts/types/dist/shared';
import type { CartesianAxisPosition } from 'echarts/types/src/coord/cartesian/AxisModel.d.ts';
import type { FieldUpdateInfo } from '../../../models/field-update.model';

@Injectable({ providedIn: 'root' })
export class SpTimeseriesRendererService extends SpBaseEchartsRenderer<TimeSeriesChartWidgetModel> {
    applyOptions(
        generatedDataset: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: TimeSeriesChartWidgetModel,
        widgetSize: WidgetSize,
    ): void {
        this.addAxisOptions(widgetConfig, options, widgetSize);
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

        this.addDataZoomOptions(widgetConfig, options);
        this.applyResponsiveLayoutOptions(options, widgetConfig, widgetSize);

        const showTooltip =
            widgetConfig.baseAppearanceConfig.chartAppearance?.showTooltip;

        Object.assign(options, {
            series: finalSeries,
            dataset:
                this.datasetUtilsService.toEChartsDataset(generatedDataset),
            axisPointer: {
                show: widgetConfig.visualizationConfig.showSpike,
            },
            tooltip: {
                show: showTooltip,
                trigger: 'axis',
                axisPointer: {
                    type: 'cross',
                },
            },
        });
    }

    public handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: TimeSeriesChartWidgetModel,
    ): void {
        widgetConfig.visualizationConfig.selectedTimeSeriesChartProperties =
            this.fieldUpdateService.updateFieldSelection(
                widgetConfig.visualizationConfig
                    .selectedTimeSeriesChartProperties,
                fieldUpdateInfo,
                field => field.fieldCharacteristics.numeric,
            );
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
            yAxisIndex: this.getYAxisIndex(
                field,
                widgetConfig.visualizationConfig.chosenAxis,
            ),
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
        } as LineSeriesOption | BarSeriesOption | ScatterSeriesOption;
        if (seriesType === 'line') {
            this.appendLineOptions(
                series as LineSeriesOption,
                widgetConfig,
                field,
            );
        } else if (seriesType === 'scatter') {
            this.appendScatterOptions(series as ScatterSeriesOption);
        }
        return series;
    }

    private makeSeriesType(
        displayTypes: Record<string, string>,
        field: DataExplorerField,
    ): 'bar' | 'line' | 'scatter' {
        const type = this.getDisplayType(displayTypes, field);
        if (type === 'bar') {
            return 'bar';
        } else if (type === 'normal_markers') {
            return 'scatter';
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

    private appendScatterOptions(series: ScatterSeriesOption): void {
        series.symbolSize = 4;
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
        } else if (displayType === 'area') {
            series.showSymbol = false;
            series.areaStyle = {
                opacity: 0.35,
            };
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

    getYAxisIndex(
        field: DataExplorerField,
        axisSettings: Record<string, string>,
    ): number {
        const identifier = field.fullDbName + field.sourceIndex;
        const selectedAxis = axisSettings[identifier];
        if (selectedAxis) {
            return selectedAxis === 'left' ? 0 : 1;
        } else {
            return 0;
        }
    }

    private addDataZoomOptions(
        config: TimeSeriesChartWidgetModel,
        options: EChartsOption,
    ): void {
        Object.assign(options, {
            dataZoom: config.baseAppearanceConfig.dataZoom?.show
                ? {
                      type: config.baseAppearanceConfig.dataZoom?.type,
                  }
                : [],
        });
    }

    private addAxisOptions(
        config: TimeSeriesChartWidgetModel,
        options: EChartsOption,
        widgetSize: WidgetSize,
    ): void {
        const xAxisOption = this.axisGeneratorService.makeAxis(
            'time',
            0,
            config.baseAppearanceConfig as WidgetBaseAppearanceConfig,
        ) as XAXisOption;
        if (xAxisOption.type === 'time') {
            xAxisOption.splitNumber = this.makeResponsiveSplitNumber(
                widgetSize.width,
            );
        }
        xAxisOption.axisLabel = {
            ...xAxisOption.axisLabel,
            hideOverlap: true,
        };

        const yAxisOptions: YAXisOption[] = [];

        const uniqueAxes = new Set(
            Object.values(config.visualizationConfig.chosenAxis).sort((a, b) =>
                a.localeCompare(b),
            ),
        );
        let axisIndex = 0;

        uniqueAxes.forEach(axis => {
            const settings =
                axisIndex === 0
                    ? config.visualizationConfig.leftAxis ||
                      ({ autoScaleActive: true } as AxisConfig)
                    : config.visualizationConfig.rightAxis ||
                      ({ autoScaleActive: true } as AxisConfig);

            yAxisOptions.push({
                type: 'value',
                position: axis as CartesianAxisPosition,
                min: settings.autoScaleActive ? undefined : settings.axisMin,
                max: settings.autoScaleActive ? undefined : settings.axisMax,
            });
            axisIndex++;
        });

        Object.assign(options, {
            xAxis: xAxisOption,
            yAxis: yAxisOptions,
        });
    }

    private applyResponsiveLayoutOptions(
        options: EChartsOption,
        config: TimeSeriesChartWidgetModel,
        widgetSize: WidgetSize,
    ): void {
        const width = widgetSize.width ?? 0;
        const isSmallWidget = width > 0 && width < 700;
        const hasSliderDataZoom =
            config.baseAppearanceConfig.dataZoom?.show &&
            config.baseAppearanceConfig.dataZoom?.type === 'slider';

        const legend =
            !Array.isArray(options.legend) && options.legend
                ? options.legend
                : {};
        const toolbox =
            !Array.isArray(options.toolbox) && options.toolbox
                ? options.toolbox
                : {};

        const showLegend = legend.show ?? true;
        const showToolbox = toolbox.show ?? true;
        const horizontalPadding = isSmallWidget ? 14 : 18;
        const topToolboxTop = 4;
        const toolboxHeight = showToolbox ? 28 : 0;
        const topLegendTop = showToolbox
            ? topToolboxTop + toolboxHeight + 4
            : 6;
        const topLegendHeight = showLegend ? 24 : 0;
        const topControlsBottom = Math.max(
            showToolbox ? topToolboxTop + toolboxHeight : 0,
            showLegend ? topLegendTop + topLegendHeight : 0,
        );
        const gridTop = topControlsBottom > 0 ? topControlsBottom + 8 : 16;

        options.toolbox = {
            ...toolbox,
            show: showToolbox,
            left: 10,
            right: 'auto',
            top: topToolboxTop,
        };

        options.legend = {
            ...legend,
            show: showLegend,
            orient: 'horizontal',
            type: 'scroll',
            left: 'center',
            right: 'auto',
            top: topLegendTop,
            bottom: 'auto',
        };

        options.grid = {
            left: horizontalPadding,
            right: horizontalPadding,
            top: gridTop,
            bottom: hasSliderDataZoom ? 72 : 34,
            containLabel: true,
        };
    }

    private makeResponsiveSplitNumber(width: number): number {
        if (!width || Number.isNaN(width)) {
            return 5;
        }

        const targetPixelPerLabel = 120;
        const minTicks = 2;
        const maxTicks = 12;
        const estimatedTicks = Math.floor(width / targetPixelPerLabel);

        return Math.min(maxTicks, Math.max(minTicks, estimatedTicks));
    }
}
