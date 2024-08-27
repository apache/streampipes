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
import { SpDataStream } from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { PipelineElementUnion } from '../../editor/model/editor.model';

@Component({
    selector: 'sp-pipeline-element-help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss'],
})
export class HelpComponent implements OnInit {
    selectedTabIndex = 0;

    availableTabs = ['Values', 'Documentation'];
    tabs: string[] = [];

    @Input()
    pipelineElement: PipelineElementUnion;

    constructor(private dialogRef: DialogRef<HelpComponent>) {}

    ngOnInit() {
        if (this.pipelineElement instanceof SpDataStream) {
            this.tabs = this.availableTabs;
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
}
