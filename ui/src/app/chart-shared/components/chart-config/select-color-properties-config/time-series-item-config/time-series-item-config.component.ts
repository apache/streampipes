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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DataExplorerField } from '@streampipes/platform-services';
import {
    TimeSeriesChartWidgetModel,
    TimeSeriesGroupColorMapping,
    TimeSeriesGroupedColorMode,
} from '../../../charts/time-series-chart/model/time-series-chart-widget.model';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatCheckbox } from '@angular/material/checkbox';
import { NgStyle } from '@angular/common';
import { StyleDirective } from '@ngbracket/ngx-layout/extended';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { ColorPickerDirective } from 'ngx-color-picker';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { ColorMappingOptionsConfigComponent } from '../../color-mapping-options-config/color-mapping-options-config.component';
import { LayoutDirective } from '@ngbracket/ngx-layout';
import { ResultLabelService } from '../../../../services/result-label.service';

@Component({
    selector: 'sp-time-series-item-config',
    templateUrl: './time-series-item-config.component.html',
    styleUrls: ['./time-series-item-config.component.scss'],
    imports: [
        LayoutGapDirective,
        LayoutAlignDirective,
        MatCheckbox,
        FlexDirective,
        NgStyle,
        StyleDirective,
        MatIconButton,
        MatTooltip,
        LayoutGapDirective,
        ColorPickerDirective,
        MatFormField,
        MatInput,
        FormsModule,
        MatSelect,
        MatOption,
        TranslatePipe,
        ColorMappingOptionsConfigComponent,
        LayoutDirective,
    ],
})
export class SpTimeseriesItemConfigComponent {
    constructor(private resultLabelService: ResultLabelService) {}

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
        this.resultLabelService.setOverride(
            this.currentlyConfiguredWidget.dataConfig.sourceConfigs[
                field.sourceIndex
            ].queryConfig,
            field,
            searchValue,
            field.fullDbName,
        );
        this.viewRefreshEmitter.emit();
    }

    getFieldKey(field: DataExplorerField): string {
        return field.fullDbName + field.sourceIndex.toString();
    }

    getDisplayName(field: DataExplorerField): string {
        return this.resultLabelService.resolveLabel(
            this.currentlyConfiguredWidget.dataConfig.sourceConfigs[
                field.sourceIndex
            ].queryConfig,
            field,
            this.currentlyConfiguredWidget.visualizationConfig.displayName[
                this.getFieldKey(field)
            ],
        );
    }

    hasGrouping(field: DataExplorerField): boolean {
        return (
            this.currentlyConfiguredWidget.dataConfig.sourceConfigs[
                field.sourceIndex
            ]?.queryConfig.groupBy?.some(
                groupByField => groupByField.selected,
            ) ?? false
        );
    }

    getGroupedColorMode(field: DataExplorerField): TimeSeriesGroupedColorMode {
        this.currentlyConfiguredWidget.visualizationConfig.groupedColorMode ??=
            {};
        return (
            this.currentlyConfiguredWidget.visualizationConfig.groupedColorMode[
                this.getFieldKey(field)
            ] ?? 'stable_palette'
        );
    }

    setGroupedColorMode(
        field: DataExplorerField,
        mode: TimeSeriesGroupedColorMode,
    ): void {
        this.currentlyConfiguredWidget.visualizationConfig.groupedColorMode ??=
            {};
        this.currentlyConfiguredWidget.visualizationConfig.groupedColorMode[
            this.getFieldKey(field)
        ] = mode;
        this.viewRefreshEmitter.emit();
    }

    getGroupedColorMappings(
        field: DataExplorerField,
    ): TimeSeriesGroupColorMapping[] {
        const fieldKey = this.getFieldKey(field);
        this.currentlyConfiguredWidget.visualizationConfig.groupedColorMappings ??=
            {};
        const mappings =
            this.currentlyConfiguredWidget.visualizationConfig
                .groupedColorMappings[fieldKey];

        if (!mappings) {
            this.currentlyConfiguredWidget.visualizationConfig.groupedColorMappings[
                fieldKey
            ] = [];
            return this.currentlyConfiguredWidget.visualizationConfig
                .groupedColorMappings[fieldKey];
        }

        return mappings;
    }

    setGroupedColorMappings(
        field: DataExplorerField,
        mappings: TimeSeriesGroupColorMapping[],
    ): void {
        this.currentlyConfiguredWidget.visualizationConfig.groupedColorMappings ??=
            {};
        this.currentlyConfiguredWidget.visualizationConfig.groupedColorMappings[
            this.getFieldKey(field)
        ] = mappings;
        this.viewRefreshEmitter.emit();
    }
}
