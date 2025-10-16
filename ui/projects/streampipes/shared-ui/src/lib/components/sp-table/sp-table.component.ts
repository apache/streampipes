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
    Input,
    Output,
    QueryList,
    TemplateRef,
    ViewChild,
} from '@angular/core';
import {
    MatColumnDef,
    MatHeaderRowDef,
    MatNoDataRow,
    MatRowDef,
    MatTable,
    MatTableDataSource,
} from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { SpTableActionsDirective } from './sp-table-actions.directive';
import { MatMenuTrigger } from '@angular/material/menu';

@Component({
    selector: 'sp-table',
    templateUrl: './sp-table.component.html',
    styleUrls: ['./sp-table.component.scss'],
    standalone: false,
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

    @Input() dataSource: MatTableDataSource<T>;

    @Output() rowClicked = new EventEmitter<T>();

    @ViewChild('paginator') paginator: MatPaginator;
    @ContentChild(SpTableActionsDirective, { read: TemplateRef })
    actionsTemplate?: TemplateRef<any>;

    pageSize = 1;
    timedOutCloser: any;
    trigger: MatMenuTrigger | undefined = undefined;

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
}
