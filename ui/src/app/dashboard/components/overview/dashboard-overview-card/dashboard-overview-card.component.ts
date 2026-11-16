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
import { TranslatePipe } from '@ngx-translate/core';
import { DashboardSummaryDto } from '@streampipes/platform-services';
import {
    DateFormatService,
    SpOverviewCardComponent,
    SpOverviewCardFactComponent,
    SpOverviewCardFactsComponent,
} from '@streampipes/shared-ui';

/** Card of a dashboard in the dashboard overview. */
@Component({
    selector: 'sp-dashboard-overview-card',
    templateUrl: './dashboard-overview-card.component.html',
    styleUrls: ['./dashboard-overview-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButton,
        MatIcon,
        SpOverviewCardComponent,
        SpOverviewCardFactComponent,
        SpOverviewCardFactsComponent,
        TranslatePipe,
    ],
})
export class DashboardOverviewCardComponent {
    readonly dashboard = input.required<DashboardSummaryDto>();
    readonly icon = input<string>();
    readonly accentColor = input<string>();
    readonly canEdit = input(false);

    readonly showDashboard = output<void>();
    readonly editDashboard = output<void>();

    private readonly dateFormatService = inject(DateFormatService);

    readonly lastModified = computed(() =>
        this.dateFormatService.formatDate(
            this.dashboard().lastModifiedEpochMs ?? undefined,
        ),
    );
    readonly created = computed(() =>
        this.dateFormatService.formatDate(
            this.dashboard().createdAtEpochMs ?? undefined,
        ),
    );
}
