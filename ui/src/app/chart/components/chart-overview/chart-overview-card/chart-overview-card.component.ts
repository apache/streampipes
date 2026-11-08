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
    input,
    output,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import {
    SpOverviewCardComponent,
    SpOverviewCardFactComponent,
    SpOverviewCardFactsComponent,
} from '@streampipes/shared-ui';
import { ChartOverviewRow } from '../chart-overview-row';

/** Card of a chart in the chart overview. The icon shows the chart type. */
@Component({
    selector: 'sp-chart-overview-card',
    templateUrl: './chart-overview-card.component.html',
    styleUrls: ['./chart-overview-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButton,
        MatIcon,
        MatTooltip,
        SpOverviewCardComponent,
        SpOverviewCardFactComponent,
        SpOverviewCardFactsComponent,
        TranslatePipe,
    ],
})
export class ChartOverviewCardComponent {
    readonly chart = input.required<ChartOverviewRow>();
    readonly accentColor = input<string>();
    readonly canEdit = input(false);

    /** The title opens the chart like a table row does. */
    readonly openChart = output<void>();
    readonly showChart = output<void>();
    readonly editChart = output<void>();
}
