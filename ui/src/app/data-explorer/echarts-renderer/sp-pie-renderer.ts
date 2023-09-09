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

import { DistributionChartWidgetModel } from '../components/widgets/distribution-chart/model/distribution-chart-widget.model';
import { EChartsOption, PieSeriesOption } from 'echarts';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';
import { SpBaseSingleFieldEchartsRenderer } from './sp-base-single-field-echarts-renderer';

export class SpPieRenderer extends SpBaseSingleFieldEchartsRenderer<
    DistributionChartWidgetModel,
    PieSeriesOption
> {
    getType(): string {
        return 'pie';
    }

    addDatasetTransform(
        widgetConfig: DistributionChartWidgetModel,
    ): DataTransformOption {
        const field =
            widgetConfig.visualizationConfig.selectedProperty.fullDbName;
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

    addAdditionalConfigs(option: EChartsOption) {
        // do nothing
    }

    addSeriesItem(
        name: string,
        datasetIndex: number,
        widgetConfig: DistributionChartWidgetModel,
    ): PieSeriesOption {
        return {
            name,
            type: 'pie',
            universalTransition: true,
            datasetIndex: datasetIndex,
            tooltip: {
                formatter: params => {
                    return `${params.marker} ${params.value[0]} <b>${params.value[1]}</b> (${params.percent}%)`;
                },
            },
            label: {
                formatter: params => {
                    return `${params.value[0]} (${params.percent}%)`;
                },
            },
            encode: { itemName: 'name', value: 'value' },
        };
    }

    initialTransforms(
        widgetConfig: DistributionChartWidgetModel,
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

    getAffectedField(widgetConfig: DistributionChartWidgetModel) {
        return widgetConfig.visualizationConfig.selectedProperty;
    }

    showAxes(): boolean {
        return false;
    }

    shouldApplySeriesPosition(): boolean {
        return true;
    }

    getDefaultSeriesName(widgetConfig: DistributionChartWidgetModel): string {
        return widgetConfig.visualizationConfig.selectedProperty.fullDbName;
    }
}
