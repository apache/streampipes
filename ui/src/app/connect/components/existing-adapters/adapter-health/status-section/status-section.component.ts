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
import { NgClass } from '@angular/common';
import {
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { HealthCheckStatus } from '../../../../model/adapter-health-status.model';

@Component({
    selector: 'sp-adapter-health-status-section',
    templateUrl: './status-section.component.html',
    styleUrls: ['./status-section.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        NgClass,
    ],
})
export class AdapterHealthStatusSectionComponent {
    @Input() label: string;
    @Input() status: HealthCheckStatus = HealthCheckStatus.UNKNOWN;
    @Input() message = '';
    @Input() supported = true;
    @Input() unsupportedLabel = 'No support yet';
    @Input() unsupportedMessage = '';
    @Input() checking = false;

    get showMessage(): boolean {
        return this.supported ? !!this.message : !!this.unsupportedMessage;
    }

    get renderedMessage(): string {
        return this.supported ? this.message : this.unsupportedMessage;
    }

    get statusClass(): string {
        if (this.checking) {
            return 'status-unknown';
        }

        if (this.status === HealthCheckStatus.HEALTHY) {
            return 'status-healthy';
        }

        if (this.status === HealthCheckStatus.UNHEALTHY) {
            return 'status-unhealthy';
        }

        return 'status-unknown';
    }

    get statusLabel(): string {
        if (!this.supported) {
            return this.unsupportedLabel;
        }

        if (this.checking) {
            return 'Checking...';
        }

        if (this.status === HealthCheckStatus.HEALTHY) {
            return 'Healthy';
        }

        if (this.status === HealthCheckStatus.UNHEALTHY) {
            return 'Unhealthy';
        }

        return 'Unknown';
    }

    get lightClass(): string {
        if (this.checking) {
            return 'light-neutral';
        }

        if (this.status === HealthCheckStatus.HEALTHY) {
            return 'light-green';
        }

        if (this.status === HealthCheckStatus.UNHEALTHY) {
            return 'light-red';
        }

        return 'light-neutral';
    }
}
