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
import { AuditService } from '@streampipes/platform-services';
import { TranslateService } from '@ngx-translate/core';
import { Observable, map, tap } from 'rxjs';
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

        const selectedColumns = exportConfig.columns.filter(
            column => column.selected,
        );
        const formatConfig = exportConfig.formatExportConfig;
        const headers = selectedColumns.map(column =>
            formatConfig.format === 'json' ||
            formatConfig.headerColumnName === 'key'
                ? column.id
                : this.translate.instant(column.label),
        );
        const delimiter =
            formatConfig.format === 'csv' ? formatConfig.delimiter : undefined;
        return this.auditService
            .download(
                query,
                selectedColumns.map(column => column.id),
                headers,
                formatConfig.format,
                delimiter,
            )
            .pipe(
                tap(file =>
                    this.saveFile(
                        file,
                        this.contentType(formatConfig.format),
                        `audit-log.${formatConfig.format}`,
                    ),
                ),
                map(() => undefined),
            );
    }

    private saveFile(data: BlobPart, type: string, filename: string): void {
        const link = document.createElement('a');
        const url = URL.createObjectURL(new Blob([data], { type }));
        link.href = url;
        link.download = filename;
        link.click();
        URL.revokeObjectURL(url);
    }

    private contentType(format: 'csv' | 'json' | 'xlsx'): string {
        switch (format) {
            case 'csv':
                return 'text/csv;charset=utf-8';
            case 'json':
                return 'application/json;charset=utf-8';
            case 'xlsx':
                return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
        }
    }
}
