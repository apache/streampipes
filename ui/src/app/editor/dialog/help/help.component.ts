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
import { PipelineElementUnion } from '../../model/editor.model';
import {
    PipelineElementService,
    SpDataStream,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { PipelineElementTypeUtils } from '../../utils/editor.utils';

@Component({
    selector: 'sp-pipeline-element-help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss'],
})
export class HelpComponent implements OnInit {
    selectedTab = 0;
    pollingActive: boolean;

    selectedIndex = 0;

    nsPrefix = 'http://www.w3.org/2001/XMLSchema#';
    availableTabs = [
        {
            title: 'Fields',
            type: 'fields',
            targets: ['set', 'stream'],
        },
        {
            title: 'Values',
            type: 'values',
            targets: ['set', 'stream'],
        },
        {
            title: 'Documentation',
            type: 'documentation',
            targets: ['set', 'stream', 'sepa', 'action'],
        },
    ];

    tabs: any[] = [];
    streamMode: boolean;

    @Input()
    pipelineElement: PipelineElementUnion;

    constructor(
        private pipelineElementService: PipelineElementService,
        private dialogRef: DialogRef<HelpComponent>,
    ) {
        this.pollingActive = true;
    }

    ngOnInit() {
        if (this.pipelineElement instanceof SpDataStream) {
            this.tabs = this.availableTabs;
            this.streamMode = true;
        } else {
            this.tabs.push(this.availableTabs[2]);
            this.streamMode = false;
        }
    }

    getFriendlyRuntimeType(runtimeType) {
        if (this.isNumber(runtimeType)) {
            return 'Number';
        } else if (this.isBoolean(runtimeType)) {
            return 'Boolean';
        } else {
            return 'Text';
        }
    }

    isNumber(runtimeType) {
        return (
            runtimeType === this.nsPrefix + 'float' ||
            runtimeType === this.nsPrefix + 'integer' ||
            runtimeType === this.nsPrefix + 'long' ||
            runtimeType === this.nsPrefix + 'double'
        );
    }

    isBoolean(runtimeType) {
        return runtimeType === this.nsPrefix + 'boolean';
    }

    close() {
        this.pollingActive = false;
        setTimeout(() => {
            this.dialogRef.close();
        });
    }

    filterTab(tab) {
        const type = PipelineElementTypeUtils.fromType(this.pipelineElement);
        const cssShortHand = PipelineElementTypeUtils.toCssShortHand(type);
        return tab.targets.indexOf(cssShortHand) !== -1;
    }
}
