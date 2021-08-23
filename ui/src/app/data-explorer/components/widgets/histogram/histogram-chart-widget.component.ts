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
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { DatalakeRestService } from '../../../../platform-services/apis/datalake-rest.service';
import { WidgetConfigurationService } from '../../../services/widget-configuration.service';
import { ResizeService } from '../../../services/resize.service';
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { DatalakeQueryParameters } from '../../../../core-services/datalake/DatalakeQueryParameters';
import { DatalakeQueryParameterBuilder } from '../../../../core-services/datalake/DatalakeQueryParameterBuilder';
import { Observable, zip } from 'rxjs';
import { HistogramChartWidgetModel } from './model/histogram-chart-widget.model';

@Component({
  selector: 'sp-data-explorer-histogram-chart-widget',
  templateUrl: './histogram-chart-widget.component.html',
  styleUrls: ['./histogram-chart-widget.component.scss']
})
export class HistogramChartWidgetComponent extends BaseDataExplorerWidget<HistogramChartWidgetModel> implements OnInit {

  data = [
    {
        x: [],
        type: 'histogram',
    }
  ];

  graph = {
    layout: {
      font: {
        color: '#FFF'
      },
      autosize: true,
      plot_bgcolor: '#fff',
      paper_bgcolor: '#fff',
    },
    config: {
      modeBarButtonsToRemove: ['lasso2d', 'select2d', 'toggleSpikelines', 'toImage'],
      displaylogo: false,
      displayModeBar: false,
      responsive: true
    }
  };

  constructor(dataLakeRestService: DatalakeRestService,
              widgetConfigurationService:  WidgetConfigurationService,
              resizeService: ResizeService) {
    super(dataLakeRestService, widgetConfigurationService, resizeService);
  }

  refreshData() {
    this.getQuery().subscribe(result => {
      this.prepareData(result);
    });
  }

  getQuery(): Observable<DataResult> {
    return this.dataLakeRestService.getData(this.dataLakeMeasure.measureName, this.buildQuery());
  }

  refreshView() {
    this.updateAppearance();
  }

  prepareData(result: DataResult) {
    this.data[0].x = this.transform(result.rows);
  }

  transform(rows): any[] {
    return rows.map(row => row[1]);
  }

  updateAppearance() {
    this.graph.layout.paper_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.plot_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.font.color = this.dataExplorerWidget.baseAppearanceConfig.textColor;
  }

  buildQuery(): DatalakeQueryParameters {
    return DatalakeQueryParameterBuilder
        .create(this.timeSettings.startTime, this.timeSettings.endTime)
        .withAutoAggregation('MEAN')
        .withColumnFilter([this.dataExplorerWidget.dataConfig.selectedProperty])
        .build();
  }

  onResize(width: number, height: number) {
    this.graph.layout.autosize = false;
    (this.graph.layout as any).width = width;
    (this.graph.layout as any).height = height;
  }


}
