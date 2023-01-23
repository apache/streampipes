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
import { AbstractStaticPropertyRenderer } from '../base/abstract-static-property';
import { OneOfStaticProperty } from '@streampipes/platform-services';

@Component({
    selector: 'sp-static-one-of-input',
    templateUrl: './static-one-of-input.component.html',
    styleUrls: ['./static-one-of-input.component.css'],
})
export class StaticOneOfInputComponent
    extends AbstractStaticPropertyRenderer<OneOfStaticProperty>
    implements OnInit
{
    @Output() inputEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    selectedOption: string;

    constructor() {
        super();
    }

    ngOnInit() {
        if (this.noneSelected()) {
            if (this.staticProperty.options.length > 0) {
                this.staticProperty.options[0].selected = true;
                this.selectedOption = this.staticProperty.options[0].elementId;
            }
        } else {
            this.selectedOption = this.staticProperty.options.find(
                option => option.selected,
            ).elementId;
        }
        this.inputEmitter.emit(true);
        this.parentForm.updateValueAndValidity();
    }

    noneSelected(): boolean {
        return this.staticProperty.options.every(o => !o.selected);
    }

    select(id) {
        this.selectedOption = this.staticProperty.options.find(
            option => option.elementId === id,
        ).elementId;
        for (const option of this.staticProperty.options) {
            option.selected = false;
        }
        this.staticProperty.options.find(
            option => option.elementId === id,
        ).selected = true;
        this.inputEmitter.emit(true);
    }
}
