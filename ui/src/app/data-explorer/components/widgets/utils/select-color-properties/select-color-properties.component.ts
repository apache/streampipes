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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { DataExplorerField } from '@streampipes/platform-services';
import { TimeSeriesChartWidgetModel } from '../../time-series-chart/model/time-series-chart-widget.model';

@Component({
    selector: 'sp-select-color-properties',
    templateUrl: './select-color-properties.component.html',
    styleUrls: ['./select-color-properties.component.scss'],
})
export class SelectColorPropertiesComponent implements OnInit {
    @Output() changeSelectedProperties: EventEmitter<DataExplorerField[]> =
        new EventEmitter();

    @Input() availableProperties: DataExplorerField[];
    @Input() selectedProperties: DataExplorerField[];
    @Input() currentlyConfiguredWidget: TimeSeriesChartWidgetModel;

    constructor(
        protected widgetConfigurationService: WidgetConfigurationService,
    ) {}

    presetColors: string[] = [
        '#39B54A',
        '#1B1464',
        '#f44336',
        '#4CAF50',
        '#FFEB3B',
        '#FFFFFF',
        '#000000',
    ];

    ngOnInit(): void {
        if (!this.selectedProperties) {
            this.selectedProperties = [];
        }
    }

    triggerSelectedProperties() {
        this.changeSelectedProperties.emit(this.selectedProperties);
    }

    selectAllFields() {
        this.selectFields(true);
    }

    deselectAllFields() {
        this.selectFields(false);
    }

    selectFields(selected: boolean) {
        this.selectedProperties = selected ? this.availableProperties : [];
        this.triggerSelectedProperties();
    }

    isSelected(field: DataExplorerField): boolean {
        return (
            this.selectedProperties.find(
                sp =>
                    sp.fullDbName === field.fullDbName &&
                    sp.sourceIndex === field.sourceIndex,
            ) !== undefined
        );
    }

    toggleFieldSelection(field: DataExplorerField) {
        if (this.isSelected(field)) {
            this.selectedProperties = this.selectedProperties.filter(
                sp =>
                    !(
                        sp.fullDbName === field.fullDbName &&
                        sp.sourceIndex === field.sourceIndex
                    ),
            );
        } else {
            this.selectedProperties.push(field);
        }
        this.triggerSelectedProperties();
    }

    triggerViewRefresh() {
        this.widgetConfigurationService.notify({
            widgetId: this.currentlyConfiguredWidget._id,
            refreshData: false,
            refreshView: true,
        });
    }

    onFilterChange(searchValue: string, field: DataExplorerField): void {
        this.currentlyConfiguredWidget.visualizationConfig.displayName[
            field.fullDbName + field.sourceIndex.toString()
        ] = searchValue;
        this.triggerViewRefresh();
    }
}
