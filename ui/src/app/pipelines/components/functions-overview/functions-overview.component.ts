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

import { Component, Input, OnInit, inject } from '@angular/core';
import { FunctionId } from '@streampipes/platform-services';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { Router } from '@angular/router';
import { SpTableComponent } from '@streampipes/shared-ui';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
    selector: 'sp-functions-overview',
    templateUrl: './functions-overview.component.html',
    styleUrls: ['./functions-overview.component.scss'],
    imports: [
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        LayoutAlignDirective,
        LayoutDirective,
        MatIconButton,
        MatTooltip,
    ],
})
export class FunctionsOverviewComponent implements OnInit {
    private router = inject(Router);

    @Input()
    functions: FunctionId[] = [];

    dataSource: MatTableDataSource<FunctionId>;

    displayedColumns: string[] = ['name', 'action'];

    ngOnInit(): void {
        this.dataSource = new MatTableDataSource<FunctionId>();
        this.dataSource.data = this.functions;
    }

    showFunctionDetails(functionId: string): void {
        this.router.navigate(['pipelines', 'functions', functionId, 'metrics']);
    }
}
