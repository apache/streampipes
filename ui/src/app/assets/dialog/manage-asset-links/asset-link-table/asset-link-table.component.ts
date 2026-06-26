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
import {
    AssetLinkResourceRow,
    AssetLinkSelectionChange,
} from './asset-link-table.model';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatNoDataRow,
    MatRow,
    MatRowDef,
    MatTable,
} from '@angular/material/table';
import { MatCheckbox } from '@angular/material/checkbox';
import { TranslatePipe } from '@ngx-translate/core';
import { MatSort, MatSortHeader, Sort } from '@angular/material/sort';
import {
    MatFormField,
    MatPrefix,
    MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import {
    MatButtonToggle,
    MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { LayoutGapDirective } from '@ngbracket/ngx-layout';

interface AssetLinkGroupHeaderRow {
    groupHeader: true;
    id: string;
    title: string;
    count: number;
}

type AssetLinkTableRow = AssetLinkResourceRow | AssetLinkGroupHeaderRow;
type AssetLinkViewMode = 'grouped' | 'list';

@Component({
    selector: 'sp-asset-link-table',
    templateUrl: './asset-link-table.component.html',
    styleUrls: ['./asset-link-table.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatButtonToggle,
        MatButtonToggleGroup,
        MatCell,
        MatCellDef,
        MatCheckbox,
        MatColumnDef,
        MatFormField,
        MatHeaderCell,
        MatHeaderCellDef,
        MatHeaderRow,
        MatHeaderRowDef,
        MatIcon,
        MatIconButton,
        MatInput,
        MatNoDataRow,
        MatPrefix,
        MatRow,
        MatRowDef,
        MatSort,
        MatSortHeader,
        MatSuffix,
        MatTable,
        TranslatePipe,
    ],
})
export class AssetLinkTableComponent {
    @Input()
    resources: AssetLinkResourceRow[] = [];

    @Input()
    selectedResourceIds: string[] = [];

    @Output()
    selectionChange = new EventEmitter<AssetLinkSelectionChange>();

    displayedColumns = ['selected', 'resourceName', 'resourceType'];
    groupHeaderColumns = ['groupHeader'];
    searchTerm = '';
    viewMode: AssetLinkViewMode = 'grouped';
    sort: Sort = { active: 'resourceName', direction: 'asc' };

    get renderedRows(): AssetLinkTableRow[] {
        const rows = this.filteredAndSortedResources;

        if (this.viewMode === 'list') {
            return rows;
        }

        const groupedRows = new Map<string, AssetLinkResourceRow[]>();
        rows.forEach(row => {
            const groupRows = groupedRows.get(row.assetLinkType) ?? [];
            groupRows.push(row);
            groupedRows.set(row.assetLinkType, groupRows);
        });

        return Array.from(groupedRows.entries())
            .sort((left, right) =>
                left[1][0].resourceType.localeCompare(right[1][0].resourceType),
            )
            .flatMap(([id, groupRows]) => [
                {
                    groupHeader: true as const,
                    id,
                    title: groupRows[0].resourceType,
                    count: groupRows.length,
                },
                ...groupRows,
            ]);
    }

    isResourceSelected(resourceId: string): boolean {
        return this.selectedResourceIds.includes(resourceId);
    }

    updateSelection(checked: boolean, resource: AssetLinkResourceRow): void {
        this.selectionChange.emit({ checked, resource });
    }

    updateSearch(searchTerm: string): void {
        this.searchTerm = searchTerm;
    }

    clearSearch(): void {
        this.searchTerm = '';
    }

    updateViewMode(viewMode: AssetLinkViewMode): void {
        this.viewMode = viewMode;
    }

    updateSort(sort: Sort): void {
        this.sort = sort;
    }

    isGroupHeaderRow = (_: number, row: AssetLinkTableRow) =>
        this.hasGroupHeaderMarker(row);

    isDataRow = (_: number, row: AssetLinkTableRow) =>
        !this.hasGroupHeaderMarker(row);

    private get filteredAndSortedResources(): AssetLinkResourceRow[] {
        const normalizedSearchTerm = this.searchTerm.trim().toLocaleLowerCase();
        const rows = normalizedSearchTerm
            ? this.resources.filter(resource =>
                  [resource.resourceName, resource.resourceType].some(value =>
                      value.toLocaleLowerCase().includes(normalizedSearchTerm),
                  ),
              )
            : [...this.resources];

        return rows.sort((left, right) => this.compareRows(left, right));
    }

    private compareRows(
        left: AssetLinkResourceRow,
        right: AssetLinkResourceRow,
    ): number {
        const direction = this.sort.direction === 'desc' ? -1 : 1;

        if (!this.sort.active || !this.sort.direction) {
            return left.resourceName.localeCompare(right.resourceName);
        }

        const leftValue = left[this.sort.active as keyof AssetLinkResourceRow];
        const rightValue =
            right[this.sort.active as keyof AssetLinkResourceRow];

        return (
            String(leftValue ?? '').localeCompare(String(rightValue ?? '')) *
            direction
        );
    }

    private hasGroupHeaderMarker(
        row: AssetLinkTableRow,
    ): row is AssetLinkGroupHeaderRow {
        return !!(row as AssetLinkGroupHeaderRow).groupHeader;
    }
}
