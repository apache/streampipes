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

import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import {
    LayoutDirective,
    LayoutAlignDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { DatePipe, NgClass } from '@angular/common';
import { DialogRef } from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';
import {
    AdapterHealthStatus,
    HealthCheckStatus,
} from '../../../model/adapter-health-status.model';
import { AdapterHealthService } from '../../../services/adapter-health.service';

@Component({
    selector: 'sp-adapter-health-details-dialog',
    templateUrl: './adapter-health-details-dialog.component.html',
    styleUrls: ['./adapter-health-details-dialog.component.scss'],
    imports: [
        MatDivider,
        MatIcon,
        MatButton,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        DatePipe,
        NgClass,
        MatIconButton,
        MatTooltip,
        TranslatePipe,
    ],
})
export class AdapterHealthDetailsDialogComponent implements OnInit, OnDestroy {
    @Input() healthStatus: AdapterHealthStatus | null;

    showDetails = true;
    isTriggering = false;
    HealthCheckStatus = HealthCheckStatus;
    timeUntilNextCheck = '';

    private countdownInterval: ReturnType<typeof setInterval>;
    private statusPollingInterval: ReturnType<typeof setInterval>;

    constructor(
        private dialogRef: DialogRef<AdapterHealthDetailsDialogComponent>,
        private adapterHealthService: AdapterHealthService,
    ) {}

    get isCheckInProgress(): boolean {
        if (!this.healthStatus?.nextCheckTimestamp) {
            return this.isTriggering;
        }
        return (
            this.isTriggering ||
            Date.now() >= this.healthStatus.nextCheckTimestamp - 500
        );
    }

    ngOnInit(): void {
        this.updateCountdown();
        this.countdownInterval = setInterval(
            () => this.updateCountdown(),
            1000,
        );
        this.statusPollingInterval = setInterval(() => this.pollStatus(), 5000);
    }

    ngOnDestroy(): void {
        clearInterval(this.countdownInterval);
        clearInterval(this.statusPollingInterval);
    }

    private pollStatus(): void {
        if (!this.healthStatus?.adapterId) return;
        this.adapterHealthService.getAllHealthStatuses().subscribe(statuses => {
            const updated = statuses.get(this.healthStatus.adapterId);
            if (updated) {
                this.healthStatus = updated;
                this.updateCountdown();
            }
        });
    }

    private updateCountdown(): void {
        if (!this.healthStatus?.nextCheckTimestamp) return;
        const diff = this.healthStatus.nextCheckTimestamp - Date.now();
        if (diff <= 0) {
            this.timeUntilNextCheck = 'Check in progress...';
            return;
        }
        const seconds = Math.floor((diff / 1000) % 60);
        const minutes = Math.floor(diff / 60000);
        this.timeUntilNextCheck =
            minutes > 0 ? `in ${minutes}m ${seconds}s` : `in ${seconds}s`;
    }

    triggerCheck(): void {
        if (!this.healthStatus) return;
        this.isTriggering = true;
        this.adapterHealthService
            .triggerHealthCheck(this.healthStatus.adapterId)
            .subscribe({
                next: () =>
                    setTimeout(() => {
                        this.isTriggering = false;
                        this.pollStatus();
                    }, 3000),
                error: () => (this.isTriggering = false),
            });
    }

    getLightClass = (status: HealthCheckStatus) =>
        this.isCheckInProgress
            ? 'light-neutral'
            : status === HealthCheckStatus.HEALTHY
              ? 'light-green'
              : status === HealthCheckStatus.UNHEALTHY
                ? 'light-red'
                : 'light-neutral';

    getStatusClass = (status: HealthCheckStatus) =>
        this.isCheckInProgress
            ? 'status-unknown'
            : status === HealthCheckStatus.HEALTHY
              ? 'status-healthy'
              : status === HealthCheckStatus.UNHEALTHY
                ? 'status-unhealthy'
                : 'status-unknown';

    getStatusLabel = (status: HealthCheckStatus) =>
        this.isCheckInProgress
            ? 'Checking...'
            : status === HealthCheckStatus.HEALTHY
              ? 'Healthy'
              : status === HealthCheckStatus.UNHEALTHY
                ? 'Unhealthy'
                : 'Unknown';

    get dataSourceProbableCause(): string {
        if (!this.healthStatus) {
            return '';
        }

        const message = this.healthStatus.dataSourceHealthMessage?.trim();
        const details = this.healthStatus.dataSourceHealthDetails;

        // Match SpLogMessage.from(exception): prefer the wrapped cause message.
        const causeFromDetails = this.extractCauseFromDetails(details);
        if (causeFromDetails) {
            return causeFromDetails;
        }

        if (message && !message.startsWith('Health check exception:')) {
            return message;
        }

        if (message) {
            return message.replace('Health check exception:', '').trim();
        }

        return '';
    }

    private extractCauseFromDetails(details?: string | null): string {
        if (!details) {
            return '';
        }

        const lines = details
            .split('\n')
            .map(line => line.trim())
            .filter(Boolean);

        const causedByLine = lines.find(line => line.startsWith('Caused by:'));
        if (causedByLine) {
            return this.extractExceptionMessage(
                causedByLine.replace('Caused by:', '').trim(),
            );
        }

        const firstRelevantLine = lines.find(line => !line.startsWith('at '));
        if (!firstRelevantLine) {
            return '';
        }

        return this.extractExceptionMessage(firstRelevantLine);
    }

    private extractExceptionMessage(line: string): string {
        const separatorIndex = line.indexOf(':');
        if (separatorIndex < 0 || separatorIndex === line.length - 1) {
            return line;
        }

        return line.substring(separatorIndex + 1).trim();
    }

    close = () => this.dialogRef.close();
}
