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

import { Component, OnInit } from '@angular/core';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';

import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import { HeatmapWidgetModel } from './model/heatmap-widget.model';

import { EChartsOption } from 'echarts';
import { ECharts } from 'echarts/core';

@Component({
    selector: 'sp-data-explorer-heatmap-widget',
    templateUrl: './heatmap-widget.component.html',
    styleUrls: ['./heatmap-widget.component.scss'],
})
export class HeatmapWidgetComponent
    extends BaseDataExplorerWidgetDirective<HeatmapWidgetModel>
    implements OnInit
{
    eChartsInstance: ECharts;
    currentWidth: number;
    currentHeight: number;

    option = {};
    dynamic: EChartsOption;

    configReady = false;

    ngOnInit(): void {
        super.ngOnInit();
        this.onSizeChanged(
            this.gridsterItemComponent.width,
            this.gridsterItemComponent.height,
        );
        this.initOptions();
    }

    public refreshView() {}

    onResize(width: number, height: number) {
        this.onSizeChanged(width, height);
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {}

    beforeDataFetched() {}

    onDataReceived(spQueryResult: SpQueryResult[]) {
        this.setShownComponents(false, true, false, false);
        const dataBundle = this.convertData(spQueryResult);
        if (Object.keys(this.option).length > 0) {
            this.setOptions(
                dataBundle[0],
                dataBundle[1],
                dataBundle[2],
                dataBundle[3],
                dataBundle[4],
            );
        }
    }

    onChartInit(ec: ECharts) {
        this.eChartsInstance = ec;
        this.applySize(this.currentWidth, this.currentHeight);
        this.initOptions();
    }

    protected onSizeChanged(width: number, height: number) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.configReady = true;
        this.applySize(width, height);
    }

    applySize(width: number, height: number) {
        if (this.eChartsInstance) {
            this.eChartsInstance.resize({ width, height });
        }
    }

    convertData(spQueryResult: SpQueryResult[]) {
        let min = 1000000;
        let max = -100000;

        const result = spQueryResult[0].allDataSeries;
        // const xAxisData = this.transform(result[0].rows, 0);

        const aggregatedXData = [];
        result.forEach(x => {
            const localXAxisData = this.transform(x.rows, 0);
            aggregatedXData.push(...localXAxisData);
        });

        const xAxisData = aggregatedXData.sort();

        const convXAxisData = [];
        xAxisData.forEach(x => {
            const date = new Date(x);
            const size = 2;
            const year = date.getFullYear();
            const month = this.pad(date.getMonth() + 1, size);
            const day = date.getDate();
            const hours = this.pad(date.getHours(), size);
            const minutes = this.pad(date.getMinutes(), size);
            const seconds = this.pad(date.getSeconds(), size);
            const milli = this.pad(date.getMilliseconds(), 3);

            const strDate =
                year +
                '-' +
                month +
                '-' +
                day +
                ' ' +
                hours +
                ':' +
                minutes +
                ':' +
                seconds +
                '.' +
                milli;
            convXAxisData.push(strDate);
        });

        const heatIndex = this.getColumnIndex(
            this.dataExplorerWidget.visualizationConfig.selectedHeatProperty,
            spQueryResult[0],
        );

        const yAxisData = [];
        const contentData = [];

        result.map((groupedList, index) => {
            let groupedVal = '';

            if (groupedList['tags'] != null) {
                Object.entries(groupedList['tags']).forEach(([key, value]) => {
                    groupedVal = value;
                });
            }

            yAxisData.push(groupedVal);

            const contentDataPure = this.transform(
                result[index].rows,
                heatIndex,
            );
            const localMax = Math.max.apply(Math, contentDataPure);
            const localMin = Math.min.apply(Math, contentDataPure);

            max = localMax > max ? localMax : max;
            min = localMin < min ? localMin : min;

            const localXAxisData = this.transform(groupedList.rows, 0);

            contentDataPure.map((cnt, colIndex) => {
                const currentX = localXAxisData[colIndex];
                const searchedIndex = aggregatedXData.indexOf(currentX);
                contentData.push([searchedIndex, index, cnt]);
            });
        });

        const timeNames = convXAxisData;
        const groupNames = yAxisData;

        this.option['tooltip'] = {
            formatter(params) {
                const timeIndex = params.value[0];
                const groupNameIndex = params.value[1];

                const value = params.value[2];
                const time = timeNames[timeIndex];
                const groupName = groupNames[groupNameIndex];

                let formattedTip =
                    '<style>' +
                    'ul {margin: 0px; padding: 0px; list-style-type: none; text-align: left}' +
                    '</style>' +
                    '<ul>' +
                    '<li><b>' +
                    'Time: ' +
                    '</b>' +
                    time +
                    '</li>';

                if (groupName !== '') {
                    formattedTip =
                        formattedTip +
                        '<li><b>' +
                        'Group: ' +
                        '</b>' +
                        groupName +
                        '</li>';
                }

                formattedTip =
                    formattedTip +
                    '<li><b>' +
                    'Value: ' +
                    '</b>' +
                    value +
                    '</li>' +
                    '</ul>';

                return formattedTip;
            },
            position: 'top',
        };

        if (groupNames.length === 1) {
            this.option['tooltip']['position'] = (
                point,
                params,
                dom,
                rect,
                size,
            ) => {
                return [point[0], '10%'];
            };
        }

        return [contentData, convXAxisData, yAxisData, min, max];
    }

    initOptions() {
        this.option = {
            tooltip: {},
            grid: {
                height: '80%',
                top: '7%',
            },
            xAxis: {
                type: 'category',
                data: [],
                splitArea: {
                    show: true,
                },
            },
            yAxis: {
                type: 'category',
                data: [],
                splitArea: {
                    show: true,
                },
            },
            visualMap: {
                min: 0,
                max: 10,
                calculable: true,
                orient: 'vertical',
                right: '5%',
                top: '7%',
            },
            series: [
                {
                    name: '',
                    type: 'heatmap',
                    data: [],
                    label: {
                        show: true,
                    },
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowColor: 'rgba(0, 0, 0, 0.5)',
                        },
                    },
                },
            ],
        };
    }

    setOptions(
        contentData: any,
        xAxisData: any,
        yAxisData: any,
        min: any,
        max: any,
    ) {
        this.dynamic = this.option;
        this.dynamic.series[0].data = contentData;
        this.dynamic.series[0].label.show =
            this.dataExplorerWidget.visualizationConfig.showLabelsProperty;
        this.dynamic['xAxis']['data'] = xAxisData;
        this.dynamic['yAxis']['data'] = yAxisData;
        this.dynamic['visualMap']['min'] = min;
        this.dynamic['visualMap']['max'] = max;
        if (this.eChartsInstance) {
            this.eChartsInstance.setOption(this.dynamic as EChartsOption);
        }
        this.option = this.dynamic;
    }

    transform(rows, index: number): any[] {
        return rows.map(row => row[index]);
    }

    pad(num, size) {
        num = num.toString();
        while (num.length < size) {
            num = '0' + num;
        }
        return num;
    }
}
