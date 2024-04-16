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
import { DataType } from '@streampipes/platform-services';

@Component({
    selector: 'sp-edit-data-type',
    templateUrl: './edit-data-type.component.html',
    styleUrls: ['./edit-data-type.component.scss'],
})
export class EditDataTypeComponent {
    @Input() cachedProperty: any;
    @Output() dataTypeChanged = new EventEmitter<boolean>();

    runtimeDataTypes: { label: string; url: string }[] = [
        {
            label: "String - A textual datatype, e.g., 'machine1'",
            url: DataType.STRING,
        },
        {
            label: 'Boolean - A true/false value',
            url: DataType.BOOLEAN,
        },
        {
            label: "Double - A number, e.g., '1.25'",
            url: DataType.DOUBLE,
        },
        {
            label: "Float - A number, e.g., '1.25'",
            url: DataType.FLOAT,
        },
        {
            label: "Integer - A number, e.g., '2'",
            url: DataType.INTEGER,
        },
        {
            label: "Long - A number, e.g., '1623871455232'",
            url: DataType.LONG,
        },
    ];

    valueChanged() {
        this.dataTypeChanged.emit(true);
    }
}
