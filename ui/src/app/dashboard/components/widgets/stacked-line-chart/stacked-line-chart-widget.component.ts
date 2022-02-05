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
import { WidgetConfigBuilder } from "../../../registry/widget-config-builder";


@Component({
  selector: 'stacked-line-chart-widget',
  templateUrl: './stacked-line-chart-widget.component.html',
  styleUrls: ['./stacked-line-chart-widget.component.scss']
})
export class StackedLineChartWidgetComponent extends BaseEchartsWidget implements OnInit, OnDestroy {

  partitionField: string;
  valueFields: string[];
  timestampField: string;

  chartOption = {
    tooltip: {
      trigger: 'axis',
      formatter (params) {
        params = params[0];
        const date = new Date(params.value[0]);
        return date.getHours() + ':' + (date.getMinutes() + 1) + ':' + date.getSeconds() + ' : ' + params.value[1];
      },
      axisPointer: {
        animation: false
      }
    },
    xAxis: {
      type: 'time',
      axisLabel: {
        formatter: params => {
          const date = new Date(params);
          return date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
        },
        textStyle: {
          color: this.selectedPrimaryTextColor
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        textStyle: {
          color: this.selectedPrimaryTextColor
        }
      }
    },
    series: [],
    animationDuration: 500
  };

  constructor(dataLakeService: DatalakeRestService, resizeService: ResizeService) {
    super(dataLakeService, resizeService);
  }

  protected extractConfig(extractor: StaticPropertyExtractor) {
    this.valueFields = extractor.mappingPropertyValues(StackedLineChartConfig.VALUE_KEY);
    this.chartOption.xAxis.axisLabel.textStyle.color = this.selectedPrimaryTextColor;
    this.chartOption.yAxis.axisLabel.textStyle.color = this.selectedPrimaryTextColor;
  }

  protected onEvent(event: any) {
    this.dynamicData = this.chartOption;
    const timestamp = event[BaseNgxLineChartsStreamPipesWidget.TIMESTAMP_KEY];
    this.valueFields.forEach(field => {
      if (this.dynamicData.series.some(d => d.name === field)) {
        const date = new Date(timestamp);
        this.dynamicData.series.find(d => d.name === field).data.push(
            {'name': date.toString(), value: [timestamp, event[field]]}
        );
        if (this.dynamicData.series.find(d => d.name === field).data.length > 5) {
          this.dynamicData.series.find(d => d.name === field).data.shift();
        }
      } else {
        this.dynamicData.series.push(this.makeNewSeries(field, timestamp, event[field]));
      }
    });

    if (this.eChartsInstance) {
      this.eChartsInstance.setOption(this.dynamicData as EChartsOption);
    }
  }

  makeNewSeries(seriesName, timestamp, value) {
    const date = new Date(timestamp);
    return {
      type: 'line',
      smooth: true,
      name: seriesName,
      data: [{
        'name': date.toString(),
        value: [timestamp, value]
      }],
    };
  }

  protected getQueryLimit(extractor: StaticPropertyExtractor): number {
    return extractor.integerParameter(WidgetConfigBuilder.QUERY_LIMIT_KEY);
  }

}
