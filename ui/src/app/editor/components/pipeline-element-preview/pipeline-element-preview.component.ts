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
    Component,
    Input,
    OnDestroy,
    OnInit,
    inject,
    ChangeDetectionStrategy,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { KeyValue, KeyValuePipe } from '@angular/common';
import {
    LivePreviewService,
    PipelinePreviewModel,
} from '@streampipes/platform-services';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';
import { SpSpinnerComponent } from '@streampipes/shared-ui';

const NO_DATA_HINT_DELAY_MS = 10000;

@Component({
    selector: 'sp-pipeline-element-preview',
    templateUrl: './pipeline-element-preview.component.html',
    styleUrls: ['./pipeline-element-preview.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        SpSpinnerComponent,
        KeyValuePipe,
        TranslatePipe,
    ],
})
export class PipelineElementPreviewComponent implements OnInit, OnDestroy {
    private livePreviewService = inject(LivePreviewService);

    @Input()
    pipelinePreview: PipelinePreviewModel;

    @Input()
    elementId: string;

    runtimeData: Record<string, any>;
    errorMessage: string;
    noDataHintVisible = false;
    previewSub: Subscription;

    private noDataHintTimer: ReturnType<typeof setTimeout>;

    ngOnInit(): void {
        this.errorMessage =
            this.pipelinePreview.elementErrors?.[this.elementId];
        if (!this.errorMessage) {
            this.noDataHintTimer = setTimeout(
                () => (this.noDataHintVisible = true),
                NO_DATA_HINT_DELAY_MS,
            );
            this.getLatestRuntimeInfo();
        }
    }

    keyValueCompareFn = (
        a: KeyValue<string, any>,
        b: KeyValue<string, any>,
    ): number => {
        return a.key.localeCompare(b.key);
    };

    getLatestRuntimeInfo() {
        this.previewSub = this.livePreviewService.eventSub.subscribe(event => {
            const data =
                event?.[this.pipelinePreview.elementIdMappings[this.elementId]];
            if (data) {
                this.runtimeData = data;
                clearTimeout(this.noDataHintTimer);
            }
        });
    }

    ngOnDestroy() {
        clearTimeout(this.noDataHintTimer);
        this.previewSub?.unsubscribe();
    }
}
