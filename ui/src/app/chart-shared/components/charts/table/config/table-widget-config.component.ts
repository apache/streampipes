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

import { Component } from '@angular/core';
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
import { MatFormField } from '@angular/material/form-field';
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
import {
    CdkDrag,
    CdkDragHandle,
    CdkDropList,
    CdkDragDrop,
    moveItemInArray,
} from '@angular/cdk/drag-drop';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';

@Component({
    selector: 'sp-data-explorer-table-widget-config',
    templateUrl: './table-widget-config.component.html',
    styleUrls: ['./table-widget-config.component.scss'],
    imports: [
        SpVisualizationConfigOuterComponent,
        SelectMultiplePropertiesConfigComponent,
        SplitSectionComponent,
        MatFormField,
        FlexDirective,
        MatInput,
        FormsModule,
        MatSelect,
        MatOption,
        ColorPickerDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatCheckbox,
        CdkDropList,
        CdkDrag,
        CdkDragHandle,
        MatIcon,
        MatIconButton,
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

    moveSelectedColumn(fromIndex: number, offset: number): void {
        const columns = [
            ...(this.currentlyConfiguredWidget.visualizationConfig
                .selectedColumns ?? []),
        ];
        const targetIndex = fromIndex + offset;

        if (
            fromIndex < 0 ||
            targetIndex < 0 ||
            fromIndex >= columns.length ||
            targetIndex >= columns.length
        ) {
            return;
        }

        const [movedColumn] = columns.splice(fromIndex, 1);
        columns.splice(targetIndex, 0, movedColumn);
        this.currentlyConfiguredWidget.visualizationConfig.selectedColumns =
            columns;
        this.triggerViewRefresh();
    }

    dropSelectedColumn(event: CdkDragDrop<DataExplorerField[]>): void {
        if (event.previousIndex === event.currentIndex) {
            return;
        }

        const columns = [
            ...(this.currentlyConfiguredWidget.visualizationConfig
                .selectedColumns ?? []),
        ];
        moveItemInArray(columns, event.previousIndex, event.currentIndex);
        this.currentlyConfiguredWidget.visualizationConfig.selectedColumns =
            columns;
        this.triggerViewRefresh();
    }

    setHighlightedColumns(highlightedColumns: DataExplorerField[]) {
        this.currentlyConfiguredWidget.visualizationConfig.highlightedColumns =
            highlightedColumns;
        this.syncHighlightColorMap();
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
        this.syncHighlightColorMap();
        config.pageSize ??= 20;
        config.stickyHeaders ??= true;
        config.searchValue ??= '';
    }

    protected requiredFieldsForChartPresent(): boolean {
        return true;
    }

    canMoveSelectedColumnUp(index: number): boolean {
        return index > 0;
    }

    canMoveSelectedColumnDown(index: number): boolean {
        return (
            index <
            (this.currentlyConfiguredWidget.visualizationConfig.selectedColumns
                ?.length ?? 0) -
                1
        );
    }

    selectedColumnLabel(field: DataExplorerField): string {
        return `${field.runtimeName} (${field.measure})`;
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
