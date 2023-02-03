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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { StaticMappingComponent } from '../static-mapping/static-mapping';
import { MappingPropertyUnary } from '@streampipes/platform-services';

@Component({
    selector: 'sp-app-static-mapping-unary',
    templateUrl: './static-mapping-unary.component.html',
    styleUrls: ['./static-mapping-unary.component.css'],
})
export class StaticMappingUnaryComponent
    extends StaticMappingComponent<MappingPropertyUnary>
    implements OnInit
{
    @Output() inputEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor() {
        super();
    }

    ngOnInit() {
        this.extractPossibleSelections();
        if (!this.staticProperty.selectedProperty) {
            this.staticProperty.selectedProperty =
                this.availableProperties[0].propertySelector;
            this.emitUpdate(true);
        }
        this.addValidator(
            this.staticProperty.selectedProperty,
            Validators.required,
        );
        this.enableValidators();
    }

    onStatusChange(status: any) {}

    onValueChange(value: any) {
        console.log(value);
        this.staticProperty.selectedProperty = value;
        this.emitUpdate(true);
    }
}
