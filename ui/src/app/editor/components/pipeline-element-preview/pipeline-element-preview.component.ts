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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { EditorService } from '../../services/editor.service';
import { Subscription } from 'rxjs';
import { HttpDownloadProgressEvent, HttpEventType } from '@angular/common/http';
import { KeyValue } from '@angular/common';

@Component({
    selector: 'sp-pipeline-element-preview',
    templateUrl: './pipeline-element-preview.component.html',
    styleUrls: ['./pipeline-element-preview.component.scss'],
})
export class PipelineElementPreviewComponent implements OnInit, OnDestroy {
    @Input()
    previewId: string;

    @Input()
    pipelineElementDomId: string;

    runtimeData: Record<string, any>;

    runtimeDataError = false;
    timer: any;
    previewSub: Subscription;

    constructor(private editorService: EditorService) {}

    ngOnInit(): void {
        this.getLatestRuntimeInfo();
    }

    keyValueCompareFn = (
        a: KeyValue<string, any>,
        b: KeyValue<string, any>,
    ): number => {
        return a.key.localeCompare(b.key);
    };

    getLatestRuntimeInfo() {
        this.previewSub = this.editorService
            .getPipelinePreviewResult(this.previewId, this.pipelineElementDomId)
            .subscribe(event => {
                if (event) {
                    if (event.type === HttpEventType.DownloadProgress) {
                        const chunks = (
                            event as HttpDownloadProgressEvent
                        ).partialText.split('\n');
                        this.runtimeData = JSON.parse(
                            chunks[chunks.length - 2],
                        );
                    }
                } else {
                    this.runtimeDataError = true;
                }
            });
    }

    ngOnDestroy() {
        this.previewSub?.unsubscribe();
    }
}
