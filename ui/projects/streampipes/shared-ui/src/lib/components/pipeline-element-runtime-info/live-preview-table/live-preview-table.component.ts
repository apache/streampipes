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

import { Component, Input, OnInit } from '@angular/core';
import { EventSchema } from '@streampipes/platform-services';
import { RuntimeInfo } from '../pipeline-element-runtime-info.model';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
} from '@angular/material/table';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { PropertyScopeBadgeComponent } from '../../property-scope-badge/property-scope-badge.component';

@Component({
    selector: 'sp-live-preview-table',
    templateUrl: './live-preview-table.component.html',
    styleUrls: ['./live-preview-table.component.scss'],
    imports: [
        MatTable,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        DatePipe,
        TranslatePipe,
        PropertyScopeBadgeComponent,
    ],
})
export class LivePreviewTableComponent implements OnInit {
    @Input()
    eventSchema: EventSchema;

    @Input()
    runtimeInfo: RuntimeInfo[];

    @Input()
    showTitle = true;

    @Input()
    compact = false;

    displayedColumns: string[] = [];

    ngOnInit() {
        this.displayedColumns = this.compact
            ? ['runtimeName', 'value']
            : ['runtimeName', 'label', 'dataType', 'description', 'value'];
    }
}
