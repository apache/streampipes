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

import {
    AfterContentInit,
    AfterViewInit,
    Component,
    ContentChild,
    ContentChildren,
    EventEmitter,
    HostListener,
    inject,
    Input,
    OnChanges,
    OnDestroy,
    Output,
    QueryList,
    Signal,
    SimpleChanges,
    TemplateRef,
    ViewChild,
} from '@angular/core';
import { SelectionModel } from '@angular/cdk/collections';
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
    MatTableDataSource,
} from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { SpTableActionsDirective } from './sp-actions/sp-table-actions.directive';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { SpTableMultiActionsDirective } from './sp-actions/sp-table-multi-actions.directive';
import { LocalStorageService } from '../../services/local-storage-settings.service';
import { FeatureCardService } from '../feature-card-host/feature-card.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { LayoutGapDirective } from '@ngbracket/ngx-layout';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { NgClass, NgTemplateOutlet } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { TranslatePipe } from '@ngx-translate/core';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField } from '@angular/material/form-field';
import { Subscription } from 'rxjs';
import { MatOption, MatSelect } from '@angular/material/select';
import { SpAssetBrowserService } from '../asset-browser/asset-browser.service';
import { SpLabelComponent } from '../sp-label/sp-label.component';
import {
    MatButtonToggle,
    MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import {
    SpTableAssetContextConfig,
    SpTableMultiActionExecuteEvent,
    SpTableMultiActionOption,
    SpTableResolvedAssetContext,
} from './sp-table.model';
import { SpTableAssetContextService } from './sp-asset-context/sp-table-asset-context.service';

type SpTableGroupViewMode = 'list' | 'grouped';
type SpTableGroupingMode = 'label' | 'site' | 'asset';

interface SpTableGroupedSection<T> {
    id: string;
    title: string;
    color?: string;
    count: number;
    rows: T[];
}

interface SpTableGroupHeaderRow {
    __spGroupHeader: true;
    id: string;
    title: string;
    color?: string;
    count: number;
}

type SpTableRenderedRow<T> = T | SpTableGroupHeaderRow;

@Component({
    selector: 'sp-table',
    templateUrl: './sp-table.component.html',
    styleUrls: ['./sp-table.component.scss'],
    imports: [
        LayoutDirective,
        MatTable,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        LayoutAlignDirective,
        MatIconButton,
        MatButton,
        MatTooltip,
        MatIcon,
        MatCheckbox,
        MatFormField,
        MatMenuTrigger,
        MatMenu,
        MatSelect,
        MatOption,
        MatButtonToggleGroup,
        MatButtonToggle,
        NgTemplateOutlet,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        NgClass,
        ClassDirective,
        MatNoDataRow,
        FlexDirective,
        MatPaginator,
        TranslatePipe,
        LayoutGapDirective,
        SpLabelComponent,
    ],
})
export class SpTableComponent<T>
    implements AfterViewInit, AfterContentInit, OnChanges, OnDestroy
{
    readonly selectionColumnId = 'spSelection';
    readonly assetContextColumnId = 'assetContext';
    readonly groupHeaderColumnId = 'spGroupHeader';

    @ContentChildren(MatHeaderRowDef) headerRowDefs: QueryList<MatHeaderRowDef>;
    @ContentChildren(MatRowDef) rowDefs: QueryList<MatRowDef<T>>;
    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ContentChild(MatNoDataRow) noDataRow: MatNoDataRow;

    @ViewChild(MatTable, { static: true }) table: MatTable<
        SpTableRenderedRow<T>
    >;

    @Input() columns: string[];
    @Input() rowsClickable = false;
    @Input() showActionsMenu = false;
    @Input() showSelectionCheckboxes = false;
    @Input() showMultiActionsExecuteButton = false;
    @Input() multiActionsExecuteLabel = 'Execute';
    @Input() multiActionsExecuteDisabled = false;
    @Input() multiActionsSelectLabel = 'Action';
    @Input() multiActionOptions: SpTableMultiActionOption[] = [];
    @Input() featureCardId: string;
    @Input() resourceIdKey = 'elementId';
    @Input() assetContextConfig?: SpTableAssetContextConfig;

    @Input() dataSource: MatTableDataSource<T>;

    @Output() rowClicked = new EventEmitter<T>();
    @Output() selectionChanged = new EventEmitter<T[]>();
    @Output() multiActionsExecute = new EventEmitter<
        SpTableMultiActionExecuteEvent<T>
    >();
    @Output() multiActionSelectionChanged = new EventEmitter<string | null>();

    @ViewChild('paginator') paginator: MatPaginator;
    @ContentChild(SpTableActionsDirective, { read: TemplateRef })
    actionsTemplate?: TemplateRef<any>;
    @ContentChild(SpTableMultiActionsDirective, { read: TemplateRef })
    multiActionsTemplate?: TemplateRef<any>;

    timedOutCloser: any;
    trigger: MatMenuTrigger | undefined = undefined;
    visiblePageRows: T[] = [];
    selectedMultiAction: string | null = null;
    viewMode: SpTableGroupViewMode = 'list';
    groupBy: SpTableGroupingMode = 'asset';
    groupedSections: SpTableGroupedSection<T>[] = [];

    readonly selection = new SelectionModel<T>(true, []);

    private localStorageService = inject(LocalStorageService);
    private featureCardService = inject(FeatureCardService);
    private assetBrowserService = inject(SpAssetBrowserService);
    private assetContextService = inject(SpTableAssetContextService);
    private renderedDataSubscription?: Subscription;
    private assetDataSubscription?: Subscription;
    private viewInitialized = false;
    private assetContextIndex = new Map<
        string,
        Map<string, SpTableResolvedAssetContext>
    >();
    private compactLayout = false;

    readonly pageSize: Signal<number>;

    constructor() {
        this.pageSize = this.localStorageService.signalFor(
            'paginator-page-size',
            10,
        );
        this.assetDataSubscription =
            this.assetBrowserService.assetData$.subscribe(assetData => {
                this.assetContextIndex =
                    this.assetContextService.buildAssetContextIndex(assetData);
                this.applyAssetContextSortingAccessor();
                this.refreshRenderedRows();
            });
        this.updateCompactLayout();
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
        this.bindDataSource();
    }

    ngAfterContentInit() {
        this.columnDefs.forEach(columnDef =>
            this.table.addColumnDef(columnDef),
        );
        this.rowDefs.forEach(rowDef => this.table.addRowDef(rowDef));
        this.headerRowDefs.forEach(headerRowDef =>
            this.table.addHeaderRowDef(headerRowDef),
        );
        if (this.noDataRow) {
            this.table.setNoDataRow(this.noDataRow);
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['dataSource']) {
            this.selection.clear();
            this.emitSelection();
            this.visiblePageRows = [];
            if (this.viewInitialized) {
                this.bindDataSource();
            }
        }

        if (
            changes['showSelectionCheckboxes'] &&
            !this.showSelectionCheckboxes &&
            this.selection.hasValue()
        ) {
            this.selection.clear();
            this.emitSelection();
        }

        if (changes['multiActionOptions']) {
            this.ensureValidSelectedMultiAction();
        }

        if (changes['assetContextConfig']) {
            this.updateCompactLayout();
            this.applyAssetContextSortingAccessor();
            this.refreshRenderedRows();
        }
    }

    ngOnDestroy() {
        this.renderedDataSubscription?.unsubscribe();
        this.assetDataSubscription?.unsubscribe();
    }

    @HostListener('window:resize')
    onResize() {
        this.updateCompactLayout();
    }

    mouseEnter(trigger) {
        if (this.timedOutCloser) {
            clearTimeout(this.timedOutCloser);
        }
        if (this.trigger !== undefined) {
            this.trigger.closeMenu();
        }
        trigger.openMenu();
        this.trigger = trigger;
    }

    mouseLeave(trigger) {
        this.timedOutCloser = setTimeout(() => {
            trigger.closeMenu();
            this.trigger = undefined;
        }, 50);
    }

    onPage(event: PageEvent) {
        this.localStorageService.set('paginator-page-size', event.pageSize);
        if (this.viewMode === 'grouped') {
            this.refreshRenderedRows();
        }
    }

    openFeatureCard(element: T) {
        this.featureCardService.openFeatureCard(
            this.featureCardId,
            element[this.resourceIdKey],
        );
    }

    get renderedColumns(): string[] {
        const baseColumns = (this.columns ?? []).filter(
            column => !this.shouldHideColumn(column),
        );
        if (
            !this.showSelectionCheckboxes ||
            baseColumns.includes(this.selectionColumnId)
        ) {
            return baseColumns;
        }

        return [this.selectionColumnId, ...baseColumns];
    }

    get groupHeaderColumns(): string[] {
        return [this.groupHeaderColumnId];
    }

    get shouldShowGroupingControls(): boolean {
        return !!this.assetContextConfig;
    }

    get renderedDataSource(): MatTableDataSource<T> | SpTableRenderedRow<T>[] {
        return this.viewMode === 'grouped'
            ? this.groupedSections.flatMap(section => [
                  {
                      __spGroupHeader: true as const,
                      id: section.id,
                      title: section.title,
                      color: section.color,
                      count: section.count,
                  },
                  ...section.rows,
              ])
            : this.dataSource;
    }

    get showGroupedLabelsInAssetContext(): boolean {
        return this.viewMode !== 'grouped' || this.groupBy !== 'label';
    }

    get showGroupedSitesInAssetContext(): boolean {
        return this.viewMode !== 'grouped' || this.groupBy !== 'site';
    }

    get showGroupedAssetsInAssetContext(): boolean {
        return this.viewMode !== 'grouped' || this.groupBy !== 'asset';
    }

    getAssetContext(row: T): SpTableResolvedAssetContext | undefined {
        const config = this.assetContextConfig;
        if (!config) {
            return undefined;
        }

        const resourceId = this.getAssetContextResourceId(row, config);
        if (!resourceId) {
            return undefined;
        }

        return this.assetContextIndex
            .get(config.resourceLinkType)
            ?.get(resourceId);
    }

    get selectedRows(): T[] {
        return this.selection.selected;
    }

    get multiActionsContext() {
        return {
            $implicit: this.selectedRows,
            selectedRows: this.selectedRows,
            selectedCount: this.selectedRows.length,
            visiblePageRows: this.visiblePageRows,
            visiblePageRowCount: this.visiblePageRows.length,
        };
    }

    hasBuiltInMultiActionSelect(): boolean {
        return this.multiActionOptions?.length > 0;
    }

    hasMultiActionsToolbarControls(): boolean {
        return (
            this.hasBuiltInMultiActionSelect() ||
            !!this.multiActionsTemplate ||
            this.showMultiActionsExecuteButton
        );
    }

    isRowSelected(row: T): boolean {
        return this.selection.isSelected(row);
    }

    onRowCheckboxClick(event: MouseEvent): void {
        if (event.ctrlKey || event.metaKey) {
            event.preventDefault();
            this.toggleSelectAllVisibleRows(true);
        }
        event.stopPropagation();
    }

    toggleRowSelection(row: T, checked: boolean) {
        if (checked) {
            this.selection.select(row);
        } else {
            this.selection.deselect(row);
        }

        this.emitSelection();
    }

    selectVisiblePageRows() {
        if (!this.visiblePageRows.length) {
            return;
        }

        this.selection.select(...this.visiblePageRows);
        this.emitSelection();
    }

    clearSelection() {
        if (!this.selection.hasValue()) {
            return;
        }

        this.selection.clear();
        this.emitSelection();
    }

    toggleSelectAllVisibleRows(checked: boolean) {
        if (checked) {
            this.selectVisiblePageRows();
            return;
        }

        if (!this.visiblePageRows.length) {
            return;
        }

        this.selection.deselect(...this.visiblePageRows);
        this.emitSelection();
    }

    areAllVisibleRowsSelected(): boolean {
        return (
            this.visiblePageRows.length > 0 &&
            this.visiblePageRows.every(row => this.selection.isSelected(row))
        );
    }

    areSomeVisibleRowsSelected(): boolean {
        return (
            this.visiblePageRows.some(row => this.selection.isSelected(row)) &&
            !this.areAllVisibleRowsSelected()
        );
    }

    setViewMode(mode: SpTableGroupViewMode) {
        if (mode === 'grouped') {
            this.groupBy = 'asset';
        }

        if (this.viewMode === mode) {
            this.refreshRenderedRows();
            return;
        }

        this.viewMode = mode;
        this.bindDataSource();
        this.refreshRenderedRows();
    }

    setGrouping(mode: SpTableGroupingMode) {
        if (this.groupBy === mode) {
            return;
        }

        this.groupBy = mode;
        this.refreshRenderedRows();
    }

    isGroupHeaderRow = (_: number, row: SpTableRenderedRow<T>) =>
        this.hasGroupHeaderMarker(row);

    isDataRow = (_: number, row: SpTableRenderedRow<T>) =>
        !this.hasGroupHeaderMarker(row);

    private bindDataSource() {
        if (!this.dataSource) {
            return;
        }

        this.dataSource.paginator = this.paginator;

        this.renderedDataSubscription?.unsubscribe();
        this.renderedDataSubscription = this.dataSource.connect().subscribe({
            next: rows => this.updateRenderedState(rows ?? []),
        });
    }

    private refreshRenderedRows() {
        this.updateRenderedState(this.getCurrentPageRows(), false);
    }

    private getCurrentPageRows(): T[] {
        const rows =
            this.dataSource?.filteredData ?? this.dataSource?.data ?? [];
        if (!this.paginator) {
            return rows;
        }

        const pageSize = this.paginator.pageSize || this.pageSize();
        const startIndex = this.paginator.pageIndex * pageSize;
        return rows.slice(startIndex, startIndex + pageSize);
    }

    private updateRenderedState(rows: T[], pruneSelection = true) {
        this.visiblePageRows = rows;
        this.rebuildGroupedSections(rows);

        if (pruneSelection) {
            this.pruneSelection();
        }

        if (this.viewInitialized) {
            this.table.renderRows();
        }
    }

    private pruneSelection() {
        if (!this.selection.hasValue() || !this.dataSource) {
            return;
        }

        const availableRows = new Set(this.dataSource.filteredData ?? []);
        const rowsToRemove = this.selection.selected.filter(
            row => !availableRows.has(row),
        );

        if (!rowsToRemove.length) {
            return;
        }

        this.selection.deselect(...rowsToRemove);
        this.emitSelection();
    }

    private emitSelection() {
        this.selectionChanged.emit(this.selection.selected);
    }

    emitMultiActionsExecute() {
        this.multiActionsExecute.emit({
            selectedRows: this.selection.selected,
            action: this.selectedMultiAction,
        });
    }

    onSelectedMultiActionChange(action: string | null) {
        this.selectedMultiAction = action;
        this.multiActionSelectionChanged.emit(action);
    }

    isMultiActionsExecuteButtonDisabled(): boolean {
        if (
            !this.selection.selected.length ||
            this.multiActionsExecuteDisabled
        ) {
            return true;
        }

        if (this.hasBuiltInMultiActionSelect() && !this.selectedMultiAction) {
            return true;
        }

        const selectedOption = this.multiActionOptions?.find(
            option => option.value === this.selectedMultiAction,
        );

        return !!selectedOption?.disabled;
    }

    private ensureValidSelectedMultiAction() {
        if (!this.selectedMultiAction) {
            return;
        }

        const actionStillExists = (this.multiActionOptions ?? []).some(
            option => option.value === this.selectedMultiAction,
        );

        if (actionStillExists) {
            return;
        }

        this.selectedMultiAction = null;
        this.multiActionSelectionChanged.emit(null);
    }

    private shouldHideColumn(column: string): boolean {
        return (
            !!this.assetContextConfig &&
            column === this.assetContextColumnId &&
            this.compactLayout
        );
    }

    private updateCompactLayout(): void {
        const hideBelowWidth = this.assetContextConfig?.hideBelowWidth ?? 1200;
        this.compactLayout = window.innerWidth < hideBelowWidth;
    }

    private applyAssetContextSortingAccessor(): void {
        if (!this.dataSource) {
            return;
        }

        const currentAccessor =
            this.dataSource.sortingDataAccessor?.bind(this.dataSource) ??
            ((data: T, sortHeaderId: string) =>
                (data as Record<string, unknown>)?.[sortHeaderId] as
                    | string
                    | number);

        this.dataSource.sortingDataAccessor = (data, sortHeaderId) => {
            if (
                this.assetContextConfig &&
                sortHeaderId === this.assetContextColumnId
            ) {
                return this.getAssetContext(data)?.sortValue ?? '';
            }

            return currentAccessor(data, sortHeaderId);
        };
    }

    private rebuildGroupedSections(rows: T[]) {
        if (!this.assetContextConfig || this.viewMode !== 'grouped') {
            this.groupedSections = [];
            return;
        }

        const grouped = new Map<string, SpTableGroupedSection<T>>();

        rows.forEach(row => {
            this.resolveGroups(row).forEach(group => {
                const current = grouped.get(group.id) ?? {
                    id: group.id,
                    title: group.title,
                    color: group.color,
                    count: 0,
                    rows: [],
                };
                current.rows.push(row);
                current.count += 1;
                grouped.set(group.id, current);
            });
        });

        this.groupedSections = Array.from(grouped.values())
            .sort((left, right) => left.title.localeCompare(right.title))
            .map(group => ({
                ...group,
                rows: [...group.rows],
            }));
    }

    private resolveGroups(
        row: T,
    ): { id: string; title: string; color?: string }[] {
        const assetContext = this.getAssetContext(row);

        if (this.groupBy === 'label') {
            return this.resolveLabelGroups(assetContext);
        }

        if (this.groupBy === 'site') {
            return this.resolveSiteGroups(assetContext);
        }

        return this.resolveAssetGroups(assetContext);
    }

    private resolveLabelGroups(
        assetContext?: SpTableResolvedAssetContext,
    ): { id: string; title: string; color?: string }[] {
        const labels = assetContext?.labels ?? [];
        return labels.length
            ? labels.map(label => ({
                  id: `label:${label._id ?? label.label}`,
                  title: label.label,
                  color: label.color,
              }))
            : [this.createUnassignedGroup()];
    }

    private resolveSiteGroups(
        assetContext?: SpTableResolvedAssetContext,
    ): { id: string; title: string; color?: string }[] {
        const sites = assetContext?.sites ?? [];
        return sites.length
            ? sites.map(site => ({
                  id: `site:${site.id}`,
                  title: site.label,
              }))
            : [this.createUnassignedGroup()];
    }

    private resolveAssetGroups(
        assetContext?: SpTableResolvedAssetContext,
    ): { id: string; title: string; color?: string }[] {
        const assets = assetContext?.assets ?? [];
        return assets.length
            ? assets.map(asset => ({
                  id: `asset:${asset.id}`,
                  title: asset.label,
              }))
            : [this.createUnassignedGroup()];
    }

    private createUnassignedGroup(): { id: string; title: string } {
        return { id: 'unassigned', title: 'Unassigned' };
    }

    private getAssetContextResourceId(
        row: T,
        config: SpTableAssetContextConfig,
    ): string | undefined {
        const key = config.resourceIdKey ?? this.resourceIdKey;
        return (row as Record<string, string | undefined>)?.[key];
    }

    private hasGroupHeaderMarker(
        row: SpTableRenderedRow<T>,
    ): row is SpTableGroupHeaderRow {
        return !!(row as SpTableGroupHeaderRow).__spGroupHeader;
    }
}
