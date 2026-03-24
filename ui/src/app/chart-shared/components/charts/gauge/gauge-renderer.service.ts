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
import { GaugeWidgetModel } from './model/gauge-widget.model';
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

@Injectable({ providedIn: 'root' })
export class SpGaugeRendererService implements SpEchartsRenderer<GaugeWidgetModel> {
    protected fieldUpdateService = inject(SpFieldUpdateService);
    protected echartsBaseOptionsGenerator = inject(
        EchartsBasicOptionsGeneratorService,
    );

    makeSeriesItem(
        seriesName: string,
        fieldName: string,
        value: number,
        decimals: number,
        widgetConfig: GaugeWidgetModel,
        widgetSize: WidgetSize,
        gaugeLayout: GaugeLayout,
    ): GaugeSeriesOption {
        const visConfig = widgetConfig.visualizationConfig;
        const minDimension = Math.min(widgetSize.width, widgetSize.height);
        const clamp = Math.min(Math.max(minDimension / 320, 0.7), 1.4);
        return {
            name: seriesName,
            type: 'gauge',
            center: ['50%', gaugeLayout.centerY],
            radius: gaugeLayout.radius,
            progress: {
                show: true,
            },
            axisLabel: {
                fontSize: 10 * clamp,
            },
            detail: {
                show: true,
                valueAnimation: false,
                formatter: (currentValue: number) =>
                    currentValue.toFixed(decimals),
                fontSize: 14 * clamp,
                offsetCenter: [0, gaugeLayout.detailOffsetY],
            },
            min: visConfig.min,
            max: visConfig.max,
            data: [
                {
                    value: value,
                    name: visConfig.displayName ?? fieldName,
                },
            ],
        };
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
        const decimals = appearanceConfig.numberFormat?.decimals ?? 2;
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
                selectedField.fullDbName,
                data,
                decimals,
                widgetConfig,
                widgetSize,
                gaugeLayout,
            ),
        });

        return option;
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
}

interface GaugeLayout {
    centerY: number;
    radius: number;
    detailOffsetY: number;
}
