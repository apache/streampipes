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
import {
    Component,
    ElementRef,
    HostListener,
    ViewChild,
    inject,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
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
import { WidgetNumberAppearanceConfig } from '../../../models/dataview-dashboard.model';

type SortDirection = 'asc' | 'desc' | '';

interface TableRow {
    __rowIndex: number;
    [key: string]: unknown;
}

interface NumericColumnStats {
    min: number;
    max: number;
}

interface AdvancedFilter {
    type: string;
    value: string;
    value2?: string;
}

const BLANKS_LABEL = '(Blanks)';
const DROPDOWN_MAX_WIDTH = 300;
const DROPDOWN_EDGE_PADDING = 3;

const NO_INPUT_TYPES = new Set(['Top 10', 'Above average', 'Below average']);

const NUMERIC_FILTER_OPTIONS = [
    'Equals',
    'Does not equal',
    'Greater than',
    'Greater than or equal to',
    'Less than',
    'Less than or equal to',
    'Between',
    'Top 10',
    'Above average',
    'Below average',
];

const TEXT_FILTER_OPTIONS = [
    'Equals',
    'Does not equal',
    'Begins with',
    'Ends with',
    'Contains',
    'Does not contain',
];

const TIMESTAMP_FILTER_OPTIONS = ['Before', 'After', 'Between'];
const TIMESTAMP_MASK = 'yyyy-mm-dd HH:mm:ss.SSS';

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
        MatCheckbox,
        FormsModule,
        DatePipe,
        TranslatePipe,
    ],
})
export class TableWidgetComponent extends BaseDataExplorerWidgetDirective<TableWidgetModel> {
    private elRef = inject(ElementRef);

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

    columnFilters: Record<string, Set<string>> = {};
    columnSearchTerms: Record<string, string> = {};
    openFilterColumn: string | null = null;
    advancedFilters: Record<string, AdvancedFilter> = {};
    showAdvancedPanel: string | null = null;
    advancedInputValue = '';
    advancedInputValue2 = '';
    selectedAdvancedType = '';
    dropdownStyle: Record<string, string> = {};
    filterListScrollEnd = true;

    @HostListener('document:click', ['$event'])
    onDocumentClick(event: MouseEvent): void {
        if (!this.openFilterColumn) return;
        const target = event.target as HTMLElement;
        const dropdown = this.elRef.nativeElement.querySelector(
            '.column-filter-dropdown',
        );
        const trigger = this.elRef.nativeElement.querySelector(
            '.column-filter-trigger.filter-open',
        );
        if (
            dropdown &&
            !dropdown.contains(target) &&
            (!trigger || !trigger.contains(target))
        ) {
            this.closeFilter();
        }
    }

    closeFilter(): void {
        this.openFilterColumn = null;
        this.showAdvancedPanel = null;
    }

    private numericColumnStats: Record<string, NumericColumnStats> = {};

    regenerateColumnNames(): void {
        const selected =
            this.dataExplorerWidget.visualizationConfig.selectedColumns ?? [];
        this.groupByColumnNames = this.makeGroupByColumns(selected);
        this.columnNames = Array.from(
            new Set([
                'time',
                ...selected.map(c => c.fullDbName),
                ...this.groupByColumnNames,
            ]),
        );
    }

    makeGroupByColumns(selectedColumns: DataExplorerField[]): string[] {
        return this.dataExplorerWidget.dataConfig.sourceConfigs.flatMap(sc =>
            (sc.queryConfig.groupBy ?? [])
                .filter(g => g.selected)
                .filter(
                    g =>
                        !selectedColumns.find(
                            c => c.runtimeName === g.runtimeName,
                        ),
                )
                .map(g => g.runtimeName),
        );
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

        if (tags) Object.assign(row, tags);

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
        } else {
            this.sortDirection = '';
            this.sortColumn = '';
        }
        this.applyTableState(false);
    }

    toggleColumnFilter(column: string, event: MouseEvent): void {
        event.stopPropagation();
        if (this.openFilterColumn === column) {
            this.closeFilter();
            return;
        }
        this.openFilterColumn = column;
        this.showAdvancedPanel = null;
        this.columnSearchTerms[column] =
            column === 'time'
                ? (this.columnSearchTerms[column] ?? TIMESTAMP_MASK)
                : (this.columnSearchTerms[column] ?? '');
        const rect = (
            event.currentTarget as HTMLElement
        ).getBoundingClientRect();
        const root = this.elRef.nativeElement.getBoundingClientRect();
        const leftRel = rect.left - root.left;
        const finalLeft =
            root.right - rect.left >= DROPDOWN_MAX_WIDTH + DROPDOWN_EDGE_PADDING
                ? leftRel
                : Math.max(
                      0,
                      leftRel -
                          (DROPDOWN_MAX_WIDTH - rect.width) -
                          DROPDOWN_EDGE_PADDING,
                  );
        this.dropdownStyle = {
            'position': 'absolute',
            'top': `${rect.bottom - root.top}px`,
            'left': `${finalLeft}px`,
            'z-index': '9999',
        };
        setTimeout(() => {
            const list = this.elRef.nativeElement.querySelector(
                '.column-filter-list',
            );
            this.filterListScrollEnd =
                !list || list.scrollWidth <= list.clientWidth;
        });
    }

    isColumnFilterOpen = (column: string): boolean =>
        this.openFilterColumn === column;

    hasActiveFilter(column: string): boolean {
        if (this.advancedFilters[column]) return true;
        const f = this.columnFilters[column];
        return !!f && f.size < this.getAllUniqueValues(column).length;
    }

    private uniqueValuesCache: Record<string, string[]> = {};

    getAllUniqueValues = (column: string): string[] =>
        (this.uniqueValuesCache[column] ??= this.extractUniqueValues(
            this.rows,
            column,
        ));

    getVisibleUniqueValues(column: string): string[] {
        let baseRows = this.getRowsFilteredByOtherColumns(column);
        const adv = this.advancedFilters[column];
        if (adv)
            baseRows = this.applyAdvancedFilterToRows(baseRows, column, adv);
        return this.extractUniqueValues(baseRows, column);
    }

    getLivePreviewValues(column: string): string[] {
        let baseRows = this.getRowsFilteredByOtherColumns(column);
        const adv = this.advancedFilters[column];
        if (adv)
            baseRows = this.applyAdvancedFilterToRows(baseRows, column, adv);
        if (
            this.showAdvancedPanel === column &&
            this.selectedAdvancedType &&
            (this.advancedInputValue ||
                !this.needsInput(this.selectedAdvancedType))
        ) {
            baseRows = this.applyAdvancedFilterToRows(baseRows, column, {
                type: this.selectedAdvancedType,
                value: this.advancedInputValue,
                value2: this.advancedInputValue2,
            });
        }
        return this.extractUniqueValues(baseRows, column);
    }

    getFilteredUniqueValues(column: string): string[] {
        const raw = this.columnSearchTerms[column] ?? '';
        const term = (column === 'time' ? this.getTimestampTyped(raw) : raw)
            .trim()
            .toLowerCase();
        const values =
            this.showAdvancedPanel === column
                ? this.getLivePreviewValues(column)
                : this.getVisibleUniqueValues(column);
        return term
            ? values.filter(v => v.toLowerCase().includes(term))
            : values;
    }

    isValueChecked = (column: string, value: string): boolean =>
        this.columnFilters[column]?.has(value) ?? true;

    toggleValue(column: string, value: string): void {
        this.ensureColumnFilter(column);
        const f = this.columnFilters[column];
        if (f.has(value)) {
            f.delete(value);
        } else {
            f.add(value);
        }
        this.applyTableState(true);
    }

    areAllValuesSelected(column: string): boolean {
        const f = this.columnFilters[column];
        if (!f) return true;
        return this.getAllUniqueValues(column).every(v => f.has(v));
    }

    toggleAllValues(column: string): void {
        this.ensureColumnFilter(column);
        const f = this.columnFilters[column];
        const all = this.getAllUniqueValues(column);
        const allSelected = all.every(v => f.has(v));
        all.forEach(v => (allSelected ? f.delete(v) : f.add(v)));
        this.applyTableState(true);
    }

    hasSearchOrAdvanced = (column: string): boolean =>
        !!(column === 'time'
            ? this.getTimestampTyped(this.columnSearchTerms[column] ?? '')
            : this.columnSearchTerms[column]?.trim()) ||
        this.showAdvancedPanel === column;

    areDisplayedValuesSelected(column: string): boolean {
        const f = this.columnFilters[column];
        if (!f) return true;
        return this.getFilteredUniqueValues(column).every(v => f.has(v));
    }

    toggleDisplayedValues(column: string): void {
        this.ensureColumnFilter(column);
        const f = this.columnFilters[column];
        const displayed = this.getFilteredUniqueValues(column);
        const allDisplayedSelected = displayed.every(v => f.has(v));
        displayed.forEach(v => (allDisplayedSelected ? f.delete(v) : f.add(v)));
        this.applyTableState(true);
    }

    onColumnSearchChange(column: string, term: string): void {
        this.columnSearchTerms[column] = term;
    }

    clearColumnFilter(column: string): void {
        delete this.columnFilters[column];
        delete this.advancedFilters[column];
        this.columnSearchTerms[column] = '';
        this.showAdvancedPanel = null;
        this.applyTableState(true);
    }

    getTimestampTyped(val: string): string {
        let last = -1;
        for (let i = 0; i < val.length; i++) {
            if (val[i] !== TIMESTAMP_MASK[i]) last = i;
        }
        return val.slice(0, last + 1);
    }

    getTimestampTemplate(val: string): string {
        let last = -1;
        for (let i = 0; i < val.length; i++) {
            if (val[i] !== TIMESTAMP_MASK[i]) last = i;
        }
        return val.slice(last + 1);
    }

    repositionToEnd(event: Event): void {
        const input = event.target as HTMLInputElement;
        setTimeout(() =>
            input.setSelectionRange(input.value.length, input.value.length),
        );
    }

    onTimestampSearchInput(event: Event): void {
        const input = event.target as HTMLInputElement;
        const digits = input.value.replace(/\D/g, '').slice(0, 17);
        const formatted = this.formatTimestampMask(digits);
        input.value = formatted;
        this.columnSearchTerms[this.openFilterColumn!] = formatted;
    }

    onSearchKeydown(event: KeyboardEvent): void {
        if (
            this.openFilterColumn === 'time' &&
            ['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)
        ) {
            event.preventDefault();
            return;
        }
        if (
            this.openFilterColumn === 'time' &&
            (event.key === 'Backspace' || event.key === 'Delete')
        ) {
            event.preventDefault();
            const digits = (this.columnSearchTerms[this.openFilterColumn] ?? '')
                .replace(/[^0-9]/g, '')
                .slice(0, -1);
            const formatted = this.formatTimestampMask(digits);
            this.columnSearchTerms[this.openFilterColumn] = formatted;
            (event.target as HTMLInputElement).value = formatted;
            return;
        }
        if (event.key === 'Enter') {
            event.preventDefault();
            this.closeFilter();
        }
    }

    onAdvancedInputKeydown(
        event: KeyboardEvent,
        column: string,
        inputIndex: number,
    ): void {
        if (
            column === 'time' &&
            ['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)
        ) {
            event.preventDefault();
            return;
        }
        if (
            column === 'time' &&
            (event.key === 'Backspace' || event.key === 'Delete')
        ) {
            event.preventDefault();
            const current =
                inputIndex === 1
                    ? this.advancedInputValue
                    : this.advancedInputValue2;
            const digits = current.replace(/[^0-9]/g, '').slice(0, -1);
            const formatted = this.formatTimestampMask(digits);
            if (inputIndex === 1) this.advancedInputValue = formatted;
            else this.advancedInputValue2 = formatted;
            (event.target as HTMLInputElement).value = formatted;
            return;
        }
        if (event.key !== 'Enter') return;
        event.preventDefault();
        if (
            inputIndex === 1 &&
            this.needsSecondInput(this.selectedAdvancedType)
        ) {
            this.elRef.nativeElement
                .querySelectorAll('.advanced-input')?.[1]
                ?.focus();
        } else {
            this.applyAdvancedFilter(column);
        }
    }

    onFilterListScroll(event: Event): void {
        const el = event.target as HTMLElement;
        this.filterListScrollEnd =
            el.scrollLeft + el.clientWidth >= el.scrollWidth - 2;
    }

    onFilterDropdownClick = (event: MouseEvent): void =>
        event.stopPropagation();

    @HostListener('document:keydown', ['$event'])
    handleGlobalKeydown(event: KeyboardEvent): void {
        if (!this.openFilterColumn) {
            return;
        }

        const key = event.key.toLowerCase();
        const ctrl = event.ctrlKey || event.metaKey;

        if (key === 'escape') {
            this.closeFilter();
            event.preventDefault();
            event.stopPropagation();
        } else if (ctrl && key === 'f') {
            const input = this.elRef.nativeElement.querySelector(
                '.column-filter-search',
            );
            if (input) {
                input.focus();
                input.select();
                event.preventDefault();
                event.stopPropagation();
            }
        }
    }

    onTimestampInput(field: 'value' | 'value2', event: Event): void {
        const input = event.target as HTMLInputElement;
        const digits = input.value.replace(/\D/g, '').slice(0, 17);
        const formatted = this.formatTimestampMask(digits);
        input.value = formatted;
        if (field === 'value') this.advancedInputValue = formatted;
        else this.advancedInputValue2 = formatted;
    }

    private formatTimestampMask(digits: string): string {
        const positions = [
            0, 1, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20, 21, 22,
        ];
        const result = TIMESTAMP_MASK.split('');
        positions.forEach((pos, i) => {
            if (i < digits.length) result[pos] = digits[i];
        });
        return result.join('');
    }

    private parseTimestampInput(s: string): number | undefined {
        const d = s.replace(/\D/g, '');
        if (d.length < 14) return undefined;
        const ms = d.length >= 17 ? d.slice(14, 17) : '000';
        const dt = new Date(
            `${d.slice(0, 4)}-${d.slice(4, 6)}-${d.slice(6, 8)}T${d.slice(8, 10)}:${d.slice(10, 12)}:${d.slice(12, 14)}.${ms}`,
        );
        return isNaN(dt.getTime()) ? undefined : dt.getTime();
    }

    getAdvancedFilterOptions = (column: string): string[] =>
        column === 'time'
            ? TIMESTAMP_FILTER_OPTIONS
            : this.isNumericColumn(column)
              ? NUMERIC_FILTER_OPTIONS
              : TEXT_FILTER_OPTIONS;

    getAdvancedFilterLabel = (column: string): string =>
        column === 'time'
            ? 'Timestamp Filters'
            : this.isNumericColumn(column)
              ? 'Number Filters'
              : 'Text Filters';

    toggleAdvancedPanel(column: string, event: MouseEvent): void {
        event.stopPropagation();
        if (this.showAdvancedPanel === column) {
            this.showAdvancedPanel = null;
            return;
        }
        this.showAdvancedPanel = column;
        const existing = this.advancedFilters[column];
        this.selectedAdvancedType = existing?.type ?? '';
        this.advancedInputValue =
            existing?.value ?? (column === 'time' ? TIMESTAMP_MASK : '');
        this.advancedInputValue2 =
            existing?.value2 ?? (column === 'time' ? TIMESTAMP_MASK : '');
    }

    selectAdvancedType(type: string): void {
        this.selectedAdvancedType = type;
        this.advancedInputValue =
            this.openFilterColumn === 'time' ? TIMESTAMP_MASK : '';
        this.advancedInputValue2 =
            this.openFilterColumn === 'time' ? TIMESTAMP_MASK : '';
    }

    needsInput = (type: string): boolean => !NO_INPUT_TYPES.has(type);
    needsSecondInput = (type: string): boolean => type === 'Between';

    applyAdvancedFilter(column: string): void {
        const isTimeCol = column === 'time';
        const validInput = (v: string): boolean =>
            isTimeCol ? this.parseTimestampInput(v) !== undefined : !!v.trim();
        if (
            this.needsInput(this.selectedAdvancedType) &&
            !validInput(this.advancedInputValue)
        ) {
            this.showAdvancedPanel = null;
            return;
        }
        if (
            this.needsSecondInput(this.selectedAdvancedType) &&
            !validInput(this.advancedInputValue2)
        ) {
            this.showAdvancedPanel = null;
            return;
        }
        this.advancedFilters[column] = {
            type: this.selectedAdvancedType,
            value: this.advancedInputValue,
            value2: this.advancedInputValue2,
        };
        this.showAdvancedPanel = null;
        this.applyTableState(true);
    }

    cancelAdvancedPanel(): void {
        this.showAdvancedPanel = null;
    }

    clearAdvancedFilter(column: string): void {
        delete this.advancedFilters[column];
        this.showAdvancedPanel = null;
        this.selectedAdvancedType = '';
        this.advancedInputValue = '';
        this.advancedInputValue2 = '';
        this.applyTableState(true);
    }

    hasAdvancedFilter = (column: string): boolean =>
        !!this.advancedFilters[column];

    private getRowsFilteredByOtherColumns(excludeColumn: string): TableRow[] {
        let result = [...this.rows];
        const search = (
            this.dataExplorerWidget.visualizationConfig.searchValue ?? ''
        )
            .trim()
            .toLowerCase();
        if (search) {
            result = result.filter(row =>
                this.columnNames.some(c =>
                    String(this.formatCellValue(c, row[c]))
                        .toLowerCase()
                        .includes(search),
                ),
            );
        }
        for (const col of this.columnNames) {
            if (col === excludeColumn) continue;
            const f = this.columnFilters[col];
            if (f && f.size < this.getAllUniqueValues(col).length) {
                result = result.filter(row =>
                    f.has(this.formatForFilter(row, col)),
                );
            }
            const adv = this.advancedFilters[col];
            if (adv)
                result = result.filter(row =>
                    this.passesAdvancedFilter(row, col, adv),
                );
        }
        return result;
    }

    private ensureColumnFilter(column: string): void {
        this.columnFilters[column] ??= new Set(this.getAllUniqueValues(column));
    }

    private initColumnFilters(): void {
        this.columnFilters = {};
        this.columnSearchTerms = {};
        this.advancedFilters = {};
        this.uniqueValuesCache = {};
        this.openFilterColumn = null;
        this.showAdvancedPanel = null;
    }

    private extractUniqueValues(rows: TableRow[], column: string): string[] {
        const seen = new Set(rows.map(r => this.formatForFilter(r, column)));
        return Array.from(seen).sort((a, b) =>
            a === BLANKS_LABEL
                ? 1
                : b === BLANKS_LABEL
                  ? -1
                  : a.localeCompare(b, undefined, { sensitivity: 'base' }),
        );
    }

    private formatForFilter(row: TableRow, column: string): string {
        const val = row[column];
        return val === null || val === undefined || val === ''
            ? BLANKS_LABEL
            : String(this.formatCellValue(column, val));
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

        this.initColumnFilters();
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
        ).filter(f => !removedFields.find(r => r.fullDbName === f.fullDbName));

        this.refreshView();
    }

    isNumericColumn = (column: string): boolean =>
        !!this.fieldProvider.numericFields.find(f => f.fullDbName === column);

    isHighlightedColumn = (column: string): boolean =>
        !!(
            this.dataExplorerWidget.visualizationConfig.highlightedColumns ?? []
        ).find(f => f.fullDbName === column);

    headerLabel = (column: string): string =>
        column === 'time' ? 'Time' : column;

    formatCellValue(column: string, value: unknown): unknown {
        if (column === 'time') {
            const d = new Date(value as string | number);
            if (!isNaN(d.getTime())) {
                const p = (n: number, l = 2): string =>
                    String(n).padStart(l, '0');
                return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}.${p(d.getMilliseconds(), 3)}`;
            }
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

    formatDisplayCellValue(column: string, value: unknown): unknown {
        if (this.isNumericColumn(column)) {
            return this.formatNumericValue(value);
        }

        return this.formatCellValue(column, value);
    }

    getCellStyle(row: TableRow, column: string): Record<string, string> {
        if (!this.isHighlightedColumn(column)) return {};
        const highlightValue = this.getHighlightStrength(row[column], column);
        if (highlightValue === undefined) return {};
        const intensity = Math.round(8 + highlightValue * 26);
        const color = this.getHighlightColor(column);
        return {
            background: `color-mix(in srgb, ${color} ${intensity}%, var(--color-bg-0))`,
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

    private formatNumericValue(value: unknown): unknown {
        const numericValue = this.toNumber(value);
        if (numericValue === undefined) {
            return this.formatCellValue('', value);
        }

        const decimals = this.getDecimals();
        return decimals === undefined
            ? String(numericValue)
            : numericValue.toFixed(decimals);
    }

    private getDecimals(): number | undefined {
        const appearanceConfig = this.dataExplorerWidget
            .baseAppearanceConfig as WidgetNumberAppearanceConfig;
        return this.normalizeDecimals(appearanceConfig?.numberFormat?.decimals);
    }

    private normalizeDecimals(decimals: unknown): number | undefined {
        if (decimals === null || decimals === undefined || decimals === '') {
            return undefined;
        }

        const parsedValue = Number(decimals);
        if (!Number.isFinite(parsedValue)) {
            return undefined;
        }

        return Math.min(10, Math.max(0, Math.round(parsedValue)));
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
        let result = [...rows];
        const search = (
            this.dataExplorerWidget.visualizationConfig.searchValue ?? ''
        )
            .trim()
            .toLowerCase();
        if (search) {
            result = result.filter(row =>
                this.columnNames.some(c =>
                    String(this.formatCellValue(c, row[c]))
                        .toLowerCase()
                        .includes(search),
                ),
            );
        }
        for (const col of this.columnNames) {
            const f = this.columnFilters[col];
            if (f && f.size < this.getAllUniqueValues(col).length) {
                result = result.filter(row =>
                    f.has(this.formatForFilter(row, col)),
                );
            }
            const adv = this.advancedFilters[col];
            if (adv) result = this.applyAdvancedFilterToRows(result, col, adv);
        }
        return result;
    }

    private applyAdvancedFilterToRows(
        rows: TableRow[],
        column: string,
        adv: AdvancedFilter,
    ): TableRow[] {
        if (adv.type === 'Top 10') {
            const top = rows
                .map(r => ({ r, n: this.toNumber(r[column]) }))
                .filter(
                    (e): e is { r: TableRow; n: number } => e.n !== undefined,
                )
                .sort((a, b) => b.n - a.n)
                .slice(0, 10);
            const set = new Set(top.map(e => e.r));
            return rows.filter(r => set.has(r));
        }
        if (adv.type === 'Above average' || adv.type === 'Below average') {
            const nums = rows
                .map(r => this.toNumber(r[column]))
                .filter((n): n is number => n !== undefined);
            if (!nums.length) return rows;
            const avg = nums.reduce((s, n) => s + n, 0) / nums.length;
            return rows.filter(r => {
                const n = this.toNumber(r[column]);
                return (
                    n !== undefined &&
                    (adv.type === 'Above average' ? n > avg : n < avg)
                );
            });
        }
        return rows.filter(r => this.passesAdvancedFilter(r, column, adv));
    }

    private passesAdvancedFilter(
        row: TableRow,
        column: string,
        adv: AdvancedFilter,
    ): boolean {
        const raw = row[column];
        if (column === 'time') {
            const n = new Date(raw as string | number).getTime();
            const t1 = this.parseTimestampInput(adv.value);
            const t2 = this.parseTimestampInput(adv.value2 ?? '');
            switch (adv.type) {
                case 'Before':
                    return t1 !== undefined && n <= t1;
                case 'After':
                    return t1 !== undefined && n >= t1;
                case 'Between':
                    return (
                        t1 !== undefined &&
                        t2 !== undefined &&
                        n >= Math.min(t1, t2) &&
                        n <= Math.max(t1, t2)
                    );
                default:
                    return true;
            }
        }
        if (this.isNumericColumn(column)) {
            const n = this.toNumber(raw);
            const t1 = Number(adv.value);
            const t2 = Number(adv.value2);
            switch (adv.type) {
                case 'Equals':
                    return n === t1;
                case 'Does not equal':
                    return n !== t1;
                case 'Greater than':
                    return n !== undefined && n > t1;
                case 'Greater than or equal to':
                    return n !== undefined && n >= t1;
                case 'Less than':
                    return n !== undefined && n < t1;
                case 'Less than or equal to':
                    return n !== undefined && n <= t1;
                case 'Between':
                    return (
                        n !== undefined &&
                        n >= Math.min(t1, t2) &&
                        n <= Math.max(t1, t2)
                    );
                default:
                    return true;
            }
        }
        const s = String(this.formatCellValue(column, raw)).toLowerCase();
        const t = adv.value.toLowerCase();
        switch (adv.type) {
            case 'Equals':
                return s === t;
            case 'Does not equal':
                return s !== t;
            case 'Begins with':
                return s.startsWith(t);
            case 'Ends with':
                return s.endsWith(t);
            case 'Contains':
                return s.includes(t);
            case 'Does not contain':
                return !s.includes(t);
            default:
                return true;
        }
    }

    private sortRows(rows: TableRow[]): TableRow[] {
        if (!this.sortColumn || this.sortDirection === '') return [...rows];
        const dir = this.sortDirection === 'asc' ? 1 : -1;
        return [...rows].sort(
            (a, b) =>
                this.compareValues(
                    a[this.sortColumn],
                    b[this.sortColumn],
                    this.sortColumn,
                ) * dir,
        );
    }

    private compareValues(
        valueA: unknown,
        valueB: unknown,
        column: string,
    ): number {
        const a = this.normalizeSortValue(valueA, column);
        const b = this.normalizeSortValue(valueB, column);
        if (a === b) return 0;
        if (a === null) return 1;
        if (b === null) return -1;
        return a > b ? 1 : -1;
    }

    private normalizeSortValue(
        value: unknown,
        column: string,
    ): number | string | null {
        if (value === null || value === undefined || value === '') return null;
        if (column === 'time') {
            const t = new Date(value as string | number).getTime();
            return Number.isNaN(t) ? null : t;
        }
        const n = this.toNumber(value);
        if (n !== undefined) return n;
        if (typeof value === 'boolean') return value ? 1 : 0;
        return String(value).toLowerCase();
    }

    private computeNumericStats(
        rows: TableRow[],
    ): Record<string, NumericColumnStats> {
        const columns = (
            this.dataExplorerWidget.visualizationConfig.highlightedColumns ?? []
        ).map(f => f.fullDbName);
        return columns.reduce(
            (stats, col) => {
                const values = rows
                    .map(r => this.toNumber(r[col]))
                    .filter((v): v is number => v !== undefined);
                if (values.length > 0) {
                    stats[col] = {
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
        ).find(f => f.fullDbName === column);
        return (
            this.dataExplorerWidget.visualizationConfig
                .highlightedColumnColors?.[
                `${field?.fullDbName}:${field?.sourceIndex}`
            ] ?? 'var(--color-primary)'
        );
    }

    private getHighlightStrength(
        value: unknown,
        column: string,
    ): number | undefined {
        const bool = this.toBoolean(value);
        if (bool !== undefined) return bool ? 1 : 0;
        const n = this.toNumber(value);
        const stats = this.numericColumnStats[column];
        if (n === undefined || !stats) return undefined;
        return stats.max === stats.min
            ? 0.5
            : (n - stats.min) / (stats.max - stats.min);
    }

    private toNumber(value: unknown): number | undefined {
        if (typeof value === 'number' && Number.isFinite(value)) return value;
        if (typeof value === 'string' && value.trim() !== '') {
            const n = Number(value);
            return Number.isFinite(n) ? n : undefined;
        }
        return undefined;
    }

    private toBoolean(value: unknown): boolean | undefined {
        if (typeof value === 'boolean') return value;
        if (typeof value === 'string') {
            const v = value.trim().toLowerCase();
            return v === 'true' ? true : v === 'false' ? false : undefined;
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
        const start = this.pageIndex * this.pageSize;
        this.pagedRows = this.filteredRows.slice(start, start + this.pageSize);
    }
}
