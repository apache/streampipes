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
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import {
    AuditService,
    RetentionTimeConfig,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { SelectDataExportComponent } from '../../../dataset/dialog/data-retention-dialog/components/select-export/select-format.component';
import { SelectDataComponent } from '../../../dataset/dialog/data-retention-dialog/components/select-retention/select-data.component';

@Component({
    selector: 'sp-audit-retention-dialog',
    templateUrl: './audit-retention-dialog.component.html',
    styleUrls: ['./audit-retention-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FormsModule,
        MatButtonModule,
        MatDividerModule,
        TranslatePipe,
        SelectDataComponent,
        SelectDataExportComponent,
    ],
})
export class AuditRetentionDialogComponent implements OnInit {
    private api = inject(AuditService);
    private dialogRef = inject(DialogRef<AuditRetentionDialogComponent>);

    retentionConfig!: RetentionTimeConfig;
    configured = false;
    loading = true;
    saving = false;

    ngOnInit(): void {
        this.api.getRetention().subscribe({
            next: config => {
                this.configured = config !== null;
                this.retentionConfig = config ?? this.defaultConfig();
                this.loading = false;
            },
            error: () => {
                this.configured = false;
                this.retentionConfig = this.defaultConfig();
                this.loading = false;
            },
        });
    }

    save(): void {
        if (
            this.retentionConfig.dataRetentionConfig.olderThanDays < 1 ||
            this.saving
        ) {
            return;
        }
        this.saving = true;
        this.api.setRetention(this.retentionConfig).subscribe({
            next: () => this.dialogRef.close(true),
            error: () => (this.saving = false),
        });
    }

    delete(): void {
        if (!this.configured || this.saving) {
            return;
        }
        this.saving = true;
        this.api.deleteRetention().subscribe({
            next: () => this.dialogRef.close(true),
            error: () => (this.saving = false),
        });
    }

    close(): void {
        this.dialogRef.close(false);
    }

    requiresExportValidation(): boolean {
        const action = this.retentionConfig?.dataRetentionConfig?.action;
        return action === 'SAVE' || action === 'SAVEDELETE';
    }

    isExportValid(): boolean {
        const exportConfig =
            this.retentionConfig?.retentionExportConfig?.exportConfig;
        const providerId =
            this.retentionConfig?.retentionExportConfig?.exportProviderId;
        return (
            !!exportConfig?.format &&
            (exportConfig.format !== 'csv' || !!exportConfig.csvDelimiter) &&
            !!providerId
        );
    }

    private defaultConfig(): RetentionTimeConfig {
        return RetentionTimeConfig.fromData({
            dataRetentionConfig: {
                olderThanDays: 30,
                interval: 'DAILY',
                action: 'DELETE',
            },
            retentionExportConfig: {
                exportConfig: {
                    format: 'csv',
                    csvDelimiter: 'comma',
                    missingValueBehaviour: 'ignore',
                    headerColumnName: 'key',
                },
                exportProviderId: '',
            },
        } as RetentionTimeConfig);
    }
}
