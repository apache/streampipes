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
import { SpQueryResult } from '../../../../core-model/gen/streampipes-model';

import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { HeatmapWidgetModel } from './model/heatmap-widget.model';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';

import { EChartsOption } from 'echarts';
import { ECharts } from 'echarts/core';


@Component({
  selector: 'sp-data-explorer-heatmap-widget',
  templateUrl: './heatmap-widget.component.html',
  styleUrls: ['./heatmap-widget.component.scss']
})
export class HeatmapWidgetComponent extends BaseDataExplorerWidget<HeatmapWidgetModel> implements OnInit, OnDestroy {

  eChartsInstance: ECharts;
  currentWidth = 1200;
  currentHeight = 600;

  option = {};
  dynamic: EChartsOption;

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  public refreshView() {
  }

  onResize(width: number, height: number) {
  }

  handleUpdatedFields(addedFields: DataExplorerField[],
                      removedFields: DataExplorerField[]) {
  }

  beforeDataFetched() {
  }

  onDataReceived(spQueryResult: SpQueryResult[]) {
    const dataBundle = this.convertData(spQueryResult);
    if (Object.keys(this.option).length > 0) {
      this.setOptions(dataBundle[0], dataBundle[1], dataBundle[2], dataBundle[3], dataBundle[4]);
    }
  }

  onChartInit(ec) {
    this.eChartsInstance = ec;
    this.applySize(this.currentWidth, this.currentHeight);
    this.initOptions();
  }

  applySize(width: number, height: number) {
    if (this.eChartsInstance) {
      this.eChartsInstance.resize({ width: width, height: height });
    }
  }

  convertData(spQueryResult: SpQueryResult[]) {

    let min = 1000000;
    let max = -100000;

    const result = spQueryResult[0].allDataSeries;
    const xAxisData = this.transform(result[0].rows, 0); // always time.
    const heatIndex = this.getColumnIndex(this.dataExplorerWidget.visualizationConfig.selectedHeatProperty, spQueryResult[0]);

    const yAxisData = [];
    const contentData = [];

    result.map((groupedList, index) => {

      let groupedVal = index.toString();

      if (groupedList['tags'] != null) {
        Object.entries(groupedList['tags']).forEach(
          ([key, value]) => {
            groupedVal = value;
          });
      }

      yAxisData.push(groupedVal);

      const contentDataPure = this.transform(result[index].rows, heatIndex);
      const localMax = Math.max.apply(Math, contentDataPure);
      const localMin = Math.min.apply(Math, contentDataPure);

      max = localMax > max ? localMax : max;
      min = localMin < min ? localMin : min;

      contentDataPure.map((cnt, colIndex) => {
        contentData.push([colIndex, index, cnt]);
      });
    });

    return [contentData, xAxisData, yAxisData, min, max];
  }

  initOptions() {
    this.option = {
      tooltip: {
        position: 'top'
      },
      grid: {
        height: '50%',
        top: '10%'
      },
      xAxis: {
        type: 'category',
        data: [],
        splitArea: {
          show: true
        }
      },
      yAxis: {
        type: 'category',
        data: [],
        splitArea: {
          show: true
        }
      },
      visualMap: {
        min: 0,
        max: 10,
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: '15%'
      },
      series: [
        {
          name: '',
          type: 'heatmap',
          data: [],
          label: {
            show: true
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
  }

  setOptions(contentData: any, xAxisData: any, yAxisData: any, min: any, max: any) {
    this.dynamic = this.option;
    this.dynamic.series[0].data = contentData;
    this.dynamic.series[0].label.show = this.dataExplorerWidget.visualizationConfig.showLabelsProperty;
    this.dynamic['xAxis']['data'] = xAxisData;
    this.dynamic['yAxis']['data'] = yAxisData;
    this.dynamic['visualMap']['min'] = min;
    this.dynamic['visualMap']['max'] = max;
    this.eChartsInstance.setOption(this.dynamic as EChartsOption);
    this.option = this.dynamic;
  }

  transform(rows, index: number): any[] {
    return rows.map(row => row[index]);
  }

}
