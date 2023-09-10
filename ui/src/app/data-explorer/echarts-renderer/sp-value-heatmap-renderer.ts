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
import { EChartsOption, HeatmapSeriesOption } from 'echarts';
import { SpBaseSingleFieldEchartsRenderer } from './sp-base-single-field-echarts-renderer';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';

export class SpValueHeatmapRenderer extends SpBaseSingleFieldEchartsRenderer<
    DistributionChartWidgetModel,
    HeatmapSeriesOption
> {
    getType(): string {
        return 'heatmap';
    }

    addDatasetTransform(
        widgetConfig: DistributionChartWidgetModel,
    ): DataTransformOption {
        const field =
            widgetConfig.visualizationConfig.selectedProperty.fullDbName;
        return {
            type: 'sp:value-distribution',
            config: {
                field,
                resolution: widgetConfig.visualizationConfig.resolution,
            },
        };
    }

    addAdditionalConfigs(options: EChartsOption) {
        options.legend = { show: false };
        options.visualMap = {
            min: 0,
            max: 1,
            calculable: true,
            realtime: false,
            top: '0px',
            right: '50px',
            orient: 'horizontal',
            inRange: {
                color: [
                    '#313695',
                    '#4575b4',
                    '#74add1',
                    '#abd9e9',
                    '#e0f3f8',
                    '#ffffbf',
                    '#fee090',
                    '#fdae61',
                    '#f46d43',
                    '#d73027',
                    '#a50026',
                ],
            },
        };
    }

    addSeriesItem(
        name: string,
        datasetIndex: number,
        widgetConfig: DistributionChartWidgetModel,
        index: number,
    ): HeatmapSeriesOption {
        return {
            universalTransition: true,
            animation: true,
            name: name,
            type: 'heatmap',
            datasetIndex: datasetIndex,
            encode: { itemId: 0, value: 2 },
            xAxisIndex: index,
            yAxisIndex: index,
            tooltip: {
                valueFormatter: value => {
                    if (typeof value === 'number' && isFinite(value)) {
                        return (value * 100).toFixed(3) + '%';
                    } else {
                        return value as string;
                    }
                },
            },
            emphasis: {
                itemStyle: {
                    borderColor: '#333',
                    borderWidth: 1,
                },
            },
        };
    }

    getAffectedField(widgetConfig: DistributionChartWidgetModel) {
        return widgetConfig.visualizationConfig.selectedProperty;
    }
}
