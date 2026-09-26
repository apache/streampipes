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
    DestroyRef,
    Input,
    OnInit,
    inject,
} from '@angular/core';
import { DatePipe, JsonPipe } from '@angular/common';
import { A11yModule } from '@angular/cdk/a11y';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslatePipe } from '@ngx-translate/core';
import {
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    AuditService,
    AuditEntry,
    AuditEntryDetails,
    AuditStatus,
    UserService,
} from '@streampipes/platform-services';
import {
    DialogRef,
    SpAlertBannerComponent,
    SpLabelComponent,
} from '@streampipes/shared-ui';

import { auditOutcomeTone } from './audit-outcome';

@Component({
    selector: 'sp-audit-dialog',
    templateUrl: './audit-dialog.component.html',
    styleUrls: ['./audit-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        DatePipe,
        JsonPipe,
        A11yModule,
        MatButtonModule,
        MatProgressBarModule,
        TranslatePipe,
        LayoutDirective,
        LayoutGapDirective,
        SpAlertBannerComponent,
        SpLabelComponent,
    ],
})
export class AuditDialogComponent implements OnInit {
    readonly outcomeTone = auditOutcomeTone;
    private api = inject(AuditService);
    private users = inject(UserService);
    private dialog = inject<DialogRef<AuditDialogComponent>>(DialogRef);
    private destroyRef = inject(DestroyRef);
    private trigger = document.activeElement;
    @Input() event?: AuditEntry;
    status?: AuditStatus;
    details?: AuditEntryDetails;
    username?: string;
    loading = false;
    failed = false;
    missing = false;

    ngOnInit(): void {
        this.destroyRef.onDestroy(() => {
            if (
                this.trigger instanceof HTMLElement &&
                this.trigger.isConnected
            ) {
                this.trigger.focus();
            }
        });
        this.load();
        if (this.event) {
            this.users
                .getUserById(encodeURIComponent(this.event.actor))
                .pipe(takeUntilDestroyed(this.destroyRef))
                .subscribe({
                    next: user => (this.username = user?.username || undefined),
                    error: () => (this.username = undefined),
                });
        }
    }

    load(): void {
        this.loading = true;
        this.failed = false;
        this.missing = false;
        if (this.event) {
            this.api
                .getDetails(this.event)
                .pipe(takeUntilDestroyed(this.destroyRef))
                .subscribe({
                    next: details => {
                        this.details = details;
                        this.loading = false;
                    },
                    error: error => {
                        this.missing = error.status === 404;
                        this.failed = !this.missing;
                        this.loading = false;
                    },
                });
        } else {
            this.api
                .getStatus()
                .pipe(takeUntilDestroyed(this.destroyRef))
                .subscribe({
                    next: status => {
                        this.status = status;
                        this.loading = false;
                    },
                    error: () => {
                        this.failed = true;
                        this.loading = false;
                    },
                });
        }
    }

    outcomeLabel(outcome: string): string {
        const labels: Record<string, string> = {
            succeeded: 'Succeeded',
            failed: 'Failed',
            partial: 'Partial',
            requested: 'Requested',
            denied: 'Denied',
        };
        return labels[outcome] ?? outcome;
    }

    close(): void {
        this.dialog.close();
    }
}
