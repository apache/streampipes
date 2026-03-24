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

import { Component, Input } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatButton } from '@angular/material/button';
import {
    LayoutDirective,
    LayoutAlignDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { DatePipe, NgClass } from '@angular/common';
import { DialogRef } from '@streampipes/shared-ui';
import {
    AdapterHealthStatus,
    HealthCheckStatus,
} from '../../../model/adapter-health-status.model';

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
    ],
})
export class AdapterHealthDetailsDialogComponent {
    @Input() healthStatus: AdapterHealthStatus | null;

    showDetails = false;
    HealthCheckStatus = HealthCheckStatus;

    constructor(
        private dialogRef: DialogRef<AdapterHealthDetailsDialogComponent>,
    ) {}

    getStatusIcon = (status: HealthCheckStatus) =>
        status === HealthCheckStatus.HEALTHY
            ? 'check_circle'
            : status === HealthCheckStatus.UNHEALTHY
              ? 'error'
              : 'help_outline';

    getStatusClass = (status: HealthCheckStatus) =>
        status === HealthCheckStatus.HEALTHY
            ? 'status-healthy'
            : status === HealthCheckStatus.UNHEALTHY
              ? 'status-unhealthy'
              : 'status-unknown';

    getStatusLabel = (status: HealthCheckStatus) =>
        status === HealthCheckStatus.HEALTHY
            ? 'Healthy'
            : status === HealthCheckStatus.UNHEALTHY
              ? 'Unhealthy'
              : 'Unknown';

    close = () => this.dialogRef.close();
}
