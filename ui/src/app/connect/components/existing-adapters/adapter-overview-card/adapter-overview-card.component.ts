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
    computed,
    inject,
    input,
    output,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    AdapterSummaryDto,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import {
    SpOverviewCardComponent,
    SpOverviewCardFactComponent,
    SpOverviewCardFactsComponent,
    SpSpinnerComponent,
    SpStatusLabelComponent,
} from '@streampipes/shared-ui';
import { LastUpdatedFormatterService } from '../../../../core-services/time-formatting/last-updated-formatter.service';

@Component({
    selector: 'sp-adapter-overview-card',
    templateUrl: './adapter-overview-card.component.html',
    styleUrls: ['./adapter-overview-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButton,
        MatIcon,
        MatTooltip,
        SpOverviewCardComponent,
        SpOverviewCardFactComponent,
        SpOverviewCardFactsComponent,
        SpSpinnerComponent,
        SpStatusLabelComponent,
        TranslatePipe,
    ],
})
export class AdapterOverviewCardComponent {
    readonly adapter = input.required<AdapterSummaryDto>();
    readonly metrics = input<SpMetricsEntry>();
    readonly iconUrl = input<string>();
    readonly accentColor = input<string>();
    readonly operationInProgress = input(false);
    readonly currentTime = input(Date.now());

    readonly startAdapter = output<void>();
    readonly stopAdapter = output<void>();
    readonly showDetails = output<void>();
    readonly editAdapter = output<void>();

    private readonly lastUpdatedFormatter = inject(LastUpdatedFormatterService);
    private readonly translate = inject(TranslateService);

    private get locale(): string {
        return this.translate.getCurrentLang() || 'en';
    }

    private readonly lastTimestamp = computed(
        () => this.metrics()?.lastTimestamp,
    );

    readonly messageCount = computed(() =>
        new Intl.NumberFormat(this.locale).format(
            this.metrics()?.messagesOut?.counter ?? 0,
        ),
    );
    readonly exactLastMessage = computed(() =>
        this.lastTimestamp()
            ? this.lastUpdatedFormatter.formatExactLastUpdatedAt(
                  this.lastTimestamp(),
              )
            : undefined,
    );
    readonly lastMessage = computed(() => {
        const lastTimestamp = this.lastTimestamp();
        if (!lastTimestamp) {
            return undefined;
        }

        return (
            this.lastUpdatedFormatter.formatRelativeLastUpdatedAt(
                lastTimestamp,
                this.currentTime(),
            ) ??
            new Intl.DateTimeFormat(this.locale, {
                dateStyle: 'medium',
            }).format(lastTimestamp)
        );
    });
}
