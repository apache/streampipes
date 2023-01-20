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

import { Component, Input, OnInit } from '@angular/core';
import { EditorService } from '../../services/editor.service';

@Component({
    selector: 'sp-pipeline-element-preview',
    templateUrl: './pipeline-element-preview.component.html',
    styleUrls: ['./pipeline-element-preview.component.scss'],
})
export class PipelineElementPreviewComponent implements OnInit {
    @Input()
    previewId: string;

    @Input()
    pipelineElementDomId: string;

    runtimeData: ReadonlyMap<string, unknown>;

    runtimeDataError = false;
    timer: any;

    constructor(private editorService: EditorService) {}

    ngOnInit(): void {
        this.getLatestRuntimeInfo();
    }

    getLatestRuntimeInfo() {
        this.editorService
            .getPipelinePreviewResult(this.previewId, this.pipelineElementDomId)
            .subscribe(data => {
                if (data) {
                    this.runtimeDataError = false;
                    if (
                        !(
                            Object.keys(data).length === 0 &&
                            data.constructor === Object
                        )
                    ) {
                        this.runtimeData = data;
                    }

                    this.timer = setTimeout(() => {
                        this.getLatestRuntimeInfo();
                    }, 1000);
                } else {
                    this.runtimeDataError = true;
                }
            });
    }
}
