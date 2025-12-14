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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    DefaultLayoutAlignDirective,
    DefaultLayoutDirective,
    DefaultLayoutGapDirective,
} from '@ngbracket/ngx-layout';
import { MatIcon } from '@angular/material/icon';
import { SharedUiModule } from '@streampipes/shared-ui';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { PipelineHealthStatus } from '@streampipes/platform-services';

@Component({
    selector: 'sp-pipeline-preview-meta',
    templateUrl: './pipeline-preview-meta.component.html',
    styleUrls: ['./pipeline-preview-meta.component.scss'],
    imports: [
        DefaultLayoutDirective,
        DefaultLayoutGapDirective,
        MatIcon,
        DefaultLayoutAlignDirective,
        SharedUiModule,
        TranslatePipe,
    ],
})
export class PipelinePreviewMetaComponent implements OnInit {
    @Input() lastModifiedAt?: number;

    @Input() status?: boolean;
    @Input() healthStatus?: PipelineHealthStatus;

    @Input() dataInLabel?: string;
    @Input() dataOutLabel?: string;

    statusString: string;
    statusTone: string;

    healthStatusTone: string;

    private translate = inject(TranslateService);

    ngOnInit() {
        if (this.status) {
            this.statusString = this.translate.instant('Running');
            this.statusTone = 'success';
        } else {
            this.statusString = this.translate.instant('Stopped');
            this.statusTone = 'neutral';
        }

        if (this.healthStatus === 'OK') {
            this.healthStatusTone = 'success';
        } else if (this.healthStatus === 'REQUIRES_ATTENTION') {
            this.healthStatusTone = 'warning';
        } else if (this.healthStatus === 'FAILURE') {
            this.healthStatusTone = 'error';
        } else {
            this.healthStatusTone = 'neutral';
        }
    }
}
