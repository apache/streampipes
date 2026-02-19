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
    Output,
    QueryList,
    Signal,
    TemplateRef,
    ViewChild,
} from '@angular/core';
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
import { LocalStorageService } from '../../services/local-storage-settings.service';
import { FeatureCardService } from '../feature-card-host/feature-card.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { NgClass, NgTemplateOutlet } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { TranslatePipe } from '@ngx-translate/core';

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
        MatTooltip,
        MatIcon,
        MatMenuTrigger,
        MatMenu,
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
    ],
})
export class SpTableComponent<T> implements AfterViewInit, AfterContentInit {
    @ContentChildren(MatHeaderRowDef) headerRowDefs: QueryList<MatHeaderRowDef>;
    @ContentChildren(MatRowDef) rowDefs: QueryList<MatRowDef<T>>;
    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ContentChild(MatNoDataRow) noDataRow: MatNoDataRow;

    @ViewChild(MatTable, { static: true }) table: MatTable<T>;

    @Input() columns: string[];
    @Input() rowsClickable = false;
    @Input() showActionsMenu = false;
    @Input() featureCardId: string;
    @Input() resourceIdKey = 'elementId';

    @Input() dataSource: MatTableDataSource<T>;

    @Output() rowClicked = new EventEmitter<T>();

    @ViewChild('paginator') paginator: MatPaginator;
    @ContentChild(SpTableActionsDirective, { read: TemplateRef })
    actionsTemplate?: TemplateRef<any>;

    timedOutCloser: any;
    trigger: MatMenuTrigger | undefined = undefined;

    private localStorageService = inject(LocalStorageService);
    private featureCardService = inject(FeatureCardService);

    readonly pageSize: Signal<number>;

    constructor() {
        this.pageSize = this.localStorageService.signalFor(
            'paginator-page-size',
            10,
        );
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
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
}
