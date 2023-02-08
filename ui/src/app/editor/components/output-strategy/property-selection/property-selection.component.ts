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
import {
    CustomOutputStrategy,
    EventPropertyNested,
    EventPropertyUnion,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-property-selection',
    templateUrl: './property-selection.component.html',
    styleUrls: ['./property-selection.component.scss'],
})
export class PropertySelectionComponent implements OnInit {
    @Input()
    outputStrategy: CustomOutputStrategy;

    @Input()
    eventProperty: EventPropertyUnion;

    @Input()
    layer: number;

    @Input()
    restrictedEditMode: boolean;

    @Output()
    validateForm: EventEmitter<boolean> = new EventEmitter<boolean>();

    isNestedProperty: boolean;

    ngOnInit() {
        this.isNestedProperty =
            this.eventProperty instanceof EventPropertyNested;
    }

    toggle(runtimeId) {
        if (this.exists(runtimeId)) {
            this.remove(runtimeId);
        } else {
            this.add(runtimeId);
        }
        this.triggerFormValidation();
    }

    exists(runtimeId) {
        return this.outputStrategy.selectedPropertyKeys.some(
            e => e === runtimeId,
        );
    }

    add(runtimeId) {
        this.outputStrategy.selectedPropertyKeys.push(runtimeId);
        // This is needed to trigger update of scope
        this.outputStrategy.selectedPropertyKeys =
            this.outputStrategy.selectedPropertyKeys.filter(el => true);
    }

    remove(runtimeId) {
        this.outputStrategy.selectedPropertyKeys =
            this.outputStrategy.selectedPropertyKeys.filter(
                el => el !== runtimeId,
            );
    }

    triggerFormValidation() {
        this.validateForm.emit(true);
    }
}
