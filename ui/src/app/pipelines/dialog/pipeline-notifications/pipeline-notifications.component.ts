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
import { Component, Input } from '@angular/core';

@Component({
    selector: 'sp-pipeline-notifications',
    templateUrl: './pipeline-notifications.component.html',
    styleUrls: ['./pipeline-notifications.component.scss'],
})
export class PipelineNotificationsComponent {
    @Input()
    pipeline: Pipeline;

    constructor(
        private dialogRef: DialogRef<PipelineNotificationsComponent>,
        private pipelineService: PipelineService,
    ) {}

    acknowledgeAndClose() {
        this.pipeline.pipelineNotifications = [];
        if (this.pipeline.healthStatus === 'REQUIRES_ATTENTION') {
            this.pipeline.healthStatus = 'OK';
        }
        this.pipelineService.updatePipeline(this.pipeline).subscribe(msg => {
            this.dialogRef.close();
        });
    }
}
