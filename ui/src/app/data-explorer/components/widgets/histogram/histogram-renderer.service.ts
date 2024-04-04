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

import { BarSeriesOption, EChartsOption } from 'echarts';
import { SpBaseSingleFieldEchartsRenderer } from '../../../echarts-renderer/base-single-field-echarts-renderer';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';
import { Injectable } from '@angular/core';
import { DataExplorerField } from '@streampipes/platform-services';
import { HistogramChartWidgetModel } from './model/histogram-chart-widget.model';
import { FieldUpdateInfo } from '../../../models/field-update.model';

@Injectable({ providedIn: 'root' })
export class SpHistogramRendererService extends SpBaseSingleFieldEchartsRenderer<
    HistogramChartWidgetModel,
    BarSeriesOption
> {
    addAdditionalConfigs(option: EChartsOption) {
        //do nothing
    }

    public handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: HistogramChartWidgetModel,
    ): void {
        this.fieldUpdateService.updateAnyField(
            widgetConfig.visualizationConfig.selectedProperty,
            fieldUpdateInfo,
        );
    }

    addDatasetTransform(
        widgetConfig: HistogramChartWidgetModel,
    ): DataTransformOption {
        return {
            type: 'sp:histogram',
            config: {
                field: widgetConfig.visualizationConfig.selectedProperty
                    .fullDbName,
                autoBin: widgetConfig.visualizationConfig.autoBin,
                numberOfBins: widgetConfig.visualizationConfig.numberOfBins,
                autoDomain: widgetConfig.visualizationConfig.autoDomain,
                domainMin: widgetConfig.visualizationConfig.domainMin,
                domainMax: widgetConfig.visualizationConfig.domainMax,
            },
        };
    }

    addSeriesItem(
        name: string,
        datasetIndex: number,
        widgetConfig: HistogramChartWidgetModel,
        index: number,
    ): BarSeriesOption {
        return {
            name,
            type: 'bar',
            universalTransition: true,
            datasetIndex: datasetIndex,
            xAxisIndex: index,
            yAxisIndex: index,
            encode: { x: 'edge', y: 'hist' },
            barWidth: '99.3%',
        };
    }

    getSelectedField(
        widgetConfig: HistogramChartWidgetModel,
    ): DataExplorerField {
        return widgetConfig.visualizationConfig.selectedProperty;
    }

    getYAxisType(): 'value' | 'category' | 'time' | 'log' {
        return 'value';
    }

    getDefaultSeriesName(widgetConfig: HistogramChartWidgetModel): string {
        return widgetConfig.visualizationConfig.selectedProperty.fullDbName;
    }
}
