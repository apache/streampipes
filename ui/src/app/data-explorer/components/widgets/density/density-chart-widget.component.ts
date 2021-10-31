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
import { DensityChartWidgetModel } from './model/density-chart-widget.model';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';
import { SpQueryResult } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-data-explorer-density-chart-widget',
  templateUrl: './density-chart-widget.component.html',
  styleUrls: ['./density-chart-widget.component.scss']
})
export class DensityChartWidgetComponent extends BaseDataExplorerWidget<DensityChartWidgetModel> implements OnInit {

  data = [
    {
      x: [],
      y: [],
      mode: 'markers',
      name: 'points',
      marker: {
        color: 'rgb(102,0,0)',
        size: 2,
        opacity: 0.4
      },
      type: 'scatter'
    },
    {
      x: [],
      y: [],
      name: 'density',
      ncontours: 20,
      colorscale: 'Hot',
      reversescale: true,
      showscale: false,
      type: 'histogram2dcontour'
    }
  ];

  graph = {
    layout: {
      xaxis: {
        title: {
          text: ''
        }
      },
      yaxis: {
        title: {
          text: ''
        }
      },
      font: {
        color: '#FFF'
      },
      autosize: true,
      plot_bgcolor: '#fff',
      paper_bgcolor: '#fff'
    },
    config: {
      modeBarButtonsToRemove: ['lasso2d', 'select2d', 'toggleSpikelines', 'toImage'],
      displaylogo: false,
      displayModeBar: false,
      responsive: true
    }
  };

  refreshView() {
    this.updateAppearance();
  }

  prepareData(result: SpQueryResult[]) {
    const xIndex = this.getColumnIndex(this.dataExplorerWidget.visualizationConfig.firstField, result[0]);
    const yIndex = this.getColumnIndex(this.dataExplorerWidget.visualizationConfig.secondField, result[0]);
    this.data[0].x = this.transform(result[0].allDataSeries[0].rows, xIndex);
    this.data[1].x = this.transform(result[0].allDataSeries[0].rows, xIndex);
    this.data[0].y = this.transform(result[0].allDataSeries[0].rows, yIndex);
    this.data[1].y = this.transform(result[0].allDataSeries[0].rows, yIndex);
  }

  transform(rows, index: number): any[] {
    return rows.map(row => row[index]);
  }

  updateAppearance() {
    this.graph.layout.paper_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.plot_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.font.color = this.dataExplorerWidget.baseAppearanceConfig.textColor;
    this.graph.layout.xaxis.title.text = this.dataExplorerWidget.visualizationConfig.firstField.fullDbName;
    this.graph.layout.yaxis.title.text = this.dataExplorerWidget.visualizationConfig.secondField.fullDbName;
  }

  onResize(width: number, height: number) {
    this.graph.layout.autosize = false;
    (this.graph.layout as any).width = width;
    (this.graph.layout as any).height = height;
  }

  beforeDataFetched() {
  }

  onDataReceived(spQueryResult: SpQueryResult[]) {
    this.prepareData(spQueryResult);
    this.updateAppearance();
  }

  handleUpdatedFields(addedFields: DataExplorerField[], removedFields: DataExplorerField[]) {
    this.dataExplorerWidget.visualizationConfig.firstField =
      this.triggerFieldUpdate(this.dataExplorerWidget.visualizationConfig.firstField, addedFields, removedFields);

    this.dataExplorerWidget.visualizationConfig.secondField =
      this.triggerFieldUpdate(this.dataExplorerWidget.visualizationConfig.secondField, addedFields, removedFields);
  }

  triggerFieldUpdate(selected: DataExplorerField,
                     addedFields: DataExplorerField[],
                     removedFields: DataExplorerField[]): DataExplorerField {
    return this.updateSingleField(
      selected,
      this.fieldProvider.numericFields,
      addedFields,
      removedFields,
      (field) => field.fieldCharacteristics.numeric
    );
  }

}
