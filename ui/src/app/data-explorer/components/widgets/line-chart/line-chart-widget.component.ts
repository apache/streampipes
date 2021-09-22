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
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../../../core-model/datalake/GroupedDataResult';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { LineChartWidgetModel } from './model/line-chart-widget.model';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';

@Component({
  selector: 'sp-data-explorer-line-chart-widget',
  templateUrl: './line-chart-widget.component.html',
  styleUrls: ['./line-chart-widget.component.css']
})
export class LineChartWidgetComponent extends BaseDataExplorerWidget<LineChartWidgetModel> implements OnInit {

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
        type: 'date',
      },
      yaxis: {
        fixedrange: true
      },
      updatemenus: this.updatemenus,

      // setting hovermode to 'closest'
      hovermode: 'closest',
      // adding shapes for displaying labeled time intervals
      shapes: [],
      // box selection with fixed height
      selectdirection: 'h',

      // default dragmode is zoom
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
      pad: {'r': 10, 't': 10},
      showactive: true,
      type: 'buttons',
      x: 0.0,
      xanchor: 'left',
      y: 1.3,
      yanchor: 'top',
      font: {color: this.dataExplorerWidget.baseAppearanceConfig.textColor},
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

  private processNonGroupedData(dataResult: DataResult,
                                sourceIndex: number) {
    if (dataResult.total === 0) {
      this.setShownComponents(true, false, false);
    } else {
      this.data = this.transformData(dataResult, sourceIndex);
      this.setShownComponents(false, true, false);
    }
  }

  private processGroupedData(res: GroupedDataResult) {
    if (res.total === 0) {
      this.setShownComponents(true, false, false);
    } else {
      const tmp = this.transformGroupedData(res, this.fieldProvider.primaryTimestampField.runtimeName);
      this.data = this.displayGroupedData(tmp);

      this.setShownComponents(false, true, false);
    }
  }

  displayGroupedData(transformedData: GroupedDataResult) {
    const tmp = [];

    const groupNames = Object.keys(transformedData.dataResults);
    for (const groupName of groupNames) {
      const value = transformedData.dataResults[groupName];
      this.dataExplorerWidget.visualizationConfig.yKeys.forEach(key => {
        value.rows.forEach(serie => {
          if (serie.name === key) {
            serie.name = groupName + ' ' + serie.name;
            tmp.push(serie);
          }
        });
      });

      if (this.dataExplorerWidget.visualizationConfig.showCountValue) {
        let containsCount = false;
        value.rows.forEach(serie => {
          if (serie.name.startsWith('count') && !containsCount) {
            serie.name = groupName + ' count';
            tmp.push(serie);
            containsCount = true;
          }
        });
      }
    }
    return tmp;
  }

  transformData(data: DataResult,
                sourceIndex: number): any[] {
    const columnsContainingNumbers = this.dataExplorerWidget.visualizationConfig.selectedLineChartProperties
        .filter(f => this.fieldProvider.numericFields.find(field => field.fullDbName === f.fullDbName));
    const columnsContainingStrings = this.dataExplorerWidget.visualizationConfig.selectedLineChartProperties
        .filter(f => this.fieldProvider.nonNumericFields.find(field => field.fullDbName === f.fullDbName));
    const indexXkey = 0;

    const tmpLineChartTraces: any[] = [];
    // create line chart traces according to column type
    columnsContainingNumbers.forEach(key => {
      const headerName = data.headers[this.getColumnIndex(key, data)];
      tmpLineChartTraces[key.fullDbName] = {
        type: 'scatter',
        mode: this.dataExplorerWidget.visualizationConfig.chartMode,
        name: headerName,
        connectgaps: false,
        x: [],
        y: []
      };
    });

    columnsContainingStrings.forEach(key => {
      const headerName = data.headers[key.fullDbName];
      tmpLineChartTraces[key.fullDbName] = {
        name: headerName, x: [], y: []
      };
    });

    // fill line chart traces with data
    data.rows.forEach(row => {
      this.dataExplorerWidget.visualizationConfig.selectedLineChartProperties.forEach(field => {
        const columnIndex = this.getColumnIndex(field, data);
        tmpLineChartTraces[field.fullDbName].x.push(new Date(row[indexXkey]));
        tmpLineChartTraces[field.fullDbName].y.push(row[columnIndex]);
      });
    });
    return Object.values(tmpLineChartTraces);
  }

  transformGroupedData(data: GroupedDataResult, xKey: string): GroupedDataResult {
    // TODO not yet supported after refactoring
    for (const key in data.dataResults) {
      const dataResult = data.dataResults[key];
      this.data = this.transformData(dataResult, 0);
    }

    return data;
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
      this.data.forEach(d => d.mode = this.dataExplorerWidget.visualizationConfig.chartMode);
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

  onDataReceived(dataResults: DataResult[]) {
    this.data = [];
    dataResults.forEach((result, index) => {
      this.processNonGroupedData(result, index);
    });
  }

  handleUpdatedFields(addedFields: DataExplorerField[],
                      removedFields: DataExplorerField[]) {
    this.dataExplorerWidget.visualizationConfig.selectedLineChartProperties =
        this.updateFieldSelection(
            this.dataExplorerWidget.visualizationConfig.selectedLineChartProperties,
            addedFields,
            removedFields,
            (field) => field.fieldCharacteristics.numeric
        );
  }
}
