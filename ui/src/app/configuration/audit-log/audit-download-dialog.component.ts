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
    inject,
    OnInit,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatStep, MatStepLabel, MatStepper } from '@angular/material/stepper';
import {
    AuditEntry,
    AuditQuery,
    AuditService,
} from '@streampipes/platform-services';
import {
    DateInputComponent,
    DialogRef,
    FormFieldComponent,
    SearchSelectComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { expand, reduce } from 'rxjs';

interface AuditExportColumn {
    id: keyof AuditEntry;
    label: string;
    selected: boolean;
}

interface AuditActorOption {
    id: string;
    label: string;
    username?: string;
}

type AuditExportFormat = 'csv' | 'json';
type CsvDelimiter = 'comma' | 'semicolon';
type CsvHeader = 'key' | 'label';

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
        MatRadioGroup,
        MatRadioButton,
        MatStepper,
        MatStep,
        MatStepLabel,
        SplitSectionComponent,
        FormFieldComponent,
        SearchSelectComponent,
        DateInputComponent,
        TranslatePipe,
    ],
})
export class AuditDownloadDialogComponent implements OnInit {
    private dialogRef =
        inject<DialogRef<AuditDownloadDialogComponent>>(DialogRef);
    private auditService = inject(AuditService);
    private translate = inject(TranslateService);

    query: AuditQuery;
    eventTypes: string[] = [];
    actors: AuditActorOption[] = [];
    fromDate: Date;
    toDate: Date;
    format: AuditExportFormat = 'csv';
    delimiter: CsvDelimiter = 'comma';
    headerColumnName: CsvHeader = 'label';
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

    readonly eventTypeFromText = (text: string): string => text.trim();
    readonly actorSearchText = (actor: AuditActorOption): string =>
        actor.label + ' ' + actor.id;
    readonly actorFromText = (text: string): AuditActorOption => {
        const value = text.trim();
        const byId = this.actors.find(actor => actor.id === value);
        const byUsername = this.actors.filter(
            actor => actor.username?.toLowerCase() === value.toLowerCase(),
        );
        return (
            byId ??
            (byUsername.length === 1
                ? byUsername[0]
                : { id: value, label: value })
        );
    };

    get selectedActor(): AuditActorOption | undefined {
        return this.query.actor
            ? this.actorFromText(this.query.actor)
            : undefined;
    }

    get outcomeOptions(): { id: string; label: string }[] {
        return [
            { id: 'succeeded', label: 'Succeeded' },
            { id: 'failed', label: 'Failed' },
            { id: 'partial', label: 'Partial' },
            { id: 'requested', label: 'Requested' },
            { id: 'denied', label: 'Denied' },
        ];
    }

    get selectedOutcome(): { id: string; label: string } | undefined {
        return this.outcomeOptions.find(
            option => option.id === this.query.outcome,
        );
    }

    ngOnInit(): void {
        this.fromDate = new Date(this.query.from);
        this.toDate = new Date(this.query.to);
    }

    onEventTypeChange(value: string | string[] | undefined): void {
        this.query.eventType = typeof value === 'string' ? value : '';
    }

    onActorChange(
        value: AuditActorOption | AuditActorOption[] | undefined,
    ): void {
        this.query.actor = value && !Array.isArray(value) ? value.id : '';
    }

    onOutcomeChange(
        value:
            | { id: string; label: string }
            | { id: string; label: string }[]
            | undefined,
    ): void {
        this.query.outcome = value && !Array.isArray(value) ? value.id : '';
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
        const query = {
            ...this.query,
            from: this.fromDate.toISOString(),
            to: this.toDate.toISOString(),
            limit: 200,
            cursor: undefined,
        };
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
                    this.saveExport(entries);
                    this.downloading = false;
                },
                error: () => {
                    this.downloading = false;
                    this.downloadFailed = true;
                },
            });
    }

    private saveExport(entries: AuditEntry[]): void {
        if (this.format === 'json') {
            this.saveFile(
                JSON.stringify(this.selectedEntries(entries), null, 2),
                'application/json;charset=utf-8',
                'audit-log.json',
            );
            return;
        }
        const selectedColumns = this.columns.filter(column => column.selected);
        const delimiter = this.delimiter === 'comma' ? ',' : ';';
        const csv = [
            selectedColumns
                .map(column => this.csvHeader(column))
                .join(delimiter),
            ...entries.map(entry =>
                selectedColumns
                    .map(column => this.escape(entry[column.id]))
                    .join(delimiter),
            ),
        ].join('\r\n');
        this.saveFile(csv, 'text/csv;charset=utf-8', 'audit-log.csv');
    }

    private selectedEntries(entries: AuditEntry[]): Record<string, string>[] {
        const selectedColumns = this.columns.filter(column => column.selected);
        return entries.map(entry =>
            Object.fromEntries(
                selectedColumns.map(column => [
                    column.id,
                    entry[column.id] ?? '',
                ]),
            ),
        );
    }

    private csvHeader(column: AuditExportColumn): string {
        const value =
            this.headerColumnName === 'key'
                ? column.id
                : this.translate.instant(column.label);
        return this.escape(value);
    }

    private saveFile(data: string, type: string, filename: string): void {
        const link = document.createElement('a');
        const url = URL.createObjectURL(new Blob([data], { type }));
        link.href = url;
        link.download = filename;
        link.click();
        URL.revokeObjectURL(url);
    }

    private escape(value: string | null | undefined): string {
        return '"' + (value ?? '').replaceAll('"', '""') + '"';
    }
}
