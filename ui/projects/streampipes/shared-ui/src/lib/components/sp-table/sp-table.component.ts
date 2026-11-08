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
    ChangeDetectorRef,
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
    ChangeDetectionStrategy,
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
import { SpTableCardDirective } from './sp-actions/sp-table-card.directive';
import { SpOverviewCardGridComponent } from '../overview-card/overview-card-grid.component';
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
import {
    MatFormField,
    MatPrefix,
    MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
    BehaviorSubject,
    debounceTime,
    distinctUntilChanged,
    Subject,
    Subscription,
} from 'rxjs';
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
    SpTableNameSearchConfig,
    SpTableResolvedAssetContext,
} from './sp-table.model';
import { SpTableAssetContextService } from './sp-asset-context/sp-table-asset-context.service';

type SpTableViewMode = 'list' | 'grouped' | 'cards';
type SpTableGroupingMode = 'label' | 'site' | 'asset';

const VIEW_MODES: SpTableViewMode[] = ['list', 'grouped', 'cards'];
const VIEW_MODE_STORAGE_KEY = 'table-view-mode';
const GROUPING_MODES: SpTableGroupingMode[] = ['label', 'site', 'asset'];

interface SpTableStoredViewMode {
    viewMode: SpTableViewMode;
    groupBy: SpTableGroupingMode;
}

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

interface SpTableCardAssetContextItem {
    id: string;
    label: string;
    tone?: 'info' | 'neutral';
    color?: string;
    tooltip?: string;
}

const CARD_ASSET_CONTEXT_LIMIT = 2;

@Component({
    selector: 'sp-table',
    templateUrl: './sp-table.component.html',
    styleUrls: ['./sp-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
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
        MatPrefix,
        MatSuffix,
        MatInput,
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
        SpOverviewCardGridComponent,
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
    @Input() noBorder = false;
    @Input() noToolbarBorderTop = false;
    @Input() rowsClickable = false;
    @Input() showPaginator = true;
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
    @Input() nameSearchConfig?: SpTableNameSearchConfig<T>;
    /**
     * Remembers the view mode and grouping in local storage. All tables that
     * remember it share one setting, so a mode chosen on one overview page
     * also applies to the others and survives a reload.
     */
    @Input() rememberViewMode = false;

    @Input() dataSource: MatTableDataSource<T>;

    @Output() rowClicked = new EventEmitter<T>();
    @Output() selectionChanged = new EventEmitter<T[]>();
    @Output() multiActionsExecute = new EventEmitter<
        SpTableMultiActionExecuteEvent<T>
    >();
    @Output() multiActionSelectionChanged = new EventEmitter<string | null>();

    paginator: MatPaginator;

    @ViewChild('paginator')
    set paginatorControl(paginator: MatPaginator) {
        // Angular re-runs this setter whenever the view query is refreshed,
        // which happens on every table render; only rebind on a real change.
        if (paginator === this.paginator) {
            return;
        }

        this.paginator = paginator;
        if (this.viewInitialized) {
            this.bindDataSource();
        }
    }
    @ContentChild(SpTableActionsDirective, { read: TemplateRef })
    actionsTemplate?: TemplateRef<any>;
    @ContentChild(SpTableMultiActionsDirective, { read: TemplateRef })
    multiActionsTemplate?: TemplateRef<any>;
    @ContentChild(SpTableCardDirective, { read: TemplateRef })
    cardTemplate?: TemplateRef<any>;

    visiblePageRows: T[] = [];
    selectedMultiAction: string | null = null;
    nameSearchTerm = '';
    viewMode: SpTableViewMode = 'list';
    groupBy: SpTableGroupingMode = 'asset';
    groupedSections: SpTableGroupedSection<T>[] = [];
    renderedGroupedRows: SpTableRenderedRow<T>[] = [];

    /**
     * The only data source the mat-table is bound to. It emits the current
     * page rows in list mode and the group headers plus their rows in grouped
     * mode, so switching the view mode never swaps the table's data source. It
     * stays empty in card mode, where the hidden table has nothing to render.
     */
    readonly renderedRows$ = new BehaviorSubject<SpTableRenderedRow<T>[]>([]);

    readonly selection = new SelectionModel<T>(true, []);

    private changeDetectorRef = inject(ChangeDetectorRef);
    private localStorageService = inject(LocalStorageService);
    private featureCardService = inject(FeatureCardService);
    private assetBrowserService = inject(SpAssetBrowserService);
    private assetContextService = inject(SpTableAssetContextService);
    private renderedDataSubscription?: Subscription;
    private connectedDataSource?: MatTableDataSource<T>;
    private assetDataSubscription?: Subscription;
    private nameSearchSubscription?: Subscription;
    private columnDefsSubscription?: Subscription;
    private registeredColumnDefs = new Set<MatColumnDef>();
    private nameSearchInput$ = new Subject<string>();
    private viewInitialized = false;
    private defaultFilterPredicates = new WeakMap<
        MatTableDataSource<T>,
        (data: T, filter: string) => boolean
    >();
    private assetContextIndex = new Map<
        string,
        Map<string, SpTableResolvedAssetContext>
    >();
    private groupHeaderRows = new Map<string, SpTableGroupHeaderRow>();
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
        this.nameSearchSubscription = this.nameSearchInput$
            .pipe(debounceTime(150), distinctUntilChanged())
            .subscribe(value => this.applyNameSearchFilter(value));
        this.updateCompactLayout();
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
        this.bindDataSource();
    }

    ngAfterContentInit() {
        this.restoreViewMode();
        this.syncColumnDefs();
        this.columnDefsSubscription = this.columnDefs.changes.subscribe(() => {
            this.syncColumnDefs();
            this.table.renderRows();
        });
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
            this.visiblePageRows = [];
            this.configureNameSearch();
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

        if (changes['nameSearchConfig'] && !changes['dataSource']) {
            this.configureNameSearch();
        }
    }

    ngOnDestroy() {
        this.renderedDataSubscription?.unsubscribe();
        this.connectedDataSource?.disconnect();
        this.assetDataSubscription?.unsubscribe();
        this.nameSearchSubscription?.unsubscribe();
        this.columnDefsSubscription?.unsubscribe();
    }

    @HostListener('window:resize')
    onResize() {
        this.updateCompactLayout();
    }

    onPage(event: PageEvent) {
        this.localStorageService.set('paginator-page-size', event.pageSize);
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

    get showCardView(): boolean {
        return this.viewMode === 'cards' && !!this.cardTemplate;
    }

    get showSelectionToolbar(): boolean {
        return this.showSelectionCheckboxes;
    }

    get shouldShowNameSearch(): boolean {
        return !!this.nameSearchConfig?.enabled;
    }

    get nameSearchKeys(): Array<keyof T | 'name'> {
        if (!this.nameSearchConfig?.searchKey?.length) {
            return ['name'];
        }

        return this.nameSearchConfig.searchKey;
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

    getCardAssetContext(
        row: T,
    ):
        | { visible: SpTableCardAssetContextItem[]; hiddenLabels: string[] }
        | undefined {
        const assetContext = this.getAssetContext(row);
        if (!assetContext) {
            return undefined;
        }

        const items: SpTableCardAssetContextItem[] = [
            ...assetContext.sites.map(site => ({
                id: `site:${site.id}`,
                label: site.label,
                tone: 'info' as const,
            })),
            ...assetContext.assets.map(asset => ({
                id: `asset:${asset.id}`,
                label: asset.label,
                tone: 'neutral' as const,
                tooltip: asset.tooltip,
            })),
            ...assetContext.labels.map(label => ({
                id: `label:${label._id ?? label.label}`,
                label: label.label,
                color: label.color,
            })),
        ];

        return {
            visible: items.slice(0, CARD_ASSET_CONTEXT_LIMIT),
            hiddenLabels: items
                .slice(CARD_ASSET_CONTEXT_LIMIT)
                .map(item => item.label),
        };
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

    /** Name of the row for accessible labels, read from the name search keys. */
    getRowName(row: T): string {
        const value = this.nameSearchKeys
            .map(key => row?.[key as keyof T])
            .find(name => name !== undefined && name !== null && name !== '');
        return value === undefined ? '' : String(value);
    }

    isRowSelected(row: T): boolean {
        return this.selection.isSelected(row);
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

    setViewMode(mode: SpTableViewMode) {
        if (mode === 'grouped') {
            this.groupBy = 'asset';
        }

        if (this.viewMode === mode) {
            this.storeViewMode();
            this.refreshRenderedRows();
            return;
        }

        this.viewMode = mode;
        this.storeViewMode();
        this.refreshRenderedRows();
    }

    setGrouping(mode: SpTableGroupingMode) {
        if (this.groupBy === mode) {
            return;
        }

        this.groupBy = mode;
        this.storeViewMode();
        this.refreshRenderedRows();
    }

    /**
     * Restores the stored view mode once the card template is known. A mode
     * that this table does not offer, such as cards without a card template,
     * falls back to the list without changing the stored setting.
     */
    private restoreViewMode(): void {
        if (!this.rememberViewMode) {
            return;
        }

        const stored = this.localStorageService.get<
            Partial<SpTableStoredViewMode> | undefined
        >(VIEW_MODE_STORAGE_KEY, undefined);
        if (GROUPING_MODES.includes(stored?.groupBy)) {
            this.groupBy = stored.groupBy;
        }
        if (this.isViewModeAvailable(stored?.viewMode)) {
            this.viewMode = stored.viewMode;
        }
    }

    private storeViewMode(): void {
        if (this.rememberViewMode) {
            this.localStorageService.set<SpTableStoredViewMode>(
                VIEW_MODE_STORAGE_KEY,
                {
                    viewMode: this.viewMode,
                    groupBy: this.groupBy,
                },
            );
        }
    }

    private isViewModeAvailable(mode?: SpTableViewMode): boolean {
        if (!VIEW_MODES.includes(mode)) {
            return false;
        }
        if (mode === 'cards') {
            return !!this.cardTemplate;
        }
        if (mode === 'grouped') {
            return this.shouldShowGroupingControls;
        }
        return true;
    }

    onNameSearchInput(value: string) {
        this.nameSearchTerm = value;
        this.nameSearchInput$.next(value);
    }

    private applyNameSearchFilter(value: string) {
        if (!this.dataSource) {
            return;
        }

        if (!this.shouldShowNameSearch) {
            this.dataSource.filter = '';
            return;
        }

        const normalizedFilter = value.trim().toLocaleLowerCase();
        if (this.dataSource.filter === normalizedFilter) {
            return;
        }

        if (this.paginator && this.paginator.pageIndex !== 0) {
            this.paginator.pageIndex = 0;
        }

        this.dataSource.filter = normalizedFilter;
    }

    clearNameSearch() {
        this.onNameSearchInput('');
    }

    activateRow(event: Event, row: T): void {
        if (this.rowsClickable && event.target === event.currentTarget) {
            event.preventDefault();
            this.rowClicked.emit(row);
        }
    }

    isGroupHeaderRow = (_: number, row: SpTableRenderedRow<T>) =>
        this.hasGroupHeaderMarker(row);

    isDataRow = (_: number, row: SpTableRenderedRow<T>) =>
        !this.hasGroupHeaderMarker(row);

    private bindDataSource() {
        this.renderedDataSubscription?.unsubscribe();
        this.renderedDataSubscription = undefined;
        if (this.connectedDataSource !== this.dataSource) {
            this.connectedDataSource?.disconnect();
            this.connectedDataSource = undefined;
        }

        if (!this.dataSource) {
            this.updateRenderedState([]);
            return;
        }

        this.configureNameSearch();
        this.dataSource.paginator = this.showPaginator ? this.paginator : null;

        // The table itself is bound to renderedRows$, so this component owns
        // the connection to the MatTableDataSource (paging, sorting, filter).
        this.connectedDataSource = this.dataSource;
        this.renderedDataSubscription = this.dataSource.connect().subscribe({
            next: rows => this.updateRenderedState(rows ?? []),
        });
    }

    private syncColumnDefs() {
        const currentColumnDefs = new Set(this.columnDefs.toArray());

        this.registeredColumnDefs.forEach(columnDef => {
            if (!currentColumnDefs.has(columnDef)) {
                this.table.removeColumnDef(columnDef);
                this.registeredColumnDefs.delete(columnDef);
            }
        });

        currentColumnDefs.forEach(columnDef => {
            if (!this.registeredColumnDefs.has(columnDef)) {
                this.table.addColumnDef(columnDef);
                this.registeredColumnDefs.add(columnDef);
            }
        });
    }

    private refreshRenderedRows() {
        this.updateRenderedState(this.visiblePageRows, false);
    }

    private configureNameSearch() {
        if (!this.dataSource) {
            return;
        }

        if (!this.defaultFilterPredicates.has(this.dataSource)) {
            this.defaultFilterPredicates.set(
                this.dataSource,
                this.dataSource.filterPredicate,
            );
        }

        const defaultFilterPredicate =
            this.defaultFilterPredicates.get(this.dataSource) ??
            this.dataSource.filterPredicate;

        if (!this.shouldShowNameSearch) {
            this.nameSearchTerm = '';
            this.dataSource.filterPredicate = defaultFilterPredicate;
            this.dataSource.filter = '';
            return;
        }

        this.dataSource.filterPredicate = (row, filter) => {
            const normalizedFilter = filter.trim().toLocaleLowerCase();
            if (!normalizedFilter) {
                return true;
            }

            return this.nameSearchKeys.some(searchKey => {
                const searchValue = (row as Record<string, unknown>)[
                    searchKey as string
                ];
                return String(searchValue ?? '')
                    .toLocaleLowerCase()
                    .includes(normalizedFilter);
            });
        };
        this.dataSource.filter = this.nameSearchTerm.trim().toLocaleLowerCase();
    }

    private updateRenderedState(rows: T[], reconcileSelection = true) {
        this.visiblePageRows = rows;
        this.rebuildGroupedSections(rows);

        if (reconcileSelection) {
            this.reconcileSelection();
        }

        this.renderedRows$.next(this.getTableRows(rows));
        // Rendered state usually changes from a subscription (data source,
        // asset data), outside of any template event, so make sure the
        // bindings that depend on it (group headers, asset context cells,
        // selection toolbar) are re-checked under OnPush parents.
        this.changeDetectorRef.markForCheck();
    }

    private getTableRows(rows: T[]): SpTableRenderedRow<T>[] {
        if (this.showCardView) {
            return [];
        }

        return this.viewMode === 'grouped' ? this.renderedGroupedRows : rows;
    }

    private reconcileSelection() {
        if (!this.selection.hasValue() || !this.dataSource) {
            return;
        }

        const availableRowsByResourceId = new Map<unknown, T>(
            (this.dataSource.data ?? []).map(row => [
                this.getSelectionKey(row),
                row,
            ]),
        );
        const reconciledSelection = this.selection.selected
            .map(row =>
                availableRowsByResourceId.get(this.getSelectionKey(row)),
            )
            .filter((row): row is T => row !== undefined);
        const selectionChanged =
            reconciledSelection.length !== this.selection.selected.length ||
            reconciledSelection.some(
                (row, index) => row !== this.selection.selected[index],
            );

        if (!selectionChanged) {
            return;
        }

        this.selection.clear();
        this.selection.select(...reconciledSelection);
        this.emitSelection();
    }

    getSelectionKey(row: T): unknown {
        const resourceId = (row as Record<string, unknown>)[this.resourceIdKey];

        return resourceId ?? row;
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
                    string | number);

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
            this.renderedGroupedRows = [];
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
        this.renderedGroupedRows = this.groupedSections.flatMap(section => [
            this.getGroupHeaderRow(section),
            ...section.rows,
        ]);
    }

    /**
     * Reuses the header row object of a group across rebuilds so the table
     * can diff by identity and keep the rendered header row instead of
     * recreating it on every refresh.
     */
    private getGroupHeaderRow(
        section: SpTableGroupedSection<T>,
    ): SpTableGroupHeaderRow {
        const existing = this.groupHeaderRows.get(section.id);
        if (existing) {
            existing.title = section.title;
            existing.color = section.color;
            existing.count = section.count;
            return existing;
        }

        const headerRow: SpTableGroupHeaderRow = {
            __spGroupHeader: true,
            id: section.id,
            title: section.title,
            color: section.color,
            count: section.count,
        };
        this.groupHeaderRows.set(section.id, headerRow);
        return headerRow;
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
