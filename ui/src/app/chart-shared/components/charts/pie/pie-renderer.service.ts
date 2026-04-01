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

import { EChartsOption, PieSeriesOption } from 'echarts';
import type { DataTransformOption } from 'echarts/types/src/data/helper/transform.d.ts';
import { SpBaseSingleFieldEchartsRenderer } from '../../../echarts-renderer/base-single-field-echarts-renderer';
import { inject, Injectable } from '@angular/core';
import {
    PieChartVisConfig,
    PieChartWidgetModel,
} from './model/pie-chart-widget.model';
import { FieldUpdateInfo } from '../../../models/field-update.model';
import { ZRColor } from 'echarts/types/dist/shared';
import { ColorMappingService } from '../../../services/color-mapping.service';

@Injectable({ providedIn: 'root' })
export class SpPieRendererService extends SpBaseSingleFieldEchartsRenderer<
    PieChartWidgetModel,
    PieSeriesOption
> {
    colorMappingService = inject(ColorMappingService);

    addDatasetTransform(
        widgetConfig: PieChartWidgetModel,
    ): DataTransformOption {
        const config = this.getSafeConfig(widgetConfig);
        const field = config.selectedProperty.fullDbName;
        const topNEnabled = config.topNEnabled;
        if (!topNEnabled) {
            return {
                type: 'ecSimpleTransform:aggregate',
                config: {
                    resultDimensions: [
                        { name: 'name', from: field },
                        { name: 'value', from: 'time', method: 'count' },
                    ],
                    groupBy: field,
                },
            };
        }

        return {
            type: 'sp:pie-aggregate',
            config: {
                field,
                topNEnabled,
                topN: config.topN,
                othersLabel: config.othersLabel,
            },
        };
    }

    public handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: PieChartWidgetModel,
    ): void {
        this.fieldUpdateService.updateAnyField(
            widgetConfig.visualizationConfig.selectedProperty,
            fieldUpdateInfo,
        );
    }

    addAdditionalConfigs(
        option: EChartsOption,
        widgetConfig: PieChartWidgetModel,
    ): void {
        const config = this.getSafeConfig(widgetConfig);
        if (config.selectedProperty.fieldCharacteristics.binary) {
            option.legend = { show: false };
        } else {
            option.legend = {
                type: 'scroll',
                formatter: name => {
                    return (
                        config.colorMappingsPieChart.find(
                            c => String(c.value) === name,
                        )?.label || name
                    );
                },
            };
        }
        this.applySinglePieResponsiveLayout(option);
    }

    addSeriesItem(
        name: string,
        datasetIndex: number,
        widgetConfig: PieChartWidgetModel,
    ): PieSeriesOption {
        const config = this.getSafeConfig(widgetConfig);
        const innerRadius = config.selectedRadius;
        const colorMapping = config.colorMappingsPieChart;
        const decimals = this.getDecimals(widgetConfig);
        const labelMode = config.labelMode;
        const labelPosition = config.labelPosition;
        const labelAlignTo = config.labelAlignTo;
        const isOutsideLabel = labelPosition === 'outside';
        const outerRadius = isOutsideLabel ? '78%' : '90%';

        return {
            name,
            type: 'pie',
            universalTransition: true,
            datasetIndex: datasetIndex,
            startAngle: config.startAngle,
            clockwise: config.clockwise,
            minAngle: config.minAngle,
            avoidLabelOverlap: config.avoidLabelOverlap,
            tooltip: {
                formatter: params => {
                    const mappedLabel =
                        colorMapping.find(
                            c => c.value === params.value[0]?.toString(),
                        )?.label || params.value[0];
                    return `${params.marker} ${this.formatPieText(
                        mappedLabel,
                        params.value[1],
                        params.percent,
                        labelMode,
                        decimals,
                        true,
                    )}`;
                },
            },
            label: {
                position: labelPosition,
                alignTo: isOutsideLabel ? labelAlignTo : undefined,
                edgeDistance:
                    isOutsideLabel && labelAlignTo === 'edge' ? 8 : undefined,
                bleedMargin: isOutsideLabel ? 4 : undefined,
                overflow: 'truncate',
                formatter: params => {
                    const mappedLabel =
                        colorMapping.find(
                            c => c.value === params.value[0]?.toString(),
                        )?.label || params.value[0];
                    return this.formatPieText(
                        mappedLabel,
                        params.value[1],
                        params.percent,
                        labelMode,
                        decimals,
                    );
                },
            },
            labelLine: {
                show: config.showLabelLine && labelPosition === 'outside',
                length: 8,
                length2: 8,
            },
            encode: { itemName: 'name', value: 'value' },
            radius: [innerRadius + '%', outerRadius],
            itemStyle: {
                color: params => {
                    const category = params.data[0];
                    return (colorMapping.find(
                        c => c.value === category.toString(),
                    )?.color ||
                        this.colorMappingService.getDefaultColor(
                            params.data[0],
                        )) as ZRColor;
                },
            },
        };
    }

    private formatPieText(
        mappedLabel: string,
        rawValue: unknown,
        rawPercent: unknown,
        labelMode: PieChartWidgetModel['visualizationConfig']['labelMode'],
        decimals: number | undefined,
        boldValue = false,
    ): string {
        const value = this.formatNumber(rawValue, decimals);
        const valueText = boldValue ? `<b>${value}</b>` : value;
        const percent =
            typeof rawPercent === 'number'
                ? this.formatNumber(rawPercent, decimals)
                : String(rawPercent ?? '');
        const percentText = `${percent}%`;

        if (labelMode === 'name') {
            return mappedLabel;
        } else if (labelMode === 'value') {
            return valueText;
        } else if (labelMode === 'percent') {
            return percentText;
        } else if (labelMode === 'name_value') {
            return `${mappedLabel}: ${valueText}`;
        } else if (labelMode === 'name_value_percent') {
            return `${mappedLabel}: ${valueText} (${percentText})`;
        } else {
            return `${mappedLabel} (${percentText})`;
        }
    }

    initialTransforms(
        widgetConfig: PieChartWidgetModel,
        sourceIndex: number,
    ): DataTransformOption[] {
        const fieldSource = widgetConfig.visualizationConfig.selectedProperty;
        return fieldSource.sourceIndex === sourceIndex &&
            fieldSource.fieldCharacteristics.numeric
            ? [
                  {
                      type: 'sp:round',
                      config: {
                          fields: [
                              widgetConfig.visualizationConfig.selectedProperty,
                          ],
                          roundingValue:
                              widgetConfig.visualizationConfig.roundingValue,
                      },
                  },
              ]
            : [];
    }

    getSelectedField(widgetConfig: PieChartWidgetModel) {
        return widgetConfig.visualizationConfig.selectedProperty;
    }

    showAxes(): boolean {
        return false;
    }

    shouldApplySeriesPosition(): boolean {
        return true;
    }

    getDefaultSeriesName(widgetConfig: PieChartWidgetModel): string {
        return this.getSafeConfig(widgetConfig).selectedProperty.fullDbName;
    }

    private getSafeConfig(
        widgetConfig: PieChartWidgetModel,
    ): PieChartVisConfig {
        const config = widgetConfig.visualizationConfig;
        return {
            ...config,
            startAngle: Number.isFinite(config.startAngle)
                ? config.startAngle
                : 90,
            clockwise:
                typeof config.clockwise === 'boolean' ? config.clockwise : true,
            minAngle: Number.isFinite(config.minAngle) ? config.minAngle : 0,
            labelMode: config.labelMode || 'name_percent',
            labelPosition: config.labelPosition || 'outside',
            labelAlignTo: config.labelAlignTo || 'edge',
            avoidLabelOverlap:
                typeof config.avoidLabelOverlap === 'boolean'
                    ? config.avoidLabelOverlap
                    : true,
            showLabelLine:
                typeof config.showLabelLine === 'boolean'
                    ? config.showLabelLine
                    : true,
            topNEnabled: !!config.topNEnabled,
            topN:
                Number.isFinite(config.topN) && config.topN > 0
                    ? Math.round(config.topN)
                    : 10,
            othersLabel: config.othersLabel || 'Others',
            colorMappingsPieChart: config.colorMappingsPieChart || [],
            selectedRadius: Number.isFinite(config.selectedRadius)
                ? config.selectedRadius
                : 0,
        };
    }

    private applySinglePieResponsiveLayout(option: EChartsOption): void {
        const pieSeries = Array.isArray(option.series)
            ? option.series
            : option.series
              ? [option.series]
              : [];

        // Keep grouped/tagged pie layout unchanged.
        if (pieSeries.length !== 1) {
            return;
        }

        const legend =
            !Array.isArray(option.legend) && option.legend ? option.legend : {};
        const toolbox =
            !Array.isArray(option.toolbox) && option.toolbox
                ? option.toolbox
                : {};

        const showLegend = legend.show ?? true;
        const showToolbox = toolbox.show ?? true;
        const toolboxTop = 4;
        const toolboxHeight = showToolbox ? 28 : 0;
        const legendTop = showToolbox ? 36 : 6;
        const legendHeight = showLegend ? 24 : 0;
        const topControlsBottom = Math.max(
            showToolbox ? toolboxTop + toolboxHeight : 0,
            showLegend ? legendTop + legendHeight : 0,
        );
        const pieTop = topControlsBottom > 0 ? topControlsBottom + 8 : 8;
        const pieBottom = 8;

        option.toolbox = {
            ...toolbox,
            show: showToolbox,
            left: 10,
            right: 'auto',
            top: toolboxTop,
        };
        option.legend = {
            ...legend,
            show: showLegend,
            type: 'scroll',
            left: showToolbox ? 120 : 10,
            right: 10,
            top: legendTop,
            bottom: 'auto',
        };

        const singlePieSeries = pieSeries[0] as PieSeriesOption;
        delete singlePieSeries.width;
        delete singlePieSeries.height;
        singlePieSeries.left = 10;
        singlePieSeries.right = 10;
        singlePieSeries.top = pieTop;
        singlePieSeries.bottom = pieBottom;
        singlePieSeries.center = ['50%', '50%'];
    }
}
