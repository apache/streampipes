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
import { DataExplorerField } from '@streampipes/platform-services';
import { TimeSeriesChartWidgetModel } from '../../../widgets/time-series-chart/model/time-series-chart-widget.model';

@Component({
    selector: 'sp-time-series-item-config',
    templateUrl: './time-series-item-config.component.html',
    styleUrls: ['./time-series-item-config.component.scss'],
})
export class SpTimeseriesItemConfigComponent {
    @Input()
    field: DataExplorerField;

    @Input()
    currentlyConfiguredWidget: TimeSeriesChartWidgetModel;

    @Input()
    selectedProperties: DataExplorerField[];

    @Output()
    viewRefreshEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    configChangeEmitter: EventEmitter<void> = new EventEmitter<void>();

    presetColors: string[] = [
        '#39B54A',
        '#1B1464',
        '#f44336',
        '#4CAF50',
        '#FFEB3B',
        '#FFFFFF',
        '#000000',
    ];

    expanded: boolean = false;

    toggleFieldSelection(field: DataExplorerField) {
        if (this.isSelected(field)) {
            const index = this.selectedProperties.findIndex(
                sp =>
                    sp.fullDbName === field.fullDbName &&
                    sp.sourceIndex === field.sourceIndex,
            );
            this.selectedProperties.splice(index, 1);
        } else {
            this.selectedProperties.push(field);
        }
        this.configChangeEmitter.emit();
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

    toggleExpand(): void {
        this.expanded = !this.expanded;
    }

    onDisplayNameChange(searchValue: string, field: DataExplorerField): void {
        this.currentlyConfiguredWidget.visualizationConfig.displayName[
            field.fullDbName + field.sourceIndex.toString()
        ] = searchValue;
        this.viewRefreshEmitter.emit();
    }
}
