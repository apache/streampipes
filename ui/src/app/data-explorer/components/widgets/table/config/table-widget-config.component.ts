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
import { EventPropertyUnion } from '../../../../../core-model/gen/streampipes-model';
import { BaseWidgetConfig } from '../../base/base-widget-config';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { TableWidgetModel } from '../model/table-widget.model';

@Component({
  selector: 'sp-data-explorer-table-widget-config',
  templateUrl: './table-widget-config.component.html',
  styleUrls: ['./table-widget-config.component.scss']
})
export class TableWidgetConfigComponent extends BaseWidgetConfig<TableWidgetModel> implements OnInit {

  constructor(widgetConfigurationService: WidgetConfigurationService) {
    super(widgetConfigurationService);
  }

  ngOnInit(): void {
    if (!this.currentlyConfiguredWidget.dataConfig.availableColumns) {
      this.currentlyConfiguredWidget.dataConfig.availableColumns = [this.getTimestampProperty(this.dataLakeMeasure.eventSchema)];
      this.currentlyConfiguredWidget.dataConfig.availableColumns =
        this.currentlyConfiguredWidget.dataConfig.availableColumns.concat(this.getValuePropertyKeys(this.dataLakeMeasure.eventSchema));

      // Reduce selected columns when more then 6
      this.currentlyConfiguredWidget.dataConfig.selectedColumns = this.currentlyConfiguredWidget.dataConfig.availableColumns.length > 6 ?
        this.currentlyConfiguredWidget.dataConfig.availableColumns.slice(0, 5) : this.currentlyConfiguredWidget.dataConfig.availableColumns;
      this.triggerDataRefresh();
    }
  }

  onFilterChange(searchValue: string): void {
    this.currentlyConfiguredWidget.dataConfig.searchValue = searchValue.trim().toLowerCase();
    this.triggerViewRefresh();
    // this.dataSource.filter = searchValue.trim().toLowerCase();
  }

  setSelectedColumn(selectedColumns: EventPropertyUnion[]) {
    this.currentlyConfiguredWidget.dataConfig.selectedColumns = selectedColumns;
    this.currentlyConfiguredWidget.dataConfig.columnNames = this.getRuntimeNames(this.currentlyConfiguredWidget.dataConfig.selectedColumns);
    this.triggerDataRefresh();
  }

  protected updateWidgetConfigOptions() {
  }


}
