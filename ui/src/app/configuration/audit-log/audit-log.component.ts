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
    OnInit,
    inject,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import {
    LayoutDirective,
    LayoutGapDirective,
    LayoutAlignDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    AuditService,
    AuditEntry,
    AuditQuery,
    AuditStatus,
} from '@streampipes/platform-services';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SpLabelComponent,
    SplitSectionComponent,
    SpBreadcrumbService,
    SpTableComponent,
    DialogService,
    FeatureCardService,
    PanelType,
} from '@streampipes/shared-ui';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subscription } from 'rxjs';
import { AuditDialogComponent } from './audit-dialog.component';
import { SpConfigurationRoutes } from '../configuration.breadcrumb';

import { auditOutcomeTone } from './audit-outcome';

@Component({
    selector: 'sp-audit-log',
    templateUrl: './audit-log.component.html',
    styleUrls: ['./audit-log.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        DatePipe,
        FormsModule,
        MatButtonModule,
        MatIconModule,
        MatTooltipModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatAutocompleteModule,
        MatProgressBarModule,
        MatTableModule,
        LayoutDirective,
        LayoutGapDirective,
        LayoutAlignDirective,
        TranslatePipe,
        SpTableComponent,
        FormFieldComponent,
        SpAlertBannerComponent,
        SpLabelComponent,
        SplitSectionComponent,
    ],
})
export class AuditLogComponent implements OnInit {
    readonly outcomeTone = auditOutcomeTone;
    private api = inject(AuditService);
    private destroyRef = inject(DestroyRef);
    private breadcrumbs = inject(SpBreadcrumbService);
    private translate = inject(TranslateService);
    private dialogs = inject(DialogService);
    private featureCards = inject(FeatureCardService);
    private listRequest?: Subscription;
    status?: AuditStatus;
    statusError = false;
    types: string[] = [];
    days = 1;
    eventType = '';
    actor = '';
    outcome = '';
    loading = false;
    failed = false;
    dataSource = new MatTableDataSource<AuditEntry>([]);
    columns = ['time', 'eventType', 'outcome', 'actor', 'resource', 'preview'];
    page = 0;
    nextCursor: string | null = null;
    private cursors: (string | undefined)[] = [undefined];
    private query?: AuditQuery;

    ngOnInit(): void {
        this.breadcrumbs.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: 'Audit Log' },
        ]);
        this.refresh();
        this.api
            .getEventTypes()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: types => (this.types = types),
                error: () => (this.types = []),
            });
    }

    refresh(): void {
        this.statusError = false;
        this.api
            .getStatus()
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: status => {
                    this.status = status;
                    if (status.enabled) {
                        this.apply();
                    }
                },
                error: () => (this.statusError = true),
            });
    }

    apply(): void {
        const to = new Date();
        this.query = {
            from: new Date(to.getTime() - this.days * 86400000).toISOString(),
            to: to.toISOString(),
            eventType: this.eventType.trim(),
            actor: this.actor.trim(),
            outcome: this.outcome,
            limit: 50,
        };
        this.cursors = [undefined];
        this.load(0);
    }

    previous(): void {
        this.load(this.page - 1);
    }
    next(): void {
        if (this.nextCursor) {
            this.cursors[this.page + 1] = this.nextCursor;
            this.load(this.page + 1);
        }
    }
    retry(): void {
        this.load(this.page);
    }

    private load(page: number): void {
        if (!this.query) {
            return;
        }
        this.listRequest?.unsubscribe();
        this.loading = true;
        this.failed = false;
        this.dataSource.data = [];
        this.page = page;
        this.listRequest = this.api
            .getEvents({ ...this.query, cursor: this.cursors[page] })
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: result => {
                    this.dataSource.data = result.items;
                    this.nextCursor = result.nextCursor;
                    this.loading = false;
                },
                error: () => {
                    this.failed = true;
                    this.loading = false;
                    this.nextCursor = null;
                },
            });
    }

    resourcePreview(
        event: AuditEntry,
    ): { type: string; id: string } | undefined {
        return event.resourceType &&
            event.resourceId &&
            this.featureCards.supportsFeatureCard(event.resourceType)
            ? { type: event.resourceType, id: event.resourceId }
            : undefined;
    }

    openResourcePreview(entry: AuditEntry, event: Event): void {
        event.stopPropagation();
        const preview = this.resourcePreview(entry);
        if (preview) {
            this.featureCards.openFeatureCard(preview.type, preview.id);
        }
    }

    showDetails(event: AuditEntry): void {
        this.dialogs.open(AuditDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            width: '42rem',
            title: this.translate.instant('Event details'),
            data: { event },
        });
    }

    showStatus(): void {
        this.dialogs.open(AuditDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            width: '32rem',
            title: this.translate.instant('Audit status'),
        });
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
}
