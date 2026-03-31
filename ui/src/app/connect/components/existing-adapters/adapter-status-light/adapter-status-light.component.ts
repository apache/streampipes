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
import { NgClass } from '@angular/common';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTooltip } from '@angular/material/tooltip';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import {
    AdapterHealthStatus,
    HealthCheckStatus,
} from '../../../model/adapter-health-status.model';
import { AdapterHealthDetailsDialogComponent } from '../adapter-health-details-dialog/adapter-health-details-dialog.component';

@Component({
    selector: 'sp-adapter-status-light',
    templateUrl: './adapter-status-light.component.html',
    styleUrls: ['./adapter-status-light.component.scss'],
    imports: [LayoutDirective, LayoutAlignDirective, MatTooltip, NgClass],
})
export class AdapterStatusLightComponent {
    @Input() adapterRunning: boolean;
    @Input() healthStatus: AdapterHealthStatus | null = null;

    private dialogService = inject(DialogService);

    get statusClass(): string {
        if (!this.adapterRunning) {
            return 'light-neutral';
        }
        if (
            !this.healthStatus ||
            this.healthStatus.overallStatus === HealthCheckStatus.UNKNOWN
        ) {
            return 'light-neutral';
        }
        return this.healthStatus.overallStatus === HealthCheckStatus.UNHEALTHY
            ? 'light-red'
            : 'light-green';
    }

    openHealthDetails(event: MouseEvent): void {
        event.stopPropagation();
        if (!this.adapterRunning) {
            return;
        }
        this.dialogService.open(AdapterHealthDetailsDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: 'Adapter Health Status',
            width: '90vw',
            data: { healthStatus: this.healthStatus },
        });
    }
}
