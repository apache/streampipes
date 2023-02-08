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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { ResizeService } from '../../../services/resize.service';
import { BaseEchartsWidget } from '../base/base-echarts-widget';
import { StackedLineChartConfig } from './stacked-line-chart-config';
import { EChartsOption } from 'echarts';
import { DatalakeRestService } from '@streampipes/platform-services';
import { BaseNgxLineChartsStreamPipesWidget } from '../base/base-ngx-line-charts-widget';
import { WidgetConfigBuilder } from '../../../registry/widget-config-builder';

@Component({
    selector: 'sp-stacked-line-chart-widget',
    templateUrl: './stacked-line-chart-widget.component.html',
    styleUrls: ['./stacked-line-chart-widget.component.scss'],
})
export class StackedLineChartWidgetComponent
    extends BaseEchartsWidget
    implements OnInit, OnDestroy
{
    partitionField: string;
    valueFields: string[];
    timestampField: string;

    chartOption = {
        grid: {
            left: 50,
            top: 10,
            right: 50,
            bottom: 100,
        },
        tooltip: {
            trigger: 'axis',
            formatter(params) {
                params = params[0];
                const date = new Date(params.value[0]);
                return (
                    date.getHours() +
                    ':' +
                    (date.getMinutes() + 1) +
                    ':' +
                    date.getSeconds() +
                    ' : ' +
                    params.value[1]
                );
            },
            axisPointer: {
                animation: false,
            },
        },
        xAxis: {
            type: 'time',
            axisLabel: {
                formatter: params => {
                    const date = new Date(params);
                    return (
                        date.getHours() +
                        ':' +
                        date.getMinutes() +
                        ':' +
                        date.getSeconds()
                    );
                },
                textStyle: {
                    color: this.selectedPrimaryTextColor,
                },
            },
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                textStyle: {
                    color: this.selectedPrimaryTextColor,
                },
            },
        },
        series: [],
        animationDuration: 300,
    };

    constructor(
        dataLakeService: DatalakeRestService,
        resizeService: ResizeService,
    ) {
        super(dataLakeService, resizeService);
        this.configReady = true;
    }

    protected extractConfig(extractor: StaticPropertyExtractor) {
        this.valueFields = extractor.mappingPropertyValues(
            StackedLineChartConfig.VALUE_KEY,
        );
        this.chartOption.xAxis.axisLabel.textStyle.color =
            this.selectedPrimaryTextColor;
        this.chartOption.yAxis.axisLabel.textStyle.color =
            this.selectedPrimaryTextColor;
    }

    getFieldsToQuery(): string[] {
        return this.valueFields;
    }

    protected onEvent(events: any) {
        this.dynamicData = this.chartOption;
        this.dynamicData.series = [];

        this.valueFields.forEach(field => {
            const series = this.makeNewSeries(field);
            series.data = events.map(event => {
                const timestamp =
                    event[BaseNgxLineChartsStreamPipesWidget.TIMESTAMP_KEY];
                return {
                    name: timestamp.toString(),
                    value: [timestamp, event[field]],
                };
            });
            this.dynamicData.series.push(series);
        });

        if (this.eChartsInstance) {
            this.eChartsInstance.setOption(this.dynamicData as EChartsOption);
        }
    }

    makeNewSeries(seriesName: string): any {
        return {
            type: 'line',
            smooth: true,
            name: seriesName,
            data: [],
        };
    }

    protected getQueryLimit(extractor: StaticPropertyExtractor): number {
        return extractor.integerParameter(WidgetConfigBuilder.QUERY_LIMIT_KEY);
    }
}
