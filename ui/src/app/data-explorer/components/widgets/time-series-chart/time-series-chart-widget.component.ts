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
import { TimeSeriesChartWidgetModel } from './model/time-series-chart-widget.model';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';
import { SpQueryResult } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-data-explorer-time-series-chart-widget',
  templateUrl: './time-series-chart-widget.component.html',
  styleUrls: ['./time-series-chart-widget.component.scss']
})
export class TimeSeriesChartWidgetComponent extends BaseDataExplorerWidget<TimeSeriesChartWidgetModel> implements OnInit {

  presetColors: string[] = ['#39B54A', '#1B1464', '#f44336', '#4CAF50', '#FFEB3B', '#FFFFFF', '#000000'];

  data: any[] = undefined;
  advancedSettingsActive = false;
  showBackgroundColorProperty = true;

  selectedStartX = undefined;
  selectedEndX = undefined;
  n_selected_points = undefined;

  // this can be set to scale the line chart according to the layout
  offsetRightLineChart = 10;

  updatemenus = [];
  graph = {

    layout: {
      font: {
        color: '#FFF',
        family: 'Roboto'
      },
      autosize: true,
      plot_bgcolor: '#fff',
      paper_bgcolor: '#fff',
      xaxis: {
        type: 'date'
      },
      yaxis: {
        fixedrange: true
      },
      updatemenus: this.updatemenus,

      hovermode: 'closest',
      shapes: [],
      selectdirection: 'h',
      dragmode: 'zoom'
    },
    config: {
      modeBarButtonsToRemove: ['lasso2d', 'select2d', 'toggleSpikelines', 'toImage'],
      displaylogo: false
    }
  };

  ngOnInit(): void {
    this.updatemenus = [{
      buttons: [
        {
          args: ['mode', 'lines'],
          label: 'Line',
          method: 'restyle'
        },
        {
          args: ['mode', 'markers'],
          label: 'Dots',
          method: 'restyle'
        },

        {
          args: ['mode', 'lines+markers'],
          label: 'Dots + Lines',
          method: 'restyle'
        }
      ],
      direction: 'left',
      pad: { 'r': 10, 't': 10 },
      showactive: true,
      type: 'buttons',
      x: 0.0,
      xanchor: 'left',
      y: 1.3,
      yanchor: 'top',
      font: { color: this.dataExplorerWidget.baseAppearanceConfig.textColor },
      bgcolor: this.dataExplorerWidget.baseAppearanceConfig.backgroundColor,
      bordercolor: '#000'
    }];

    super.ngOnInit();
    this.resizeService.resizeSubject.subscribe(info => {
      if (info.gridsterItem.id === this.gridsterItem.id) {
        setTimeout(() => {
          this.graph.layout.autosize = false;
          (this.graph.layout as any).width = (info.gridsterItemComponent.width - this.offsetRightLineChart);
          (this.graph.layout as any).height = (info.gridsterItemComponent.height - 80);
        }, 100);
      }
    });
  }

  transformData(data: SpQueryResult,
                sourceIndex: number): any[] {

    const numericPlusBooleanFields = this.fieldProvider.numericFields.concat(this.fieldProvider.booleanFields);

    const columnsContainingNumbersPlusBooleans = this.dataExplorerWidget.visualizationConfig.selectedTimeSeriesChartProperties
      .filter(f => numericPlusBooleanFields.find(field => field.fullDbName === f.fullDbName && f.sourceIndex === data.sourceIndex));

    const indexXkey = 0;

    const tmpLineChartTraces: any[] = [];
    columnsContainingNumbersPlusBooleans.forEach(field => {
      const headerName = data.headers[this.getColumnIndex(field, data)];
      tmpLineChartTraces[field.fullDbName + sourceIndex.toString()] = {
        type: 'scatter',
        mode: 'Line',
        name: headerName,
        connectgaps: false,
        x: [],
        y: []
      };
    });

    data.allDataSeries[0].rows.forEach(row => {
      this.dataExplorerWidget.visualizationConfig.selectedTimeSeriesChartProperties.forEach(field => {
        if (field.sourceIndex === data.sourceIndex) {
          const columnIndex = this.getColumnIndex(field, data);

          let value = row[columnIndex];
          if (this.fieldProvider.booleanFields.find(f => field.fullDbName === f.fullDbName
            && f.sourceIndex === data.sourceIndex) !== undefined) {
            value = value === true ? 1 : 0;
          }

          tmpLineChartTraces[field.fullDbName + sourceIndex.toString()].x.push(new Date(row[indexXkey]));
          tmpLineChartTraces[field.fullDbName + sourceIndex.toString()].y.push(value);
        }
      });
    });

    return Object.values(tmpLineChartTraces);
  }

  setStartX(startX: string) {
    this.selectedStartX = startX;
  }

  setEndX(endX: string) {
    this.selectedEndX = endX;
  }

  updateAppearance() {
    this.graph.layout.paper_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.plot_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.font.color = this.dataExplorerWidget.baseAppearanceConfig.textColor;
    if (this.data) {
      this.dataExplorerWidget.visualizationConfig.selectedTimeSeriesChartProperties.map((field, index) => {
        if (this.data[index] !== undefined) {
          this.data[index]['marker'] = { 'color': '' };

          const name = field.runtimeName + field.sourceIndex.toString();

          if (!(name in this.dataExplorerWidget.visualizationConfig.chosenColor)) {
            this.dataExplorerWidget.visualizationConfig.chosenColor[name] = this.presetColors[index];
          }

          if (!(name in this.dataExplorerWidget.visualizationConfig.displayName)) {
            this.dataExplorerWidget.visualizationConfig.displayName[name] = field.fullDbName;
          }

          if (!(name in this.dataExplorerWidget.visualizationConfig.displayType)) {
            this.dataExplorerWidget.visualizationConfig.displayType[name] = 'lines';
          }

          this.data[index].marker.color = this.dataExplorerWidget.visualizationConfig.chosenColor[name];
          this.data[index].name = this.dataExplorerWidget.visualizationConfig.displayName[name];

          let displayType = 'scatter';
          let displayMode = 'lines';

          const setType = this.dataExplorerWidget.visualizationConfig.displayType[name];

          if (setType !== 'bar') {
            displayMode = setType;
          } else {
            displayType = 'bar';
          }

          this.data[index].type = displayType;
          this.data[index].mode = displayMode;

        }
      });
    }

  }

  refreshView() {
    this.updateAppearance();
  }

  onResize(width: number, height: number) {
    this.graph.layout.autosize = false;
    (this.graph.layout as any).width = width;
    (this.graph.layout as any).height = height;
  }

  beforeDataFetched() {
    this.graph.layout.shapes = [];
    this.setShownComponents(false, false, true);
  }

  onDataReceived(spQueryResults: SpQueryResult[]) {
    this.data = [];

    this.setShownComponents(true, false, false);
    spQueryResults.map((spQueryResult, index) => {
      this.data = this.data.concat(this.transformData(spQueryResult, spQueryResult.sourceIndex));
    });
    this.setShownComponents(false, true, false);
  }

  handleUpdatedFields(addedFields: DataExplorerField[],
                      removedFields: DataExplorerField[]) {
    this.dataExplorerWidget.visualizationConfig.selectedTimeSeriesChartProperties =
      this.updateFieldSelection(
        this.dataExplorerWidget.visualizationConfig.selectedTimeSeriesChartProperties,
        addedFields,
        removedFields,
        (field) => field.fieldCharacteristics.numeric
      );
  }
}
