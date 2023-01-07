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
import { DataTypesService } from '../../../../../services/data-type.service';

@Component({
    selector: 'sp-edit-data-type',
    templateUrl: './edit-data-type.component.html',
    styleUrls: ['./edit-data-type.component.scss'],
})
export class EditDataTypeComponent implements OnInit {
    @Input() cachedProperty: any;
    @Output() dataTypeChanged = new EventEmitter<boolean>();

    runtimeDataTypes;
    constructor(private dataTypeService: DataTypesService) {}

    ngOnInit() {
        this.runtimeDataTypes = this.dataTypeService.getDataTypes();
    }

    valueChanged() {
        this.dataTypeChanged.emit(true);
    }
}
