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

import { Component, ChangeDetectionStrategy } from '@angular/core';
import { BaseWidgetConfig } from '../../base/base-widget-config';
import { TableVisConfig, TableWidgetModel } from '../model/table-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { SpVisualizationConfigOuterComponent } from '../../../chart-config/visualization-config-outer/visualization-config-outer.component';
import { SelectMultiplePropertiesConfigComponent } from '../../../chart-config/select-multiple-properties-config/select-multiple-properties-config.component';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { MatOption, MatSelect } from '@angular/material/select';
import { ColorPickerDirective } from 'ngx-color-picker';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FieldOrderConfigComponent } from '../../../chart-config/field-order-config/field-order-config.component';

@Component({
    selector: 'sp-data-explorer-table-widget-config',
    templateUrl: './table-widget-config.component.html',
    styleUrls: ['./table-widget-config.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        SpVisualizationConfigOuterComponent,
        SelectMultiplePropertiesConfigComponent,
        SplitSectionComponent,
        MatFormField,
        MatLabel,
        FlexDirective,
        MatInput,
        FormsModule,
        MatSelect,
        MatOption,
        ColorPickerDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatCheckbox,
        MatRadioGroup,
        MatRadioButton,
        FieldOrderConfigComponent,
        TranslatePipe,
        FormFieldComponent,
        SpAlertBannerComponent,
    ],
})
export class TableWidgetConfigComponent extends BaseWidgetConfig<
    TableWidgetModel,
    TableVisConfig
> {
    readonly pageSizeOptions = [10, 20, 50, 100, 250, 500];
    readonly presetColors = [
        '#39B54A',
        '#1B1464',
        '#2563EB',
        '#F59E0B',
        '#DC2626',
        '#14B8A6',
        '#9333EA',
    ];

    onFilterChange(searchValue: string): void {
        this.currentlyConfiguredWidget.visualizationConfig.searchValue =
            searchValue.trim().toLowerCase();
        this.triggerViewRefresh();
    }

    setSelectedColumn(selectedColumns: DataExplorerField[]) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedColumns =
            this.mergeSelectedColumnOrder(selectedColumns);
        this.triggerViewRefresh();
    }

    setSelectedColumnOrder(selectedColumns: DataExplorerField[]): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedColumns =
            selectedColumns;
        this.triggerViewRefresh();
    }

    setHighlightedColumns(highlightedColumns: DataExplorerField[]) {
        this.currentlyConfiguredWidget.visualizationConfig.highlightedColumns =
            highlightedColumns;
        this.syncHighlightColorMap();
        this.syncHighlightRangeMap();
        this.triggerViewRefresh();
    }

    setPageSize(pageSize: number): void {
        this.currentlyConfiguredWidget.visualizationConfig.pageSize = pageSize;
        this.triggerViewRefresh();
    }

    setStickyHeaders(stickyHeaders: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.stickyHeaders =
            stickyHeaders;
        this.triggerViewRefresh();
    }

    colorKey(field: DataExplorerField): string {
        return `${field.fullDbName}:${field.sourceIndex}`;
    }

    getHighlightColor(field: DataExplorerField): string {
        return (
            this.currentlyConfiguredWidget.visualizationConfig
                .highlightedColumnColors?.[this.colorKey(field)] ??
            this.defaultHighlightColor(field)
        );
    }

    setHighlightColor(field: DataExplorerField, color: string): void {
        this.currentlyConfiguredWidget.visualizationConfig.highlightedColumnColors[
            this.colorKey(field)
        ] = color;
        this.triggerViewRefresh();
    }

    getRangeMode(field: DataExplorerField): 'automatic' | 'custom' {
        return this.currentlyConfiguredWidget.visualizationConfig
            .highlightedColumnRanges?.[this.colorKey(field)]
            ? 'custom'
            : 'automatic';
    }

    setRangeMode(field: DataExplorerField, mode: string): void {
        const config = this.currentlyConfiguredWidget.visualizationConfig;
        config.highlightedColumnRanges ??= {};
        const key = this.colorKey(field);
        if (mode === 'custom') {
            config.highlightedColumnRanges[key] ??= {};
        } else {
            delete config.highlightedColumnRanges[key];
        }
        this.triggerViewRefresh();
    }

    getRangeValue(
        field: DataExplorerField,
        bound: 'min' | 'max',
    ): number | null {
        const value =
            this.currentlyConfiguredWidget.visualizationConfig
                .highlightedColumnRanges?.[this.colorKey(field)]?.[bound];
        return typeof value === 'number' ? value : null;
    }

    setRangeValue(
        field: DataExplorerField,
        bound: 'min' | 'max',
        value: number | null,
    ): void {
        const config = this.currentlyConfiguredWidget.visualizationConfig;
        config.highlightedColumnRanges ??= {};
        const key = this.colorKey(field);
        config.highlightedColumnRanges[key] ??= {};
        if (value === null || Number.isNaN(value)) {
            delete config.highlightedColumnRanges[key][bound];
        } else {
            config.highlightedColumnRanges[key][bound] = value;
        }
        this.triggerViewRefresh();
    }

    isRangeValid(field: DataExplorerField): boolean {
        const range =
            this.currentlyConfiguredWidget.visualizationConfig
                .highlightedColumnRanges?.[this.colorKey(field)];
        if (!range) return true;
        if (range.min === undefined && range.max === undefined) return true;
        return (
            typeof range.min === 'number' &&
            typeof range.max === 'number' &&
            range.min < range.max
        );
    }

    isNumericHighlightField(field: DataExplorerField): boolean {
        return (
            field.fieldCharacteristics.numeric &&
            !field.fieldCharacteristics.binary
        );
    }

    get highlightableFields(): DataExplorerField[] {
        return this.fieldProvider.allFields.filter(
            field =>
                field.fieldCharacteristics.numeric ||
                field.fieldCharacteristics.binary,
        );
    }

    isHighlighted(field: DataExplorerField): boolean {
        return !!(
            this.currentlyConfiguredWidget.visualizationConfig
                .highlightedColumns ?? []
        ).find(
            highlightedField =>
                highlightedField.fullDbName === field.fullDbName &&
                highlightedField.sourceIndex === field.sourceIndex,
        );
    }

    toggleHighlightedField(field: DataExplorerField): void {
        const highlightedColumns =
            this.currentlyConfiguredWidget.visualizationConfig
                .highlightedColumns ?? [];

        if (this.isHighlighted(field)) {
            this.currentlyConfiguredWidget.visualizationConfig.highlightedColumns =
                highlightedColumns.filter(
                    highlightedField =>
                        !(
                            highlightedField.fullDbName === field.fullDbName &&
                            highlightedField.sourceIndex === field.sourceIndex
                        ),
                );
        } else {
            this.currentlyConfiguredWidget.visualizationConfig.highlightedColumns =
                [...highlightedColumns, field];
        }

        this.syncHighlightColorMap();
        this.syncHighlightRangeMap();
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: TableVisConfig): void {
        config.selectedColumns = this.fieldService.getSelectedFields(
            config.selectedColumns,
            this.fieldProvider.allFields,
            () => {
                return this.fieldProvider.allFields.length > 6
                    ? this.fieldProvider.allFields.slice(0, 5)
                    : this.fieldProvider.allFields;
            },
        );
        config.highlightedColumns = this.fieldService.getSelectedFields(
            config.highlightedColumns ?? [],
            this.highlightableFields,
            () => [],
        );
        config.highlightedColumnColors ??= {};
        config.highlightedColumnRanges ??= {};
        this.syncHighlightColorMap();
        this.syncHighlightRangeMap();
        config.pageSize ??= 20;
        config.stickyHeaders ??= true;
        config.searchValue ??= '';
    }

    protected requiredFieldsForChartPresent(): boolean {
        return true;
    }

    private syncHighlightColorMap(): void {
        const activeColorKeys = new Set(
            (
                this.currentlyConfiguredWidget.visualizationConfig
                    .highlightedColumns ?? []
            ).map(field => this.colorKey(field)),
        );
        const currentColorMap =
            this.currentlyConfiguredWidget.visualizationConfig
                .highlightedColumnColors ?? {};

        const nextColorMap = Object.fromEntries(
            Object.entries(currentColorMap).filter(([key]) =>
                activeColorKeys.has(key),
            ),
        );

        (
            this.currentlyConfiguredWidget.visualizationConfig
                .highlightedColumns ?? []
        ).forEach(field => {
            const key = this.colorKey(field);
            nextColorMap[key] ??= this.defaultHighlightColor(field);
        });

        this.currentlyConfiguredWidget.visualizationConfig.highlightedColumnColors =
            nextColorMap;
    }

    private syncHighlightRangeMap(): void {
        const config = this.currentlyConfiguredWidget.visualizationConfig;
        const activeKeys = new Set(
            (config.highlightedColumns ?? []).map(field =>
                this.colorKey(field),
            ),
        );
        config.highlightedColumnRanges = Object.fromEntries(
            Object.entries(config.highlightedColumnRanges ?? {}).filter(
                ([key]) => activeKeys.has(key),
            ),
        );
    }

    private defaultHighlightColor(field: DataExplorerField): string {
        return this.presetColors[field.sourceIndex % this.presetColors.length];
    }

    fieldTypeLabel(field: DataExplorerField): string {
        return field.fieldCharacteristics.binary ? 'Boolean' : 'Numeric';
    }

    private mergeSelectedColumnOrder(
        nextSelectedColumns: DataExplorerField[],
    ): DataExplorerField[] {
        const currentSelectedColumns =
            this.currentlyConfiguredWidget.visualizationConfig
                .selectedColumns ?? [];

        const retainedColumns = currentSelectedColumns.filter(currentField =>
            nextSelectedColumns.some(nextField =>
                this.isSameField(currentField, nextField),
            ),
        );

        const newlyAddedColumns = nextSelectedColumns.filter(
            nextField =>
                !currentSelectedColumns.some(currentField =>
                    this.isSameField(currentField, nextField),
                ),
        );

        return [...retainedColumns, ...newlyAddedColumns];
    }

    private isSameField(a: DataExplorerField, b: DataExplorerField): boolean {
        return a.fullDbName === b.fullDbName && a.sourceIndex === b.sourceIndex;
    }
}
