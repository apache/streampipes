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

import {Component, OnDestroy, OnInit} from "@angular/core";
import {RxStompService} from "@stomp/ng2-stompjs";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {DashboardService} from "../../../services/dashboard.service";
import {ResizeService} from "../../../services/resize.service";
import {ECharts} from "echarts/core";
import {EChartsOption} from "echarts";
import {BarRaceConfig} from "./bar-race-config";


@Component({
  selector: 'bar-race-widget',
  templateUrl: './bar-race-widget.component.html',
  styleUrls: ['./bar-race-widget.component.scss']
})
export class BarRaceWidgetComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

  currentWidth: number;
  currentHeight: number;

  configReady: boolean = false;

  eChartsInstance: ECharts;
  dynamicData: any;

  partitionField: string;
  valueField: string;

  chartOption = {
    grid: {
      top: 10,
      bottom: 30,
      left: 150,
      right: 80
    },
    xAxis: {
      max: 'dataMax',
      label: {
        formatter: n => {
          return Math.round(n);
        },
      },
      axisLabel: {
        textStyle: {
          color: "#FFFFFF"
        }
      }
    },
    dataset: {
      source: [],
    },
    yAxis: {
      type: 'category',
      inverse: true,
      interval: 0,
      data: [],
      axisLabel: {
        show: true,
        formatter: value => {
          return value;
        },
        textStyle: {
          color: "#FFFFFF"
        }
      },
      animationDuration: 300,
      animationDurationUpdate: 300
    },
    series: [{
      //data: [],
      data: [],
      realtimeSort: true,
      seriesLayoutBy: 'column',
      type: 'bar',
      itemStyle: {
        color: param => {
          return this.selectedPrimaryTextColor;
        }
      },
      encode: {
        x: "value",
        y: "name"
      },
      label: {
        show: true,
        precision: 1,
        position: 'right',
        valueAnimation: true,
        fontFamily: 'monospace',
        color: param => {
          return this.selectedPrimaryTextColor;
        }
      }
    }],
    // Disable init animation.
    animationDuration: 0,
    animationDurationUpdate: 1000,
    animationEasing: 'linear',
    animationEasingUpdate: 'linear',
  };

  constructor(rxStompService: RxStompService, dashboardService: DashboardService, resizeService: ResizeService) {
    super(rxStompService, dashboardService, resizeService, false);
  }

  protected extractConfig(extractor: StaticPropertyExtractor) {
    this.partitionField = extractor.mappingPropertyValue(BarRaceConfig.PARTITION_KEY);
    this.valueField = extractor.mappingPropertyValue(BarRaceConfig.VALUE_KEY);
    this.chartOption.xAxis.axisLabel.textStyle.color = this.selectedPrimaryTextColor;
    this.chartOption.yAxis.axisLabel.textStyle.color = this.selectedPrimaryTextColor;
  }

  protected onEvent(event: any) {
    this.dynamicData = this.chartOption;
    let partitionValue = event[this.partitionField];
    let value = event[this.valueField];
    if (this.dynamicData.series[0].data.some(d => d.name == partitionValue)) {
      this.dynamicData.series[0].data.find(d => d.name == partitionValue).value = value;
    } else {
      this.dynamicData.series[0].data.push({name: partitionValue, value: value});
      this.dynamicData.yAxis.data.push(partitionValue);
    }

    if (this.eChartsInstance) {
      this.eChartsInstance.setOption(this.dynamicData as EChartsOption);
    }
  }

  protected onSizeChanged(width: number, height: number) {
    this.currentWidth = width;
    this.currentHeight = height;
    this.configReady = true;
    this.applySize(width, height);
  }

  onChartInit(ec) {
    this.eChartsInstance = ec;
    this.applySize(this.currentWidth, this.currentHeight);
  }

  applySize(width: number, height: number) {
    if (this.eChartsInstance) {
      this.eChartsInstance.resize({width: width, height: height});
    }
  }
}
