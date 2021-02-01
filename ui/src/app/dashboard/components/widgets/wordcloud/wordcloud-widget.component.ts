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
import {ResizeService} from "../../../services/resize.service";
import {DashboardService} from "../../../services/dashboard.service";
import {EventPropertyList} from "../../../../core-model/gen/streampipes-model";
import {WordCloudConfig} from "./wordcloud-config";

import { EChartsOption } from 'echarts';
import 'echarts-wordcloud';
import {ECharts} from "echarts/core";


@Component({
  selector: 'wordcloud-widget',
  templateUrl: './wordcloud-widget.component.html',
  styleUrls: ['./wordcloud-widget.component.scss']
})
export class WordcloudWidgetComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

  countProperty: string;
  nameProperty: string;
  eventProperty: EventPropertyList;

  canvasDimensions: any;
  words: Array<string> = new Array<string>();

  eChartsInstance: ECharts;
  dynamicData: EChartsOption;
  // @ts-ignore
  chartOption: EChartsOption = {
    series: [{
      type: 'wordCloud',
      shape: 'circle',
      left: 'center',
      top: 'center',
      width: '100%',
      height: '100%',
      right: null,
      bottom: null,
      sizeRange: [12, 60],
      rotationRange: [-90, 90],
      rotationStep: 45,
      gridSize: 8,
      drawOutOfBound: false,
      layoutAnimation: true,

      // Global text style
      textStyle: {
        fontFamily: 'sans-serif',
        fontWeight: 'bold',
        // Color can be a callback function or a color string
        color: function () {
          // Random color
          return 'rgb(' + [
            Math.round(Math.random() * 160),
            Math.round(Math.random() * 160),
            Math.round(Math.random() * 160)
          ].join(',') + ')';
        }
      },
      emphasis: {
        focus: 'self',

        textStyle: {
          shadowBlur: 10,
          shadowColor: '#333'
        }
      },

      // Data is an array. Each array item must have name and value property.
      data: [{
        name: 'Farrah Abraham',
        value: 366,
        // Style of single text
        textStyle: {
        }
      }]
    }]
  };

  constructor(rxStompService: RxStompService, dashboardService: DashboardService, resizeService: ResizeService) {
    super(rxStompService, dashboardService, resizeService, false);
  }

  protected extractConfig(extractor: StaticPropertyExtractor) {
    this.countProperty = extractor.mappingPropertyValue(WordCloudConfig.COUNT_PROPERTY_KEY);
    this.nameProperty = extractor.mappingPropertyValue(WordCloudConfig.NAME_PROPERTY_KEY);
  }

  protected onEvent(event: any) {
    this.words = event[this.countProperty];
    this.dynamicData = this.chartOption;
    if (this.words.length > 0) {
      this.dynamicData.series[0].data.push({name: this.words[0], value: 366, textStyle: {}});
    }
    this.eChartsInstance.setOption(this.dynamicData);
  }

  protected onSizeChanged(width: number, height: number) {
    this.canvasDimensions = {
      //gridSize: Math.round(width),
      weightFactor: function (size) {
        return Math.pow(size, 2.3) * width / 1024;
      },
      fontFamily: 'Times, serif',
      color: function (word, weight) {
        return (weight === 12) ? '#f02222' : '#c09292';
      },
      rotateRatio: 0.5,
      rotationSteps: 2,
      backgroundColor: '#ffe0e0'
    }
  }

  onChartInit(ec) {
    console.log(ec);
    this.eChartsInstance = ec;
  }

}
