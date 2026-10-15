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

import { Injectable, inject } from '@angular/core';
import { AuditEntry, AuditService } from '@streampipes/platform-services';
import { TranslateService } from '@ngx-translate/core';
import { EMPTY, Observable, expand, map, reduce, tap } from 'rxjs';
import { AuditDownloadDialogModel } from '../model/audit-download-dialog.model';
import { AuditExportConfig } from '../model/audit-export-config.model';

@Injectable({
    providedIn: 'root',
})
export class AuditExportService {
    private auditService = inject(AuditService);
    private translate = inject(TranslateService);

    download(
        dialogModel: AuditDownloadDialogModel,
        exportConfig: AuditExportConfig,
    ): Observable<void> {
        const query = {
            ...dialogModel.query,
            from: exportConfig.fromDate.toISOString(),
            to: exportConfig.toDate.toISOString(),
            limit: 200,
            cursor: undefined,
        };

        return this.auditService.getEvents(query).pipe(
            expand(page =>
                page.nextCursor
                    ? this.auditService.getEvents({
                          ...query,
                          cursor: page.nextCursor,
                      })
                    : EMPTY,
            ),
            reduce(
                (entries: AuditEntry[], page) => entries.concat(page.items),
                [],
            ),
            tap(entries => this.saveExport(entries, exportConfig)),
            map(() => undefined),
        );
    }

    private saveExport(
        entries: AuditEntry[],
        exportConfig: AuditExportConfig,
    ): void {
        if (exportConfig.formatExportConfig.format === 'json') {
            this.saveFile(
                JSON.stringify(
                    this.selectedEntries(entries, exportConfig),
                    null,
                    2,
                ),
                'application/json;charset=utf-8',
                'audit-log.json',
            );
            return;
        }

        const selectedColumns = exportConfig.columns.filter(
            column => column.selected,
        );
        const delimiter =
            exportConfig.formatExportConfig.format === 'csv' &&
            exportConfig.formatExportConfig.delimiter === 'semicolon'
                ? ';'
                : ',';
        const headerColumnName =
            exportConfig.formatExportConfig.format === 'csv'
                ? exportConfig.formatExportConfig.headerColumnName
                : 'label';
        const csv = [
            selectedColumns
                .map(column =>
                    this.escape(
                        headerColumnName === 'key'
                            ? column.id
                            : this.translate.instant(column.label),
                    ),
                )
                .join(delimiter),
            ...entries.map(entry =>
                selectedColumns
                    .map(column => this.escape(entry[column.id]))
                    .join(delimiter),
            ),
        ].join('\r\n');
        this.saveFile(csv, 'text/csv;charset=utf-8', 'audit-log.csv');
    }

    private selectedEntries(
        entries: AuditEntry[],
        exportConfig: AuditExportConfig,
    ): Record<string, unknown>[] {
        const selectedColumns = exportConfig.columns.filter(
            column => column.selected,
        );
        return entries.map(entry =>
            Object.fromEntries(
                selectedColumns.map(column => [
                    column.id,
                    entry[column.id] ?? '',
                ]),
            ),
        );
    }

    private saveFile(data: string, type: string, filename: string): void {
        const link = document.createElement('a');
        const url = URL.createObjectURL(new Blob([data], { type }));
        link.href = url;
        link.download = filename;
        link.click();
        URL.revokeObjectURL(url);
    }

    private escape(value: unknown): string {
        return `"${String(value ?? '').replaceAll('"', '""')}"`;
    }
}
