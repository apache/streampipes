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

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatStep, MatStepLabel, MatStepper } from '@angular/material/stepper';
import {
    AuditEntry,
    AuditQuery,
    AuditService,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { expand, reduce } from 'rxjs';

interface AuditExportColumn {
    id: keyof AuditEntry;
    label: string;
    selected: boolean;
}

@Component({
    selector: 'sp-audit-download-dialog',
    templateUrl: './audit-download-dialog.component.html',
    styleUrls: ['./audit-download-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FormsModule,
        MatButtonModule,
        MatCheckboxModule,
        MatDividerModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatStepper,
        MatStep,
        MatStepLabel,
        TranslatePipe,
    ],
})
export class AuditDownloadDialogComponent {
    private dialogRef =
        inject<DialogRef<AuditDownloadDialogComponent>>(DialogRef);
    private auditService = inject(AuditService);
    private translate = inject(TranslateService);

    query: AuditQuery;
    downloading = false;
    downloadFailed = false;
    readonly columns: AuditExportColumn[] = [
        { id: 'storedAt', label: 'Stored at', selected: true },
        { id: 'eventType', label: 'Event type', selected: true },
        { id: 'outcome', label: 'Outcome', selected: true },
        { id: 'actor', label: 'Actor username', selected: true },
        { id: 'resourceType', label: 'Resource type', selected: true },
        { id: 'resourceId', label: 'Resource ID', selected: true },
        { id: 'eventId', label: 'Event ID', selected: true },
    ];

    get hasSelectedColumns(): boolean {
        return this.columns.some(column => column.selected);
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
        const query = { ...this.query, limit: 200, cursor: undefined };
        this.auditService
            .getEvents(query)
            .pipe(
                expand(page =>
                    page.nextCursor
                        ? this.auditService.getEvents({
                              ...query,
                              cursor: page.nextCursor,
                          })
                        : [],
                ),
                reduce(
                    (entries: AuditEntry[], page) => entries.concat(page.items),
                    [],
                ),
            )
            .subscribe({
                next: entries => {
                    this.saveCsv(entries);
                    this.downloading = false;
                    stepper.next();
                },
                error: () => {
                    this.downloading = false;
                    this.downloadFailed = true;
                },
            });
    }

    private saveCsv(entries: AuditEntry[]): void {
        const selectedColumns = this.columns.filter(column => column.selected);
        const csv = [
            selectedColumns
                .map(column =>
                    this.escape(this.translate.instant(column.label)),
                )
                .join(','),
            ...entries.map(entry =>
                selectedColumns
                    .map(column => this.escape(entry[column.id]))
                    .join(','),
            ),
        ].join('\r\n');
        const link = document.createElement('a');
        const url = URL.createObjectURL(
            new Blob([csv], { type: 'text/csv;charset=utf-8' }),
        );
        link.href = url;
        link.download = 'audit-log.csv';
        link.click();
        URL.revokeObjectURL(url);
    }

    private escape(value: string | null | undefined): string {
        return '"' + (value ?? '').replaceAll('"', '""') + '"';
    }
}
