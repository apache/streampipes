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

import { DatePipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    HostBinding,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpQueryResult } from '@streampipes/platform-services';
import { TranslatePipe } from '@ngx-translate/core';

type PreviewRow = Record<string, unknown>;

@Component({
    selector: 'sp-chart-data-preview',
    templateUrl: './chart-data-preview.component.html',
    styleUrls: ['./chart-data-preview.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        MatIconButton,
        MatIcon,
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
    ],
})
export class ChartDataPreviewComponent implements OnChanges {
    private static readonly MAX_PREVIEW_ROWS = 2000;
    private readonly datePipe = new DatePipe('en-US');

    @Input() queryResults: SpQueryResult[] = [];
    @Input() defaultExpanded = false;

    columns: string[] = [];
    rows: PreviewRow[] = [];
    totalRows = 0;
    allRowsRendered = true;
    expanded = false;

    @HostBinding('style.height')
    get hostHeight(): string {
        return this.expanded
            ? 'var(--size-data-preview-expanded)'
            : 'var(--size-data-preview-collapsed)';
    }

    @HostBinding('class.expanded')
    get hostExpandedClass(): boolean {
        return this.expanded;
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.defaultExpanded) {
            this.expanded = !!this.defaultExpanded;
        }
        if (changes.queryResults) {
            this.rebuildPreview(this.queryResults ?? []);
        }
    }

    private rebuildPreview(queryResults: SpQueryResult[]): void {
        const headerColumns: string[] = [];
        const tagColumns: string[] = [];
        const flattenedRows: PreviewRow[] = [];
        const showSourceColumn = queryResults.length > 1;

        this.totalRows = 0;

        queryResults.forEach(queryResult => {
            queryResult.headers?.forEach(header =>
                this.pushUnique(headerColumns, header),
            );

            queryResult.allDataSeries?.forEach(series => {
                const tags = series.tags ?? {};
                Object.keys(tags).forEach(tagKey =>
                    this.pushUnique(tagColumns, tagKey),
                );

                series.rows?.forEach(values => {
                    this.totalRows += 1;
                    if (
                        flattenedRows.length >=
                        ChartDataPreviewComponent.MAX_PREVIEW_ROWS
                    ) {
                        return;
                    }

                    const row: PreviewRow = {};
                    queryResult.headers?.forEach((header, index) => {
                        row[header] = values?.[index];
                    });

                    Object.entries(tags).forEach(([key, value]) => {
                        row[key] = value;
                    });

                    if (showSourceColumn) {
                        row['_source'] =
                            queryResult.forId || queryResult.sourceIndex;
                    }

                    flattenedRows.push(row);
                });
            });
        });

        const orderedHeaderColumns = this.orderHeaderColumns(headerColumns);
        this.columns = showSourceColumn
            ? ['_source', ...orderedHeaderColumns, ...tagColumns]
            : [...orderedHeaderColumns, ...tagColumns];
        this.rows = flattenedRows;
        this.allRowsRendered =
            this.totalRows <= ChartDataPreviewComponent.MAX_PREVIEW_ROWS;
    }

    private orderHeaderColumns(columns: string[]): string[] {
        const timeColumns = columns.filter(column => column === 'time');
        const otherColumns = columns.filter(column => column !== 'time');
        return [...timeColumns, ...otherColumns];
    }

    private pushUnique(collection: string[], value: string): void {
        if (value !== undefined && !collection.includes(value)) {
            collection.push(value);
        }
    }

    trackColumn(_index: number, column: string): string {
        return column;
    }

    trackRow(index: number): number {
        return index;
    }

    isTimeColumn(column: string): boolean {
        return column === 'time';
    }

    displayColumnName(column: string): string {
        return column === '_source' ? 'Source' : column;
    }

    isValidDateValue(value: unknown): boolean {
        if (value === null || value === undefined || value === '') {
            return false;
        }

        const date = new Date(value as string | number);
        return !Number.isNaN(date.getTime());
    }

    stringify(value: unknown): string {
        if (value === null || value === undefined) {
            return '-';
        }
        return String(value);
    }

    formatCellValue(column: string, value: unknown): string {
        if (this.isTimeColumn(column) && this.isValidDateValue(value)) {
            return (
                this.datePipe.transform(
                    value as string | number | Date,
                    'yyyy-MM-dd HH:mm:ss.SSS',
                ) ?? this.stringify(value)
            );
        }

        return this.stringify(value);
    }

    toggleExpanded(): void {
        this.expanded = !this.expanded;
    }
}
