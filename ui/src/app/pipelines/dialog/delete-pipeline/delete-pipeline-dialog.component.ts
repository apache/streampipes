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

import { Component, inject, Input } from '@angular/core';
import { Pipeline, PipelineService } from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-delete-pipeline-dialog',
    templateUrl: './delete-pipeline-dialog.component.html',
    styleUrls: ['./delete-pipeline-dialog.component.scss'],
})
export class DeletePipelineDialogComponent {
    @Input()
    pipeline: Pipeline;

    isInProgress = false;
    currentStatus: any;

    private translateService = inject(TranslateService);
    private pipelineService = inject(PipelineService);
    private dialogRef = inject(DialogRef<DeletePipelineDialogComponent>);

    constructor() {}

    close(refreshPipelines: boolean) {
        this.dialogRef.close(refreshPipelines);
    }

    deletePipeline() {
        this.isInProgress = true;
        this.currentStatus = this.translateService.instant(
            'Deleting pipeline...',
        );
        this.pipelineService
            .deleteOwnPipeline(this.pipeline._id)
            .subscribe(data => {
                this.close(true);
            });
    }

    stopAndDeletePipeline() {
        this.isInProgress = true;
        this.currentStatus = this.translateService.instant(
            'Stopping pipeline...',
        );
        this.pipelineService.stopPipeline(this.pipeline._id).subscribe(
            data => {
                this.deletePipeline();
            },
            data => {
                this.deletePipeline();
            },
        );
    }
}
