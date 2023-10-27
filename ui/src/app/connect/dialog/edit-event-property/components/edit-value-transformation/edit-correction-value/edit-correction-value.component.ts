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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EventPropertyUnion } from '@streampipes/platform-services';

@Component({
    selector: 'sp-edit-correction-value',
    templateUrl: './edit-correction-value.component.html',
    styleUrls: ['./edit-correction-value.component.scss'],
})
export class EditCorrectionValueComponent {
    @Input() cachedProperty: EventPropertyUnion;

    @Output() correctionValueChanged = new EventEmitter<boolean>();

    operators = [
        { value: 'MULTIPLY', viewValue: 'Multiply' },
        { value: 'ADD', viewValue: 'Add' },
        { value: 'SUBTRACT', viewValue: 'Subtract' },
        { value: 'DIVIDE', viewValue: 'Divide' },
    ];

    constructor() {}

    valueChanged() {
        if (this.cachedProperty.additionalMetadata.correctionValue) {
            if (!this.cachedProperty.additionalMetadata.operator) {
                this.correctionValueChanged.emit(true);
            } else {
                this.correctionValueChanged.emit(false);
            }
        } else {
            this.correctionValueChanged.emit(false);
            this.cachedProperty.additionalMetadata.operator = undefined;
        }
    }
}
