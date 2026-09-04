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

import { Component, inject, Input } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
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
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'sp-data-retention-log-dialog',
    templateUrl: './data-retention-log-dialog.component.html',
    imports: [
        MatTable,
        FlexDirective,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        MatDivider,
        MatButton,
        DatePipe,
        TranslatePipe,
    ],
})
export class DataRetentionLogDialogComponent {
    @Input()
    retentionLog: string;

    displayedColumns: string[] = ['date', 'path', 'state', 'error'];

    private dialogRef = inject(DialogRef<DataRetentionLogDialogComponent>);
    private translateService = inject(TranslateService);

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex);
    }
}
