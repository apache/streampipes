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

import { DatePipe, NgClass, NgStyle } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import { TableWidgetModel } from './model/table-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { NoDataInDateRangeComponent } from '../base/no-data/no-data-in-date-range.component';
import { TooMuchDataComponent } from '../base/too-much-data/too-much-data.component';
import { TranslatePipe } from '@ngx-translate/core';

type SortDirection = 'asc' | 'desc' | '';

interface TableRow {
    __rowIndex: number;
    [key: string]: unknown;
}

interface NumericColumnStats {
    min: number;
    max: number;
}

@Component({
    selector: 'sp-data-explorer-table-widget',
    templateUrl: './table-widget.component.html',
    styleUrls: ['./table-widget.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        NgStyle,
        NgClass,
        NoDataInDateRangeComponent,
        TooMuchDataComponent,
        MatPaginator,
        MatIcon,
        DatePipe,
        TranslatePipe,
    ],
})
export class TableWidgetComponent extends BaseDataExplorerWidgetDirective<TableWidgetModel> {
    private static readonly DEFAULT_PAGE_SIZE = 20;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    readonly pageSizeOptions = [10, 20, 50, 100, 250, 500];

    rows: TableRow[] = [];
    filteredRows: TableRow[] = [];
    pagedRows: TableRow[] = [];
    columnNames: string[] = [];
    groupByColumnNames: string[] = [];
    pageSize = TableWidgetComponent.DEFAULT_PAGE_SIZE;
    pageIndex = 0;
    sortColumn = '';
    sortDirection: SortDirection = '';

    private numericColumnStats: Record<string, NumericColumnStats> = {};

    regenerateColumnNames(): void {
        this.groupByColumnNames = this.makeGroupByColumns(
            this.dataExplorerWidget.visualizationConfig.selectedColumns ?? [],
        );

        this.columnNames = Array.from(
            new Set([
                'time',
                ...(
                    this.dataExplorerWidget.visualizationConfig
                        .selectedColumns ?? []
                ).map(column => column.fullDbName),
                ...this.groupByColumnNames,
            ]),
        );
    }

    makeGroupByColumns(selectedColumns: DataExplorerField[]): string[] {
        return this.dataExplorerWidget.dataConfig.sourceConfigs.flatMap(sc => {
            return (sc.queryConfig.groupBy ?? [])
                .filter(groupBy => groupBy.selected)
                .filter(
                    groupBy =>
                        selectedColumns.find(
                            column =>
                                column.runtimeName === groupBy.runtimeName,
                        ) === undefined,
                )
                .map(groupBy => groupBy.runtimeName);
        });
    }

    transformData(spQueryResult: SpQueryResult, rowOffset: number): TableRow[] {
        let nextRowIndex = rowOffset;
        return spQueryResult.allDataSeries.flatMap(series =>
            series.rows.map(row =>
                this.createTableObject(
                    spQueryResult.headers,
                    row,
                    series.tags,
                    nextRowIndex++,
                ),
            ),
        );
    }

    createTableObject(
        keys: string[],
        values: unknown[],
        tags: Record<string, string>,
        rowIndex: number,
    ): TableRow {
        const row = keys.reduce(
            (object, key, index) => {
                object[key] = values[index];
                return object;
            },
            { __rowIndex: rowIndex } as TableRow,
        );

        if (tags) {
            Object.keys(tags).forEach(key => {
                row[key] = tags[key];
            });
        }

        return row;
    }

    onPage(event: PageEvent): void {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        this.dataExplorerWidget.visualizationConfig.pageSize = event.pageSize;
        this.updatePagedRows();
    }

    sortBy(column: string): void {
        if (this.sortColumn !== column) {
            this.sortColumn = column;
            this.sortDirection = 'asc';
        } else if (this.sortDirection === 'asc') {
            this.sortDirection = 'desc';
        } else if (this.sortDirection === 'desc') {
            this.sortDirection = '';
            this.sortColumn = '';
        } else {
            this.sortDirection = 'asc';
        }

        this.applyTableState(false);
    }

    sortIcon(column: string): string {
        if (this.sortColumn !== column || this.sortDirection === '') {
            return 'unfold_more';
        }

        return this.sortDirection === 'asc' ? 'north' : 'south';
    }

    public refreshView() {
        this.ensureDefaults();
        this.regenerateColumnNames();
        this.applyTableState(true);
    }

    onResize(_width: number, _height: number) {}

    beforeDataFetched() {}

    onDataReceived(spQueryResults: SpQueryResult[]) {
        this.ensureDefaults();
        this.regenerateColumnNames();

        let rowOffset = 1;
        this.rows = spQueryResults.flatMap(spQueryResult => {
            const transformedRows = this.transformData(
                spQueryResult,
                rowOffset,
            );
            rowOffset += transformedRows.length;
            return transformedRows;
        });

        this.applyTableState(true);
        this.setShownComponents(false, true, false, false);
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        const fieldUpdateInfo = {
            addedFields,
            removedFields,
            fieldProvider: this.fieldProvider,
        };

        this.dataExplorerWidget.visualizationConfig.selectedColumns =
            this.fieldUpdateService.updateFieldSelection(
                this.dataExplorerWidget.visualizationConfig.selectedColumns ??
                    [],
                fieldUpdateInfo,
                () => true,
            );

        this.dataExplorerWidget.visualizationConfig.highlightedColumns = (
            this.dataExplorerWidget.visualizationConfig.highlightedColumns ?? []
        ).filter(
            field =>
                !removedFields.find(
                    removedField =>
                        removedField.fullDbName === field.fullDbName,
                ),
        );

        this.refreshView();
    }

    isNumericColumn(column: string): boolean {
        return !!this.fieldProvider.numericFields.find(
            field => field.fullDbName === column,
        );
    }

    isHighlightedColumn(column: string): boolean {
        return !!(
            this.dataExplorerWidget.visualizationConfig.highlightedColumns ?? []
        ).find(field => field.fullDbName === column);
    }

    headerLabel(column: string): string {
        return column === 'time' ? 'Time' : column;
    }

    formatCellValue(column: string, value: unknown): unknown {
        if (column === 'time') {
            return value;
        }

        if (typeof value === 'object' && value !== null) {
            try {
                return JSON.stringify(value);
            } catch {
                return String(value);
            }
        }

        return value ?? '';
    }

    getCellStyle(row: TableRow, column: string): Record<string, string> {
        if (!this.isHighlightedColumn(column)) {
            return {};
        }

        const highlightValue = this.getHighlightStrength(row[column], column);
        if (highlightValue === undefined) {
            return {};
        }
        const intensity = Math.round(8 + highlightValue * 26);
        const highlightColor = this.getHighlightColor(column);

        return {
            background: `color-mix(in srgb, ${highlightColor} ${intensity}%, var(--color-bg-0))`,
        };
    }

    trackColumn(_index: number, column: string): string {
        return column;
    }

    trackRow(_index: number, row: TableRow): number {
        return row.__rowIndex;
    }

    private ensureDefaults(): void {
        this.dataExplorerWidget.visualizationConfig.searchValue ??= '';
        this.dataExplorerWidget.visualizationConfig.highlightedColumns ??= [];
        this.dataExplorerWidget.visualizationConfig.highlightedColumnColors ??=
            {};
        this.dataExplorerWidget.visualizationConfig.pageSize ??=
            TableWidgetComponent.DEFAULT_PAGE_SIZE;

        this.pageSize = this.pageSizeOptions.includes(
            this.dataExplorerWidget.visualizationConfig.pageSize,
        )
            ? this.dataExplorerWidget.visualizationConfig.pageSize
            : TableWidgetComponent.DEFAULT_PAGE_SIZE;
    }

    private applyTableState(resetPageIndex: boolean): void {
        this.filteredRows = this.filterRows(this.rows);
        this.numericColumnStats = this.computeNumericStats(this.filteredRows);
        this.filteredRows = this.sortRows(this.filteredRows);

        if (resetPageIndex) {
            this.pageIndex = 0;
            this.paginator?.firstPage();
        }

        this.ensureValidPageIndex();
        this.updatePagedRows();
    }

    private filterRows(rows: TableRow[]): TableRow[] {
        const searchTerm = (
            this.dataExplorerWidget.visualizationConfig.searchValue ?? ''
        )
            .trim()
            .toLowerCase();

        if (!searchTerm) {
            return [...rows];
        }

        return rows.filter(row =>
            this.columnNames.some(column =>
                String(this.formatCellValue(column, row[column]))
                    .toLowerCase()
                    .includes(searchTerm),
            ),
        );
    }

    private sortRows(rows: TableRow[]): TableRow[] {
        if (!this.sortColumn || this.sortDirection === '') {
            return [...rows];
        }

        const directionMultiplier = this.sortDirection === 'asc' ? 1 : -1;
        return [...rows].sort((rowA, rowB) => {
            const comparison = this.compareValues(
                rowA[this.sortColumn],
                rowB[this.sortColumn],
                this.sortColumn,
            );

            return comparison * directionMultiplier;
        });
    }

    private compareValues(
        valueA: unknown,
        valueB: unknown,
        column: string,
    ): number {
        const normalizedA = this.normalizeSortValue(valueA, column);
        const normalizedB = this.normalizeSortValue(valueB, column);

        if (normalizedA === normalizedB) {
            return 0;
        }

        if (normalizedA === null) {
            return 1;
        }

        if (normalizedB === null) {
            return -1;
        }

        return normalizedA > normalizedB ? 1 : -1;
    }

    private normalizeSortValue(
        value: unknown,
        column: string,
    ): number | string | null {
        if (value === null || value === undefined || value === '') {
            return null;
        }

        if (column === 'time') {
            const timestamp = new Date(value as string | number).getTime();
            return Number.isNaN(timestamp) ? null : timestamp;
        }

        const numericValue = this.toNumber(value);
        if (numericValue !== undefined) {
            return numericValue;
        }

        if (typeof value === 'boolean') {
            return value ? 1 : 0;
        }

        return String(value).toLowerCase();
    }

    private computeNumericStats(
        rows: TableRow[],
    ): Record<string, NumericColumnStats> {
        return (
            this.dataExplorerWidget.visualizationConfig.highlightedColumns ?? []
        )
            .map(field => field.fullDbName)
            .reduce(
                (stats, column) => {
                    const values = rows
                        .map(row => this.toNumber(row[column]))
                        .filter(
                            (value): value is number => value !== undefined,
                        );

                    if (values.length > 0) {
                        stats[column] = {
                            min: Math.min(...values),
                            max: Math.max(...values),
                        };
                    }

                    return stats;
                },
                {} as Record<string, NumericColumnStats>,
            );
    }

    private getHighlightColor(column: string): string {
        const field = (
            this.dataExplorerWidget.visualizationConfig.highlightedColumns ?? []
        ).find(highlightedField => highlightedField.fullDbName === column);

        if (!field) {
            return 'var(--color-primary)';
        }

        return (
            this.dataExplorerWidget.visualizationConfig
                .highlightedColumnColors?.[
                `${field.fullDbName}:${field.sourceIndex}`
            ] ?? 'var(--color-primary)'
        );
    }

    private getHighlightStrength(
        value: unknown,
        column: string,
    ): number | undefined {
        const booleanValue = this.toBoolean(value);
        if (booleanValue !== undefined) {
            return booleanValue ? 1 : 0;
        }

        const numericValue = this.toNumber(value);
        const stats = this.numericColumnStats[column];
        if (numericValue === undefined || !stats) {
            return undefined;
        }

        return stats.max === stats.min
            ? 0.5
            : (numericValue - stats.min) / (stats.max - stats.min);
    }

    private toNumber(value: unknown): number | undefined {
        if (typeof value === 'number' && Number.isFinite(value)) {
            return value;
        }

        if (typeof value === 'string' && value.trim() !== '') {
            const numericValue = Number(value);
            return Number.isFinite(numericValue) ? numericValue : undefined;
        }

        return undefined;
    }

    private toBoolean(value: unknown): boolean | undefined {
        if (typeof value === 'boolean') {
            return value;
        }

        if (typeof value === 'string') {
            const normalizedValue = value.trim().toLowerCase();
            if (normalizedValue === 'true') {
                return true;
            }
            if (normalizedValue === 'false') {
                return false;
            }
        }

        return undefined;
    }

    private ensureValidPageIndex(): void {
        const maxPageIndex =
            this.filteredRows.length > 0
                ? Math.floor((this.filteredRows.length - 1) / this.pageSize)
                : 0;
        this.pageIndex = Math.min(this.pageIndex, maxPageIndex);
    }

    private updatePagedRows(): void {
        const startIndex = this.pageIndex * this.pageSize;
        this.pagedRows = this.filteredRows.slice(
            startIndex,
            startIndex + this.pageSize,
        );
    }
}
