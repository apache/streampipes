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

import { Component, computed, input } from '@angular/core';
import {
    PipelineElementStatus,
    PipelineOperationStatus,
} from '@streampipes/platform-services';
import { MatIcon } from '@angular/material/icon';
import {
    SpAlertBannerComponent,
    SpLabelComponent,
} from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-pipeline-operation-status',
    templateUrl: './pipeline-operation-status.component.html',
    styleUrls: ['./pipeline-operation-status.component.scss'],
    imports: [MatIcon, SpAlertBannerComponent, SpLabelComponent, TranslatePipe],
})
export class PipelineOperationStatusComponent {
    readonly pipelineOperationStatus = input<
        PipelineOperationStatus | undefined
    >(undefined);

    readonly elementStatuses = computed(() =>
        [...(this.pipelineOperationStatus()?.elementStatus ?? [])].sort(
            (first, second) => Number(first.success) - Number(second.success),
        ),
    );

    getDisplayElementId(status: PipelineElementStatus): string {
        const elementId = status.elementId ?? '';
        const lastSeparatorIndex = elementId.lastIndexOf('/');
        return lastSeparatorIndex > 0
            ? elementId.substring(0, lastSeparatorIndex)
            : elementId;
    }
}
