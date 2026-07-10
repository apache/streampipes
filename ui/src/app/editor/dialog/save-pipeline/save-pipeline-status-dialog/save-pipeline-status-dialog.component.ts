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

import { Component, Input, inject } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { PipelineOperationStatus } from '@streampipes/platform-services';
import { StatusIndicator } from '../../../../core-ui/multi-step-status-indicator/multi-step-status-indicator.model';
import { PipelineAction } from '../../../../pipelines/model/pipeline-model';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MultiStepStatusIndicatorComponent } from '../../../../core-ui/multi-step-status-indicator/multi-step-status-indicator.component';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { PipelineStartedStatusComponent } from '../../../../core-ui/pipeline/pipeline-started-status/pipeline-started-status.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-save-pipeline-status-dialog',
    templateUrl: './save-pipeline-status-dialog.component.html',
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        MultiStepStatusIndicatorComponent,
        MatDivider,
        MatButton,
        PipelineStartedStatusComponent,
        TranslatePipe,
    ],
})
export class SavePipelineStatusDialogComponent {
    private dialogRef = inject(DialogRef<SavePipelineStatusDialogComponent>);

    @Input()
    statusIndicators: StatusIndicator[] = [];

    @Input()
    finalPipelineOperationStatus?: PipelineOperationStatus;

    @Input()
    pipelineAction?: PipelineAction;

    close(): void {
        this.dialogRef.close();
    }
}
