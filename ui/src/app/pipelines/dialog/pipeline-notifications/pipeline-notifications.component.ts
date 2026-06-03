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

import { DialogRef } from '@streampipes/shared-ui';
import { Pipeline, PipelineService } from '@streampipes/platform-services';
import { Component, Input, inject } from '@angular/core';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-pipeline-notifications',
    templateUrl: './pipeline-notifications.component.html',
    styleUrls: ['./pipeline-notifications.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        MatDivider,
        MatButton,
        TranslatePipe,
    ],
})
export class PipelineNotificationsComponent {
    private dialogRef =
        inject<DialogRef<PipelineNotificationsComponent>>(DialogRef);
    private pipelineService = inject(PipelineService);

    @Input()
    pipeline: Pipeline;

    acknowledgeAndClose() {
        this.pipeline.pipelineNotifications = [];
        if (this.pipeline.healthStatus === 'REQUIRES_ATTENTION') {
            this.pipeline.healthStatus = 'OK';
        }
        this.pipelineService.updatePipeline(this.pipeline).subscribe(_msg => {
            this.dialogRef.close();
        });
    }
}
