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
import { EventPropertyUnion } from '../../../../../core-model/gen/streampipes-model';
import { FilterCondition, PieChartWidgetModel } from '../model/pie-chart-widget.model';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'sp-data-explorer-pie-chart-widget-config',
  templateUrl: './pie-chart-widget-config.component.html',
  styleUrls: ['./pie-chart-widget-config.component.scss']
})
export class PieWidgetConfigComponent extends BaseWidgetConfig<PieChartWidgetModel> implements OnInit {

  ngOnInit(): void {
    this.updateWidgetConfigOptions();
  }

  setSelectedProperties(selectedColumn: MatSelectChange) {
    this.currentlyConfiguredWidget.dataConfig.selectedFilters.forEach(f => f.field = selectedColumn.value);
    this.triggerDataRefresh();
  }

  protected updateWidgetConfigOptions() {
    if (this.dataLakeMeasure.measureName && !this.currentlyConfiguredWidget.dataConfig.availableProperties) {
      this.currentlyConfiguredWidget.dataConfig.availableProperties = this.dataLakeMeasure.eventSchema.eventProperties;
      this.currentlyConfiguredWidget.dataConfig.selectedFilters = [];
    }
  }

  addFilter() {
    const newFilter = {
      number: this.currentlyConfiguredWidget.dataConfig.selectedFilters.length,
      field: this.currentlyConfiguredWidget.dataConfig.selectedProperty,
      operator: '='
    } as unknown as FilterCondition;
    this.currentlyConfiguredWidget.dataConfig.selectedFilters.push(newFilter);
  }

  remove(index: number) {
    this.currentlyConfiguredWidget.dataConfig.selectedFilters.splice(index, 1);
  }

}
