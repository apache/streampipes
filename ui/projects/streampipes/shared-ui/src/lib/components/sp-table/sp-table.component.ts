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
import { SpTableActionsDirective } from './sp-table-actions.directive';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { SpTableMultiActionsDirective } from './sp-table-multi-actions.directive';
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
import { FormFieldComponent } from '../form-field/form-field.component';

export interface SpTableMultiActionOption {
    value: string;
    label: string;
    icon?: string;
    disabled?: boolean;
}

export interface SpTableMultiActionExecuteEvent<T> {
    selectedRows: T[];
    action: string | null;
}

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
        FormFieldComponent,
    ],
})
export class SpTableComponent<T>
    implements AfterViewInit, AfterContentInit, OnChanges, OnDestroy
{
    readonly selectionColumnId = 'spSelection';

    @ContentChildren(MatHeaderRowDef) headerRowDefs: QueryList<MatHeaderRowDef>;
    @ContentChildren(MatRowDef) rowDefs: QueryList<MatRowDef<T>>;
    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ContentChild(MatNoDataRow) noDataRow: MatNoDataRow;

    @ViewChild(MatTable, { static: true }) table: MatTable<T>;

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

    readonly selection = new SelectionModel<T>(true, []);

    private localStorageService = inject(LocalStorageService);
    private featureCardService = inject(FeatureCardService);
    private renderedDataSubscription?: Subscription;
    private viewInitialized = false;

    readonly pageSize: Signal<number>;

    constructor() {
        this.pageSize = this.localStorageService.signalFor(
            'paginator-page-size',
            10,
        );
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
        this.table.setNoDataRow(this.noDataRow);
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
    }

    ngOnDestroy() {
        this.renderedDataSubscription?.unsubscribe();
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
    }

    openFeatureCard(element: T) {
        this.featureCardService.openFeatureCard(
            this.featureCardId,
            element[this.resourceIdKey],
        );
    }

    get renderedColumns(): string[] {
        const baseColumns = this.columns ?? [];
        if (
            !this.showSelectionCheckboxes ||
            baseColumns.includes(this.selectionColumnId)
        ) {
            return baseColumns;
        }

        return [this.selectionColumnId, ...baseColumns];
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

    private bindDataSource() {
        if (!this.dataSource || !this.paginator) {
            return;
        }

        this.dataSource.paginator = this.paginator;

        this.renderedDataSubscription?.unsubscribe();
        this.renderedDataSubscription = this.dataSource.connect().subscribe({
            next: rows => {
                this.visiblePageRows = rows ?? [];
                this.pruneSelection();
            },
        });
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
}
