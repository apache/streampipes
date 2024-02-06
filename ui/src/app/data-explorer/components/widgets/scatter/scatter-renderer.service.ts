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
import { EChartsOption } from 'echarts';
import { SpBaseEchartsRenderer } from '../../../echarts-renderer/base-echarts-renderer';
import { CorrelationChartWidgetModel } from '../correlation-chart/model/correlation-chart-widget.model';
import { GeneratedDataset, WidgetSize } from '../../../models/dataset.model';
import { FieldUpdateInfo } from '../../../models/field-update.model';

@Injectable({ providedIn: 'root' })
export class SpScatterRendererService extends SpBaseEchartsRenderer<CorrelationChartWidgetModel> {
    applyOptions(
        generatedDataset: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: CorrelationChartWidgetModel,
        _widgetSize: WidgetSize,
    ): void {
        const xField = this.getXField(widgetConfig);
        const yField = this.getYField(widgetConfig);
        const dataset = this.datasetUtilsService.findPreparedDataset(
            generatedDataset,
            xField.sourceIndex,
        );
        const series = [];
        for (
            let i = 0;
            i <
            dataset.meta.preparedDataStartIndex +
                dataset.meta.preparedDataLength;
            i++
        ) {
            series.push({
                name: dataset.tagValues[
                    i - dataset.meta.preparedDataStartIndex
                ],
                symbolSize: 5,
                datasetIndex: i,
                encode: {
                    x: xField.fullDbName,
                    y: yField.fullDbName,
                    tooltip: [xField.fullDbName, yField.fullDbName],
                },
                type: 'scatter',
            });
        }
        Object.assign(options, {
            dataset:
                this.datasetUtilsService.toEChartsDataset(generatedDataset),
            xAxis: {
                type: 'value',
                min: 'dataMin',
                max: 'dataMax',
            },
            yAxis: {
                type: 'value',
                min: 'dataMin',
                max: 'dataMax',
            },
            series,
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

    getXField(widgetConfig: CorrelationChartWidgetModel) {
        return widgetConfig.visualizationConfig.firstField;
    }

    getYField(widgetConfig: CorrelationChartWidgetModel) {
        return widgetConfig.visualizationConfig.secondField;
    }
}
