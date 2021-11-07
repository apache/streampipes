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
import { DistributionChartWidgetModel } from './model/distribution-chart-widget.model';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';
import { SpQueryResult } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-data-explorer-distribution-chart-widget',
  templateUrl: './distribution-chart-widget.component.html',
  styleUrls: ['./distribution-chart-widget.component.scss']
})
export class DistributionChartWidgetComponent extends BaseDataExplorerWidget<DistributionChartWidgetModel> implements OnInit {

  data = [
    {
      x: [],
      values: [],
      labels: [],
      type: 'pie'
    }
  ];

  graph = {
    layout: {
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

  transform(rows, index: number): any[] {
    return rows.map(row => row[index]);
  }

  prepareData(spQueryResult: SpQueryResult[]) {
    const series = spQueryResult[0];
    const finalLabels: string[] = [];
    const finalValues: number[] = [];
    const values: Map<string, number> = new Map();
    const field = this.dataExplorerWidget.visualizationConfig.selectedProperty;
    const index = this.getColumnIndex(field, series);
    const histoValues: number[] = [];

    if (series.total > 0) {
      const colValues = this.transform(series.allDataSeries[0].rows, index);
      colValues.forEach(value => {

      histoValues.push(value);

      if (field.fieldCharacteristics.numeric) {
        const roundingValue = this.dataExplorerWidget.visualizationConfig.roundingValue;
        value = Math.round(value / roundingValue) * roundingValue;
      }

      if (!values.has(value)) {
        values.set(value, 0);
      }
      const currentVal = values.get(value);
      values.set(value, currentVal + 1);

      });
    }
    values.forEach((value, key) => {
      finalLabels.push(key);
      finalValues.push(value);
    });

    if (this.dataExplorerWidget.visualizationConfig.displayType === 'pie') {
      this.data[0].values = finalValues;
      this.data[0].labels = finalLabels;
      this.data[0].type = 'pie';
    } else {
      this.data[0].x = histoValues;
      this.data[0].type = 'histogram';
    }
  }

  existsLabel(labels: string[],
              value: string) {
    return labels.indexOf(value) > -1;
  }

  updateAppearance() {
    this.graph.layout.paper_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.plot_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.font.color = this.dataExplorerWidget.baseAppearanceConfig.textColor;
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
  }

  handleUpdatedFields(addedFields: DataExplorerField[], removedFields: DataExplorerField[]) {
    this.dataExplorerWidget.visualizationConfig.selectedProperty =
      this.triggerFieldUpdate(this.dataExplorerWidget.visualizationConfig.selectedProperty, addedFields, removedFields);

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
