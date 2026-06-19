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

import { inject, Injectable } from '@angular/core';
import { GaugeVisConfig, GaugeWidgetModel } from './model/gauge-widget.model';
import { EChartsOption, GaugeSeriesOption } from 'echarts';
import { FieldUpdateInfo } from '../../../models/field-update.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';
import {
    SpEchartsRenderer,
    WidgetEchartsAppearanceConfig,
} from '../../../models/dataview-dashboard.model';
import { WidgetSize } from '../../../models/dataset.model';
import { EchartsBasicOptionsGeneratorService } from '../../../echarts-renderer/echarts-basic-options-generator.service';
import { SpFieldUpdateService } from '../../../services/field-update.service';
import { ResultLabelService } from '../../../services/result-label.service';

@Injectable({ providedIn: 'root' })
export class SpGaugeRendererService implements SpEchartsRenderer<GaugeWidgetModel> {
    protected fieldUpdateService = inject(SpFieldUpdateService);
    protected echartsBaseOptionsGenerator = inject(
        EchartsBasicOptionsGeneratorService,
    );
    protected resultLabelService = inject(ResultLabelService);

    makeSeriesItem(
        seriesName: string,
        selectedField: DataExplorerField,
        value: number,
        decimals: number | undefined,
        widgetConfig: GaugeWidgetModel,
        widgetSize: WidgetSize,
        gaugeLayout: GaugeLayout,
    ): GaugeSeriesOption {
        const visConfig = widgetConfig.visualizationConfig;
        const clamp = this.getSizeClamp(widgetSize);
        const useThresholdColors = !!visConfig.enableThresholdColors;
        const displayName = this.resultLabelService.resolveLabel(
            widgetConfig.dataConfig.sourceConfigs[selectedField.sourceIndex]
                .queryConfig,
            selectedField,
            this.makeDisplayName(
                visConfig.displayName,
                selectedField.fullDbName,
            ),
        );

        const series: GaugeSeriesOption = {
            name: seriesName,
            type: 'gauge',
            center: ['50%', gaugeLayout.centerY],
            radius: gaugeLayout.radius,
            startAngle: this.toFiniteNumber(visConfig.startAngle, 225),
            endAngle: this.toFiniteNumber(visConfig.endAngle, -45),
            splitNumber: this.normalizeSplitNumber(visConfig.splitNumber),
            pointer: {
                show: visConfig.showPointer,
            },
            progress: {
                show: !useThresholdColors,
            },
            axisLabel: {
                fontSize: 10 * clamp,
            },
            detail: {
                show: true,
                valueAnimation: false,
                formatter: (currentValue: number) =>
                    this.formatNumber(currentValue, decimals),
                fontSize: 14 * clamp,
                offsetCenter: [0, gaugeLayout.detailOffsetY],
            },
            min: visConfig.min,
            max: visConfig.max,
            data: [
                {
                    value: value,
                    name: displayName,
                },
            ],
        };

        if (useThresholdColors) {
            series.axisLine = {
                lineStyle: {
                    color: this.makeThresholdSegments(visConfig),
                },
            };
            series.pointer = {
                ...series.pointer,
                itemStyle: {
                    color: 'auto',
                },
            };
        }

        return series;
    }

    getSelectedField(widgetConfig: GaugeWidgetModel): DataExplorerField {
        return widgetConfig.visualizationConfig.selectedProperty;
    }

    handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: GaugeWidgetModel,
    ): void {
        this.fieldUpdateService.updateAnyField(
            this.getSelectedField(widgetConfig),
            fieldUpdateInfo,
        );
    }

    render(
        queryResult: SpQueryResult[],
        widgetConfig: GaugeWidgetModel,
        widgetSize: WidgetSize,
    ): EChartsOption {
        const option = this.echartsBaseOptionsGenerator.makeBaseConfig(
            widgetConfig.baseAppearanceConfig as WidgetEchartsAppearanceConfig,
            {},
        );
        const appearanceConfig =
            widgetConfig.baseAppearanceConfig as WidgetEchartsAppearanceConfig;
        const decimals = this.normalizeDecimals(
            appearanceConfig.numberFormat?.decimals,
        );
        const selectedField = this.getSelectedField(widgetConfig);
        const sourceIndex = selectedField.sourceIndex;
        const dataSeries = queryResult[sourceIndex].allDataSeries[0];
        const columnIndex = dataSeries.headers.indexOf(
            selectedField.fullDbName,
        );
        const value = Number(dataSeries.rows[0][columnIndex]);
        const data = Number.isFinite(value) ? value : 0;
        const legend =
            !Array.isArray(option.legend) && option.legend ? option.legend : {};
        const toolbox =
            !Array.isArray(option.toolbox) && option.toolbox
                ? option.toolbox
                : {};
        const showLegend = false;
        const showToolbox = toolbox.show ?? true;
        const gaugeLayout = this.makeGaugeLayout(
            widgetSize,
            showToolbox,
            showLegend,
        );

        Object.assign(option, {
            toolbox: {
                ...toolbox,
                left: 10,
                right: 'auto',
                top: 4,
                show: showToolbox,
            },
            legend: {
                ...legend,
                show: showLegend,
            },
            series: this.makeSeriesItem(
                '',
                selectedField,
                data,
                decimals,
                widgetConfig,
                widgetSize,
                gaugeLayout,
            ),
        });

        return option;
    }

    private formatNumber(value: number, decimals?: number): string {
        if (decimals === undefined) {
            return String(value);
        }

        return value.toFixed(decimals);
    }

    private normalizeDecimals(decimals: unknown): number | undefined {
        if (decimals === null || decimals === undefined || decimals === '') {
            return undefined;
        }

        const parsedValue = Number(decimals);
        if (!Number.isFinite(parsedValue)) {
            return undefined;
        }

        return Math.min(10, Math.max(0, Math.round(parsedValue)));
    }

    private makeGaugeLayout(
        widgetSize: WidgetSize,
        showToolbox: boolean,
        showLegend: boolean,
    ): GaugeLayout {
        const topPadding = 8;
        const bottomPadding = 14;
        const toolboxHeight = showToolbox ? 30 : 0;
        const legendHeight = showLegend ? 30 : 0;
        const gap = showToolbox && showLegend ? 6 : 0;
        const topReserved = topPadding + toolboxHeight + gap + legendHeight;

        const availableHeight = Math.max(
            100,
            widgetSize.height - topReserved - bottomPadding,
        );
        const availableWidth = Math.max(100, widgetSize.width - 20);
        const diameter = Math.max(
            90,
            Math.min(availableHeight, availableWidth),
        );
        const radius = Math.round(diameter * 0.46);
        const centerY = topReserved + Math.round(availableHeight / 2);
        const detailOffsetY = Math.round(radius * 0.62);

        return {
            centerY,
            radius,
            detailOffsetY,
        };
    }

    private makeThresholdSegments(
        visConfig: GaugeVisConfig,
    ): Array<[number, string]> {
        const normalizedThresholds = this.normalizeThresholds(visConfig);
        const lowRatio =
            (normalizedThresholds.low - normalizedThresholds.min) /
            normalizedThresholds.range;
        const highRatio =
            (normalizedThresholds.high - normalizedThresholds.min) /
            normalizedThresholds.range;

        return [
            [this.clamp(lowRatio, 0, 1), this.getLowColor(visConfig)],
            [this.clamp(highRatio, 0, 1), this.getMediumColor(visConfig)],
            [1, this.getHighColor(visConfig)],
        ];
    }

    private normalizeThresholds(visConfig: GaugeVisConfig): {
        min: number;
        max: number;
        range: number;
        low: number;
        high: number;
    } {
        const min = this.toFiniteNumber(visConfig.min, 0);
        const configMax = this.toFiniteNumber(visConfig.max, min + 1);
        const max = configMax > min ? configMax : min + 1;
        const range = max - min;

        const lowDefault = min + range * 0.6;
        const highDefault = min + range * 0.8;

        const low = this.clamp(
            this.toFiniteNumber(visConfig.thresholdLow, lowDefault),
            min,
            max,
        );
        const high = this.clamp(
            this.toFiniteNumber(visConfig.thresholdHigh, highDefault),
            min,
            max,
        );

        return low <= high
            ? { min, max, range, low, high }
            : { min, max, range, low: high, high: low };
    }

    private normalizeSplitNumber(splitNumber: number): number {
        return Math.max(1, Math.round(this.toFiniteNumber(splitNumber, 10)));
    }

    private getSizeClamp(widgetSize: WidgetSize): number {
        const minDimension = Math.min(widgetSize.width, widgetSize.height);
        return Math.min(Math.max(minDimension / 320, 0.7), 1.4);
    }

    private clamp(value: number, min: number, max: number): number {
        return Math.min(max, Math.max(min, value));
    }

    private toFiniteNumber(value: unknown, fallback: number): number {
        const parsedValue = Number(value);
        return Number.isFinite(parsedValue) ? parsedValue : fallback;
    }

    private getLowColor(visConfig: GaugeVisConfig): string {
        return visConfig.thresholdColorLow || '#91cc75';
    }

    private getMediumColor(visConfig: GaugeVisConfig): string {
        return visConfig.thresholdColorMedium || '#fac858';
    }

    private getHighColor(visConfig: GaugeVisConfig): string {
        return visConfig.thresholdColorHigh || '#ee6666';
    }

    private makeDisplayName(displayName: unknown, fallback: string): string {
        if (typeof displayName === 'string') {
            return displayName;
        }

        return fallback;
    }
}

interface GaugeLayout {
    centerY: number;
    radius: number;
    detailOffsetY: number;
}
