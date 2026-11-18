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
    SpOverviewCardComponent,
    SpOverviewCardFactComponent,
    SpOverviewCardFactsComponent,
    SpSpinnerComponent,
} from '@streampipes/shared-ui';
import { LastUpdatedFormatterService } from '../../../../core-services/time-formatting/last-updated-formatter.service';
import { DatasetOverviewEntry } from '../dataset-overview-entry';

/**
 * Card of a dataset in the dataset overview. The last event is passed on its
 * own because the overview loads it per page and updates the entry in place.
 */
@Component({
    selector: 'sp-dataset-overview-card',
    templateUrl: './dataset-overview-card.component.html',
    styleUrls: ['./dataset-overview-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButton,
        MatIcon,
        MatTooltip,
        SpOverviewCardComponent,
        SpOverviewCardFactComponent,
        SpOverviewCardFactsComponent,
        SpSpinnerComponent,
        TranslatePipe,
    ],
})
export class DatasetOverviewCardComponent {
    readonly dataset = input.required<DatasetOverviewEntry>();
    readonly lastEvent = input<number | null>(null);
    readonly lastEventLoading = input(false);
    readonly currentTime = input(Date.now());
    readonly icon = input<string>();
    readonly accentColor = input<string>();
    readonly canConfigureRetention = input(false);

    readonly showDetails = output<void>();
    readonly download = output<void>();
    readonly configureRetention = output<void>();
    readonly showRetentionLog = output<void>();

    private readonly lastUpdatedFormatter = inject(LastUpdatedFormatterService);
    private readonly translate = inject(TranslateService);

    readonly lastEventText = computed(() => {
        const lastEvent = this.lastEvent();
        if (!lastEvent) {
            return undefined;
        }

        return (
            this.lastUpdatedFormatter.formatRelativeLastUpdatedAt(
                lastEvent,
                this.currentTime(),
            ) ??
            new Intl.DateTimeFormat(this.translate.getCurrentLang() || 'en', {
                dateStyle: 'medium',
            }).format(lastEvent)
        );
    });

    readonly exactLastEvent = computed(() =>
        this.lastEvent()
            ? this.lastUpdatedFormatter.formatExactLastUpdatedAt(
                  this.lastEvent(),
              )
            : '',
    );

    /** Exact time of the last retention export, if there was one. */
    readonly lastExport = computed(() => {
        const lastExport = Date.parse(this.dataset().lastExport ?? '');
        return Number.isNaN(lastExport)
            ? undefined
            : this.lastUpdatedFormatter.formatExactLastUpdatedAt(lastExport);
    });
}
