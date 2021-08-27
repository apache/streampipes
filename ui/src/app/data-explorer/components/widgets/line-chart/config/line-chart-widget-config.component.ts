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
import { LineChartWidgetModel } from '../model/line-chart-widget.model';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { EventPropertyUnion } from '../../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-data-explorer-line-chart-widget-config',
  templateUrl: './line-chart-widget-config.component.html',
  styleUrls: ['./line-chart-widget-config.component.scss']
})
export class LineChartWidgetConfigComponent extends BaseWidgetConfig<LineChartWidgetModel> implements OnInit {

  constructor(widgetConfigurationService: WidgetConfigurationService) {
    super(widgetConfigurationService);
  }

  ngOnInit(): void {
    this.updateWidgetConfigOptions();
  }

  setSelectedProperties(selectedColumns: EventPropertyUnion[]) {
    this.currentlyConfiguredWidget.dataConfig.selectedLineChartProperties = selectedColumns;
    this.currentlyConfiguredWidget.dataConfig.yKeys = this.getRuntimeNames(selectedColumns);
    this.triggerDataRefresh();
  }

  setSelectedBackgroundColorProperty(selectedBackgroundColorProperty: EventPropertyUnion) {
    if (selectedBackgroundColorProperty.runtimeName === '') {
      this.currentlyConfiguredWidget.dataConfig.selectedBackgroundColorProperty = undefined;
      this.currentlyConfiguredWidget.dataConfig.backgroundColorPropertyKey = undefined;
    } else {
      this.currentlyConfiguredWidget.dataConfig.selectedBackgroundColorProperty = selectedBackgroundColorProperty;
      this.currentlyConfiguredWidget.dataConfig.backgroundColorPropertyKey = selectedBackgroundColorProperty.runtimeName;
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

  protected updateWidgetConfigOptions() {
    if (this.dataLakeMeasure.measureName && !this.currentlyConfiguredWidget.dataConfig.availableProperties) {
      this.currentlyConfiguredWidget.dataConfig.availableProperties = this.getNumericProperty(this.dataLakeMeasure.eventSchema);
      this.currentlyConfiguredWidget.dataConfig.dimensionProperties = this.getDimensionProperties(this.dataLakeMeasure.eventSchema);
      this.currentlyConfiguredWidget.dataConfig.availableNonNumericColumns = this.getNonNumericProperties(this.dataLakeMeasure.eventSchema);
      this.currentlyConfiguredWidget.dataConfig.yKeys = [];
      this.currentlyConfiguredWidget.dataConfig.chartMode = 'lines';
      this.currentlyConfiguredWidget.dataConfig.autoAggregate = true;

      // Reduce selected columns when more then 6
      this.currentlyConfiguredWidget.dataConfig.selectedLineChartProperties =
        this.currentlyConfiguredWidget.dataConfig.availableProperties.length > 6 ?
          this.currentlyConfiguredWidget.dataConfig.availableProperties.slice(0, 5) :
          this.currentlyConfiguredWidget.dataConfig.availableProperties;
      this.currentlyConfiguredWidget.dataConfig.xKey = this.getTimestampProperty(this.dataLakeMeasure.eventSchema).runtimeName;
      this.currentlyConfiguredWidget.dataConfig.yKeys =
        this.getRuntimeNames(this.currentlyConfiguredWidget.dataConfig.selectedLineChartProperties);
    }
  }
}
