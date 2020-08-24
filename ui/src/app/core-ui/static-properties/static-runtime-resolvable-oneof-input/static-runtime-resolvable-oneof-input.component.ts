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

import {Component, OnChanges, OnInit} from '@angular/core';
import {RuntimeResolvableOneOfStaticProperty} from "../../../core-model/gen/streampipes-model";
import {BaseRuntimeResolvableInput} from "../static-runtime-resolvable-input/base-runtime-resolvable-input";
import {RuntimeResolvableService} from "../static-runtime-resolvable-input/runtime-resolvable.service";

@Component({
    selector: 'app-static-runtime-resolvable-oneof-input',
    templateUrl: './static-runtime-resolvable-oneof-input.component.html',
    styleUrls: ['./static-runtime-resolvable-oneof-input.component.css']
})
export class StaticRuntimeResolvableOneOfInputComponent
    extends BaseRuntimeResolvableInput<RuntimeResolvableOneOfStaticProperty> implements OnInit, OnChanges {

    constructor(RuntimeResolvableService: RuntimeResolvableService) {
        super(RuntimeResolvableService);
    }

    ngOnInit() {
        super.onInit();
    }

    afterOptionsLoaded() {
        if (this.staticProperty.options && this.staticProperty.options.length > 0) {
            this.staticProperty.options[0].selected = true;
        }
    }

    select(id) {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }
        this.staticProperty.options.find(option => option.elementId === id).selected = true;
    }
}
