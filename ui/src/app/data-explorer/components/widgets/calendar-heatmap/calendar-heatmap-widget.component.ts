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
import { DataResult } from '../../../../core-model/datalake/DataResult';

import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { CalendarHeatmapWidgetModel } from './model/calendar-heatmap-widget.model';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';

import {EChartsOption, number, format} from 'echarts';
import {ECharts} from 'echarts/core';



@Component({
  selector: 'sp-data-explorer-calendar-heatmap-widget',
  templateUrl: './calendar-heatmap-widget.component.html',
  styleUrls: ['./calendar-heatmap-widget.component.scss']
})
export class CalendarHeatmapWidgetComponent extends BaseDataExplorerWidget<CalendarHeatmapWidgetModel> implements OnInit, OnDestroy {

  eChartsInstance: ECharts;
  currentWidth = 1600;
  currentHeight = 1200;

  option = {}

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

  onDataReceived(dataResults: DataResult[]) {
  }

  onChartInit(ec) {
    this.eChartsInstance = ec;
    this.applySize(this.currentWidth, this.currentHeight);
    
    this.option = {
      title: {
        top: 30,
        left: 'center',
        text: 'Daily Step Count'
      },
      tooltip: {
        // trigger: 'axis',
        formatter: function(params) {
          // params = params[0];
          // var date = new Date(params.value[0]);
          return params.value[0] + ' : ' + params.value[1];
        }
      },
      visualMap: {
        min: 0,
        max: 10000,
        type: 'piecewise',
        orient: 'horizontal',
        left: 'center',
        top: 65
      },
      calendar: {
        top: 120,
        left: 30,
        right: 30,
        cellSize: ['auto', 13],
        range: '2017',
        itemStyle: {
          borderWidth: 0.5
        },
        yearLabel: { show: false }
      },
      series: {
        type: 'heatmap',
        coordinateSystem: 'calendar',
        data: this.loadData()
      }
    };
  }
  
  applySize(width: number, height: number) {
    if (this.eChartsInstance) {
      this.eChartsInstance.resize({width: width, height: height});
    }
  }

  loadData() {
    const year = '2017';
    var date = +number.parseDate(year + '-01-01');
    var end = +number.parseDate(+year + 1 + '-01-01');
    var dayTime = 3600 * 24 * 1000;
    var data = [];
    for (var time = date; time < end; time += dayTime) {
      data.push([
        format.formatTime('yyyy-MM-dd', time),
        Math.floor(Math.random() * 10000)
      ]);
    }
    return data;
  }

  loadDataNew() {
    const year = '2017';
    var date = +number.parseDate(year + '-01-01');
    var end = +number.parseDate(+year + 1 + '-01-01');
    var dayTime = 3600 * 24 * 1000;
    var data = [];
    for (var time = date; time < end; time += dayTime) {
      data.push([
        format.formatTime('yyyy-MM-dd', time),
        Math.floor(Math.random() * 10000)
      ]);
    }
    return data;
  }

}
