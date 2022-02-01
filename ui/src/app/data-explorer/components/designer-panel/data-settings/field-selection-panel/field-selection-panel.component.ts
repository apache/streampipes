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

import { Component, Input, OnInit } from '@angular/core';
import { FieldConfig, SourceConfig } from '../../../../models/dataview-dashboard.model';
import { EventPropertyUnion } from '@streampipes/platform-services';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';

@Component({
  selector: 'sp-field-selection-panel',
  templateUrl: './field-selection-panel.component.html',
  styleUrls: ['./field-selection-panel.component.scss']
})
export class FieldSelectionPanelComponent implements OnInit {

  @Input() sourceConfig: SourceConfig;
  @Input() widgetId: string;

  expandFields = false;

  constructor(private fieldProvider: DataExplorerFieldProviderService,
              private widgetConfigService: WidgetConfigurationService) {}

  ngOnInit() {
    this.applyDefaultFields();
  }

  applyDefaultFields() {
    if (this.sourceConfig.queryConfig.fields.length === 0) {
      this.addAllFields();
    }
  }

  addAllFields() {
    this.sourceConfig.measure.eventSchema.eventProperties.forEach(property => {
      this.addField(property);
    });
  }

  selectAllFields() {
    this.selectFields(true);
  }

  deselectAllFields() {
    this.selectFields(false);
  }

  selectFields(selected: boolean) {
    this.sourceConfig.queryConfig.fields.forEach(field => field.selected = selected);
    this.widgetConfigService.notify({widgetId: this.widgetId, refreshData: true, refreshView: true});
  }

  addField(property: EventPropertyUnion) {
    const selection: FieldConfig = {runtimeName: property.runtimeName, selected: false, numeric: this.fieldProvider.isNumber(property)};
    selection.aggregations = [this.findDefaultAggregation(property)];
    this.sourceConfig.queryConfig.fields.push(selection);
  }

  findDefaultAggregation(property: EventPropertyUnion): string {
    if (this.fieldProvider.isNumber(property)) {
      return 'MEAN';
    } else if (this.fieldProvider.isBoolean(property)
        || this.fieldProvider.isDimensionProperty(property)
        || this.fieldProvider.isString(property)) {
      return 'MODE';
    }
  }

  toggleExpandFields() {
    this.expandFields = !this.expandFields;
  }

}

