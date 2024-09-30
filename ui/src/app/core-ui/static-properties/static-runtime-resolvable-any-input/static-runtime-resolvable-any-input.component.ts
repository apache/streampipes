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

import { Component, OnInit } from '@angular/core';
import {
    Option,
    RuntimeResolvableAnyStaticProperty,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { RuntimeResolvableService } from '../static-runtime-resolvable-input/runtime-resolvable.service';
import { BaseRuntimeResolvableSelectionInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-selection-input';
import { MatSelectChange } from '@angular/material/select';

@Component({
    selector: 'sp-app-static-runtime-resolvable-any-input',
    templateUrl: './static-runtime-resolvable-any-input.component.html',
    styleUrls: ['./static-runtime-resolvable-any-input.component.scss'],
})
export class StaticRuntimeResolvableAnyInputComponent
    extends BaseRuntimeResolvableSelectionInput<RuntimeResolvableAnyStaticProperty>
    implements OnInit
{
    selectedOptions: Option[] = [];

    constructor(runtimeResolvableService: RuntimeResolvableService) {
        super(runtimeResolvableService);
    }

    ngOnInit() {
        super.onInit();
        this.selectedOptions = this.staticProperty.options.filter(
            o => o.selected,
        );
    }

    selectAll(select: boolean): void {
        this.staticProperty.options.forEach(o => (o.selected = select));
        this.selectedOptions = select ? this.staticProperty.options : [];
        this.emitUpdate(true);
    }

    onSelectionChange(): void {
        this.staticProperty.options.forEach(option => {
            option.selected = this.selectedOptions.includes(option);
        });
    }

    checkEmitUpdate(): void {
        this.emitUpdate(true);
    }

    afterOptionsLoaded(staticProperty: RuntimeResolvableAnyStaticProperty) {
        this.staticProperty.options = staticProperty.options;
        this.selectedOptions = this.staticProperty.options.filter(
            o => o.selected,
        );
    }

    parse(
        staticProperty: StaticPropertyUnion,
    ): RuntimeResolvableAnyStaticProperty {
        return staticProperty as RuntimeResolvableAnyStaticProperty;
    }

    afterErrorReceived() {}
}
