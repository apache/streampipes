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

import { Component, Input, OnInit, inject } from '@angular/core';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
} from '@streampipes/platform-services';
import { TranslateService } from '@ngx-translate/core';
import { DialogRef } from '../base-dialog/dialog-ref';

@Component({
    selector: 'sp-pipeline-element-help',
    templateUrl: './pipeline-element-help.component.html',
    styleUrls: ['./pipeline-element-help.component.scss'],
})
export class PipelineElementHelpComponent implements OnInit {
    selectedTabIndex = 0;

    translateService = inject(TranslateService);

    availableTabs = [
        this.translateService.instant('Preview'),
        this.translateService.instant('Documentation'),
    ];

    tabs: string[] = [];

    @Input()
    pipelineElement:
        | SpDataStream
        | DataProcessorInvocation
        | DataSinkInvocation;

    isDataStream: boolean;

    constructor(private dialogRef: DialogRef<PipelineElementHelpComponent>) {}

    ngOnInit() {
        if (this.pipelineElement instanceof SpDataStream) {
            this.tabs = this.availableTabs;
            this.isDataStream = true;
        } else {
            this.tabs.push(this.availableTabs[1]);
            this.selectedTabIndex = 1;
        }
    }

    close() {
        setTimeout(() => {
            this.dialogRef.close();
        });
    }

    protected readonly SpDataStream = SpDataStream;
}
