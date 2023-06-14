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

import { Component, OnChanges, OnInit } from '@angular/core';
import {
    RuntimeResolvableOneOfStaticProperty,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { RuntimeResolvableService } from '../static-runtime-resolvable-input/runtime-resolvable.service';
import { BaseRuntimeResolvableSelectionInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-selection-input';
import { UntypedFormControl } from '@angular/forms';

@Component({
    selector: 'sp-app-static-runtime-resolvable-oneof-input',
    templateUrl: './static-runtime-resolvable-oneof-input.component.html',
    styleUrls: ['./static-runtime-resolvable-oneof-input.component.css'],
})
export class StaticRuntimeResolvableOneOfInputComponent
    extends BaseRuntimeResolvableSelectionInput<RuntimeResolvableOneOfStaticProperty>
    implements OnInit, OnChanges
{
    constructor(runtimeResolvableService: RuntimeResolvableService) {
        super(runtimeResolvableService);
    }

    ngOnInit() {
        super.onInit();
        this.parentForm.addControl(
            this.staticProperty.internalName,
            new UntypedFormControl(this.staticProperty.options, []),
        );
        this.performValidation();
    }

    afterOptionsLoaded(staticProperty: RuntimeResolvableOneOfStaticProperty) {
        this.staticProperty.options = staticProperty.options;
        if (
            this.staticProperty.options &&
            this.staticProperty.options.length > 0
        ) {
            this.staticProperty.options[0].selected = true;
        }
    }

    select(id) {
        for (const option of this.staticProperty.options) {
            option.selected = false;
        }
        this.staticProperty.options.find(
            option => option.elementId === id,
        ).selected = true;
        this.performValidation();
    }

    parse(
        staticProperty: StaticPropertyUnion,
    ): RuntimeResolvableOneOfStaticProperty {
        return staticProperty as RuntimeResolvableOneOfStaticProperty;
    }

    afterErrorReceived() {
        this.staticProperty.options = [];
        this.performValidation();
    }

    performValidation() {
        let error = { error: true };
        if (
            this.staticProperty.options &&
            this.staticProperty.options.find(o => o.selected) !== undefined
        ) {
            error = undefined;
        }
        this.parentForm.controls[this.staticProperty.internalName].setErrors(
            error,
        );
    }
}
