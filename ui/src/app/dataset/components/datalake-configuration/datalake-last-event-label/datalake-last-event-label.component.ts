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

import { Component, inject, Input, OnChanges } from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';
import { SpLabelComponent } from '@streampipes/shared-ui';
import { LastUpdatedFormatterService } from '../../../../core-services/time-formatting/last-updated-formatter.service';

@Component({
    selector: 'sp-datalake-last-event-label',
    templateUrl: './datalake-last-event-label.component.html',
    styleUrls: ['./datalake-last-event-label.component.scss'],
    imports: [MatTooltip, SpLabelComponent],
})
export class DatalakeLastEventLabelComponent implements OnChanges {
    @Input()
    lastEvent: number | null;

    @Input()
    currentTime = Date.now();

    displayValue = 'n/a';
    tooltipValue = '';
    usesRelativeTime = false;

    private lastUpdatedFormatterService = inject(LastUpdatedFormatterService);

    ngOnChanges(): void {
        const relativeTime =
            this.lastUpdatedFormatterService.formatRelativeLastUpdatedAt(
                this.lastEvent,
                this.currentTime,
            );
        const exactTime =
            this.lastUpdatedFormatterService.formatExactLastUpdatedAt(
                this.lastEvent,
            );

        this.usesRelativeTime = !!relativeTime;
        this.displayValue = relativeTime ?? exactTime;
        this.tooltipValue = relativeTime ? exactTime : '';
    }
}
