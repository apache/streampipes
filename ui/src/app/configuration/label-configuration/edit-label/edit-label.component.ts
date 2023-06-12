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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SpLabel } from '@streampipes/platform-services';

@Component({
    selector: 'sp-edit-label',
    templateUrl: './edit-label.component.html',
    styleUrls: ['./edit-label.component.scss'],
})
export class SpEditLabelComponent implements OnInit {
    @Input()
    editMode = false;

    @Input()
    label: SpLabel;

    @Input()
    showPreview = true;

    @Output()
    cancelEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    saveEmitter: EventEmitter<SpLabel> = new EventEmitter<SpLabel>();

    ngOnInit(): void {
        if (!this.label) {
            this.label = {
                color: '#00a7fc',
                label: '',
                description: '',
            };
        }
    }
}
