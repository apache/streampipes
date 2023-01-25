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
import { EventPropertyUnion } from '@streampipes/platform-services';

@Component({
    selector: 'sp-group-configuration',
    templateUrl: './group-configuration.component.html',
    styleUrls: ['./group-configuration.component.css'],
})
export class GroupConfigurationComponent implements OnInit {
    groupingAvailable = true;

    @Input()
    groupValue;

    @Output()
    groupValueChange = new EventEmitter();

    showCountValueCheckbox = false;

    @Input()
    showCountValue = false;

    @Output()
    showCountValueChange = new EventEmitter();

    @Input()
    dimensionProperties: EventPropertyUnion[];

    constructor() {}

    ngOnInit(): void {
        if (this.dimensionProperties.length === 0) {
            this.groupingAvailable = false;
        }
    }

    onModelChange(event, type) {
        if (type === 'groupValue') {
            if (this.groupValue !== 'None') {
                this.showCountValueCheckbox = true;
            } else {
                this.showCountValueCheckbox = false;
                this.showCountValue = false;
            }
        }
        this[`${type}Change`].emit(event);
    }
}
