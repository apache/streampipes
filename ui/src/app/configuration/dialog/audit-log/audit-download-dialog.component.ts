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
    ChangeDetectionStrategy,
    Component,
    Input,
    OnInit,
    inject,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatStep, MatStepLabel, MatStepper } from '@angular/material/stepper';
import {
    DialogRef,
    FormatExportConfig,
    SelectFormatComponent,
} from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';
import { AuditDownloadComponent } from './components/download/download.component';
import { AuditSelectDataComponent } from './components/select-data/select-data.component';
import {
    AuditActorOption,
    AuditDownloadDialogModel,
    AuditExportColumn,
} from './model/audit-download-dialog.model';
import { AuditExportConfig } from './model/audit-export-config.model';
import { AuditExportService } from './services/audit-export.service';

@Component({
    selector: 'sp-audit-download-dialog',
    templateUrl: './audit-download-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        MatButtonModule,
        MatDividerModule,
        MatStepper,
        MatStep,
        MatStepLabel,
        AuditSelectDataComponent,
        SelectFormatComponent,
        AuditDownloadComponent,
        TranslatePipe,
    ],
})
export class AuditDownloadDialogComponent implements OnInit {
    private dialogRef =
        inject<DialogRef<AuditDownloadDialogComponent>>(DialogRef);
    private auditExportService = inject(AuditExportService);

    @Input({ required: true }) query: AuditDownloadDialogModel['query'];
    @Input() eventTypes: string[] = [];
    @Input() actors: AuditActorOption[] = [];
    @Input() availableColumns: AuditExportColumn[] = [];

    dialogModel: AuditDownloadDialogModel;
    exportConfig: AuditExportConfig;
    readonly availableFormats: FormatExportConfig['format'][] = ['json', 'csv'];
    downloading = false;
    downloadFailed = false;

    get hasSelectedColumns(): boolean {
        return this.exportConfig.columns.some(column => column.selected);
    }

    ngOnInit(): void {
        this.dialogModel = {
            query: { ...this.query },
            eventTypes: this.eventTypes,
            actors: this.actors,
            availableColumns: this.availableColumns,
        };
        this.exportConfig = {
            dateRangeSelection: 'current',
            fromDate: new Date(this.query.from),
            toDate: new Date(this.query.to),
            columns: this.availableColumns.map(column => ({
                ...column,
                selected: true,
            })),
            formatExportConfig: {
                format: 'csv',
                delimiter: 'comma',
                headerColumnName: 'label',
            },
        };
    }

    close(): void {
        this.dialogRef.close();
    }

    download(stepper: MatStepper): void {
        if (this.downloading || !this.hasSelectedColumns) {
            return;
        }

        this.downloading = true;
        this.downloadFailed = false;
        stepper.next();
        this.auditExportService
            .download(this.dialogModel, this.exportConfig)
            .subscribe({
                next: () => (this.downloading = false),
                error: () => {
                    this.downloading = false;
                    this.downloadFailed = true;
                },
            });
    }
}
