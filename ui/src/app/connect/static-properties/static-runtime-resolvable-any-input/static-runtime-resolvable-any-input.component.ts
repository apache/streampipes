/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RuntimeResolvableAnyStaticProperty} from "../../model/RuntimeResolvableAnyStaticProperty";

@Component({
    selector: 'app-static-runtime-resolvable-any-input',
    templateUrl: './static-runtime-resolvable-any-input.component.html',
    styleUrls: ['./static-runtime-resolvable-any-input.component.css']
})
export class StaticRuntimeResolvableAnyInputComponent implements OnInit {

    @Input()
    staticProperty: RuntimeResolvableAnyStaticProperty;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    constructor() { }

    ngOnInit() {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }
    }

    select(id) {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }
        this.staticProperty.options.find(option => option.id === id).selected = true;
        this.inputEmitter.emit(true)
    }

}
