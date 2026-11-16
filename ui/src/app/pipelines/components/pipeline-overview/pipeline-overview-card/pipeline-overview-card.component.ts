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

import { DatePipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    computed,
    input,
    output,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { PipelineSummaryDto } from '@streampipes/platform-services';
import {
    SpOverviewCardComponent,
    SpOverviewCardFactComponent,
    SpOverviewCardFactsComponent,
    SpStatusLabelComponent,
} from '@streampipes/shared-ui';
import { getPipelineStatus } from '../pipeline-status';

@Component({
    selector: 'sp-pipeline-overview-card',
    templateUrl: './pipeline-overview-card.component.html',
    styleUrls: ['./pipeline-overview-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        DatePipe,
        MatButton,
        MatIcon,
        MatTooltip,
        SpOverviewCardComponent,
        SpOverviewCardFactComponent,
        SpOverviewCardFactsComponent,
        SpStatusLabelComponent,
        TranslatePipe,
    ],
})
export class PipelineOverviewCardComponent {
    readonly pipeline = input.required<PipelineSummaryDto>();
    readonly icon = input<string>();
    readonly accentColor = input<string>();
    readonly hasWritePrivileges = input(false);
    readonly operationInProgress = input(false);

    readonly startPipeline = output<void>();
    readonly stopPipeline = output<void>();
    readonly showDetails = output<void>();
    readonly editPipeline = output<void>();
    readonly showNotifications = output<void>();

    readonly status = computed(() => getPipelineStatus(this.pipeline()));
    readonly notificationCount = computed(
        () => this.pipeline().pipelineNotifications?.length ?? 0,
    );
}
