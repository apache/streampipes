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
import { PieChartWidgetModel } from './model/pie-chart-widget.model';
import { DataViewQueryGeneratorService } from '../../../services/data-view-query-generator.service';
import { DataExplorerFieldProviderService } from '../../../services/data-explorer-field-provider-service';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';

@Component({
  selector: 'sp-data-explorer-pie-chart-widget',
  templateUrl: './pie-chart-widget.component.html',
  styleUrls: ['./pie-chart-widget.component.scss']
})
export class PieChartWidgetComponent extends BaseDataExplorerWidget<PieChartWidgetModel> implements OnInit {

  data = [
    {
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

  constructor(dataLakeRestService: DatalakeRestService,
              widgetConfigurationService: WidgetConfigurationService,
              resizeService: ResizeService,
              queryGenerator: DataViewQueryGeneratorService,
              fieldProvider: DataExplorerFieldProviderService) {
    super(dataLakeRestService, widgetConfigurationService, resizeService, queryGenerator, fieldProvider);
  }

  refreshView() {
    this.updateAppearance();
  }

  prepareData(results: DataResult[]) {
    const finalLabels: string[] = [];
    const finalValues: number[] = [];
    const values: Map<string, number> = new Map();
    const field = this.dataExplorerWidget.visualizationConfig.selectedProperty;
    const index = field.sourceIndex;

    if (results[index]) {
      const rowIndex = this.getColumnIndex(field, results[index]);
      results[index].rows.forEach(row => {
        const value = row[rowIndex].toString();
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
    this.data[0].labels = finalLabels;
    this.data[0].values = finalValues;
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

  onDataReceived(dataResults: DataResult[]) {
    this.prepareData(dataResults);
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
