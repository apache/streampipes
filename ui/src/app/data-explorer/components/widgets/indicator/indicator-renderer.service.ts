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
import { IndicatorChartWidgetModel } from './model/indicator-chart-widget.model';
import { GeneratedDataset, WidgetSize } from '../../../models/dataset.model';
import { EChartsOption, GraphicComponentOption } from 'echarts';
import { FieldUpdateInfo } from '../../../models/field-update.model';

@Injectable({ providedIn: 'root' })
export class SpIndicatorRendererService extends SpBaseEchartsRenderer<IndicatorChartWidgetModel> {
    applyOptions(
        generatedDataset: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: IndicatorChartWidgetModel,
        widgetSize: WidgetSize,
    ): void {
        const field = widgetConfig.visualizationConfig.valueField;
        const datasetOption = this.datasetUtilsService.findPreparedDataset(
            generatedDataset,
            field.sourceIndex,
        ).rawDataset;
        const fieldIndex = datasetOption.dimensions.indexOf(field.fullDbName);
        const datasetSize = datasetOption.source.length as number;
        const value = (datasetOption.source as any)[datasetSize - 1][
            fieldIndex
        ];
        const graphicElements: GraphicComponentOption[] = [];
        let previousValue = undefined;
        graphicElements.push(this.makeCurrentValue(value, widgetConfig));
        if (datasetSize > 1 && widgetConfig.visualizationConfig.showDelta) {
            previousValue = (datasetOption.source as any)[datasetSize - 2][
                fieldIndex
            ];
            graphicElements.push(
                this.makeDelta(value, previousValue, widgetConfig),
            );
        }

        Object.assign(options, {
            graphic: {
                elements: graphicElements,
            },
        });
    }

    public handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: IndicatorChartWidgetModel,
    ): void {
        widgetConfig.visualizationConfig.valueField =
            this.fieldUpdateService.updateSingleField(
                widgetConfig.visualizationConfig.valueField,
                fieldUpdateInfo.fieldProvider.allFields,
                fieldUpdateInfo,
                field => true,
            );
    }

    makeCurrentValue(
        value: any,
        widgetConfig: IndicatorChartWidgetModel,
    ): GraphicComponentOption {
        return {
            type: 'text',
            left: 'center',
            top: '30%',
            style: this.makeTextStyle(value, 80, widgetConfig),
        };
    }

    makeDelta(
        value: any,
        previousValue: any,
        widgetConfig: IndicatorChartWidgetModel,
    ): GraphicComponentOption {
        const delta = value - previousValue;
        return {
            type: 'text',
            left: 'center',
            top: '50%',
            style: this.makeTextStyle(delta, 50, widgetConfig),
        };
    }

    makeTextStyle(
        textContent: any,
        fontSize: number,
        widgetConfig: IndicatorChartWidgetModel,
    ) {
        return {
            text: this.formatValue(textContent),
            fontSize,
            fontWeight: 'bold',
            lineDash: [0, 200],
            lineDashOffset: 0,
            fill: widgetConfig.baseAppearanceConfig.textColor,
            lineWidth: 1,
        };
    }

    formatValue(value: any): any {
        if (typeof value === 'number') {
            return parseFloat(value.toFixed(3));
        } else {
            return value;
        }
    }
}
