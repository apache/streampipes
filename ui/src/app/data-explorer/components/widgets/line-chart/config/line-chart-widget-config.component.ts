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
import { BaseWidgetConfig } from '../../base/base-widget-config';
import { LineCartVisConfig, LineChartWidgetModel } from '../model/line-chart-widget.model';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { EventPropertyUnion } from '../../../../../core-model/gen/streampipes-model';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { DataExplorerField } from '../../../../models/dataview-dashboard.model';
import { WidgetType } from '../../../../registry/data-explorer-widgets';

@Component({
  selector: 'sp-data-explorer-line-chart-widget-config',
  templateUrl: './line-chart-widget-config.component.html',
  styleUrls: ['./line-chart-widget-config.component.scss']
})
export class LineChartWidgetConfigComponent extends BaseWidgetConfig<LineChartWidgetModel, LineCartVisConfig> implements OnInit {

  constructor(widgetConfigurationService: WidgetConfigurationService,
              fieldService: DataExplorerFieldProviderService) {
    super(widgetConfigurationService, fieldService);
  }

  presetColors: string[] = ['#39B54A', '#1B1464', '#f44336', '#4CAF50', '#FFEB3B', '#FFFFFF', '#000000'];

  ngOnInit(): void {
    super.onInit();
  }

  setSelectedProperties(selectedColumns: DataExplorerField[]) {
    this.currentlyConfiguredWidget.visualizationConfig.selectedLineChartProperties = selectedColumns;
    console.log(selectedColumns);
    // this.currentlyConfiguredWidget.dataConfig.yKeys = this.getRuntimeNames(selectedColumns);
    this.triggerDataRefresh();
  }

  setSelectedBackgroundColorProperty(selectedBackgroundColorProperty: EventPropertyUnion) {
    if (selectedBackgroundColorProperty.runtimeName === '') {
      this.currentlyConfiguredWidget.visualizationConfig.selectedBackgroundColorProperty = undefined;
      this.currentlyConfiguredWidget.visualizationConfig.backgroundColorPropertyKey = undefined;
    } else {
      this.currentlyConfiguredWidget.visualizationConfig.selectedBackgroundColorProperty = selectedBackgroundColorProperty;
      this.currentlyConfiguredWidget.visualizationConfig.backgroundColorPropertyKey = selectedBackgroundColorProperty.runtimeName;
    }
    this.triggerDataRefresh();
  }

  // handlingAdvancedToggleChange() {
  //   this.currentlyConfiguredWidget.dataConfig.showBackgroundColorProperty =
  //     !this.currentlyConfiguredWidget.dataConfig.showBackgroundColorProperty;
  //   this.triggerDataRefresh();
  // }

  toggleLabelingMode() {
    // this.triggerViewRefresh();
  }

  protected getWidgetType(): WidgetType {
    return WidgetType.LineChart;
  }

  protected initWidgetConfig(): LineCartVisConfig {
    const colors = {};
    this.fieldProvider.numericFields.map((field, index) => {
      colors[field.runtimeName + field.sourceIndex] = this.presetColors[index];
    });
    return {
      forType: this.getWidgetType(),
      yKeys: [],
      chartMode: 'lines',
      selectedLineChartProperties: this.fieldProvider.numericFields.length > 6 ?
        this.fieldProvider.numericFields.slice(0, 5) :
        this.fieldProvider.numericFields,
      chosenColor: colors,
    };
  }

}
