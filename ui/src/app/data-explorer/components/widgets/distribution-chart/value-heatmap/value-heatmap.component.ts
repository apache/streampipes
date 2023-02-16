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

import { Component, Input } from '@angular/core';
import { EChartsOption } from 'echarts';
import { ECharts } from 'echarts/core';
import { SpQueryResult } from '@streampipes/platform-services';
import { DistributionChartWidgetModel } from '../model/distribution-chart-widget.model';

@Component({
    selector: 'sp-data-explorer-value-heatmap-widget',
    templateUrl: './value-heatmap.component.html',
    styleUrls: ['./value-heatmap.component.scss'],
})
export class SpValueHeatmapComponent {
    currentWidth_: number;
    currentHeight_: number;
    data_: SpQueryResult[];

    @Input()
    widgetConfig: DistributionChartWidgetModel;

    configReady = true;
    option: EChartsOption = {
        grid: {
            height: '80%',
            top: '7%',
        },
        tooltip: {},
        xAxis: {
            type: 'category',
            data: [],
        },
        yAxis: {
            type: 'category',
            data: [],
        },
        visualMap: {
            min: 0,
            max: 1,
            calculable: true,
            realtime: false,
            top: '10px',
            left: '10px',
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
        },
        series: [
            {
                name: 'Gaussian',
                type: 'heatmap',
                data: [],
                emphasis: {
                    itemStyle: {
                        borderColor: '#333',
                        borderWidth: 1,
                    },
                },
                animation: false,
            },
        ],
    };

    dynamic: EChartsOption;

    eChartsInstance: ECharts;

    onChartInit(ec: ECharts) {
        this.eChartsInstance = ec;
        this.applySize(this.currentWidth_, this.currentHeight_);
        this.initOptions();
    }

    initOptions() {
        if (this.data_) {
            const dataResult = [];
            const resolution: number =
                +this.widgetConfig.visualizationConfig.resolution;
            const allRows = this.data_[0].allDataSeries[0].rows;
            const fieldIndex = this.getFieldIndex(
                this.data_[0].allDataSeries[0].headers,
            );
            const allValues = allRows.map(row => row[fieldIndex]);
            const total = allValues.length;
            let currentCount = 0;
            allValues.sort((a, b) => a - b);
            let start = allValues[0];
            for (let i = 0; i < allValues.length; i++) {
                const value = allValues[i];
                if (value < start + resolution) {
                    currentCount += 1;
                }
                if (value >= start + resolution || i + 1 === allValues.length) {
                    const currentRange =
                        '>' +
                        start.toFixed(2) +
                        (i + 1 < allValues.length
                            ? '<' + allValues[i + 1].toFixed(2)
                            : '');
                    dataResult.push([
                        currentRange,
                        0,
                        (currentCount + 1) / total,
                    ]);
                    currentCount = 0;
                    start = allValues[i + 1];
                }
            }
            this.dynamic = this.option;
            (this.dynamic.xAxis as any).data = dataResult.map(r => r[0]);
            this.dynamic.series[0].data = dataResult;
            this.dynamic.series[0].name = this.data_[0].headers[fieldIndex];
            if (this.eChartsInstance) {
                this.eChartsInstance.setOption(this.dynamic);
            }
        }
    }

    getFieldIndex(headers: string[]) {
        return headers.indexOf(
            this.widgetConfig.visualizationConfig.selectedProperty.fullDbName,
        );
    }

    applySize(width: number, height: number) {
        if (this.eChartsInstance) {
            this.eChartsInstance.resize({ width, height });
        }
    }

    @Input()
    set currentWidth(currentWidth: number) {
        this.currentWidth_ = currentWidth;
        this.applySize(this.currentWidth_, this.currentHeight_);
    }

    get currentWidth() {
        return this.currentWidth_;
    }

    @Input()
    set currentHeight(currentHeight: number) {
        this.currentHeight_ = currentHeight;
        this.applySize(this.currentWidth_, this.currentHeight_);
    }

    get currentHeight() {
        return this.currentHeight_;
    }

    @Input()
    set data(data: SpQueryResult[]) {
        this.data_ = data;
        this.initOptions();
    }

    get data() {
        return this.data_;
    }
}
