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

import '@angular/compiler';
import { Injector, runInInjectionContext } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import {
    Option,
    RuntimeResolvableOneOfStaticProperty,
} from '@streampipes/platform-services';
import { beforeEach, describe, expect, it } from 'vitest';
import { StaticPropertyUtilService } from '../static-property-util.service';
import { RuntimeResolvableService } from '../static-runtime-resolvable-input/runtime-resolvable.service';
import { StaticRuntimeResolvableOneOfInputComponent } from './static-runtime-resolvable-oneof-input.component';

describe('StaticRuntimeResolvableOneOfInputComponent', () => {
    let component: StaticRuntimeResolvableOneOfInputComponent;

    beforeEach(() => {
        const injector = Injector.create({
            providers: [
                { provide: RuntimeResolvableService, useValue: {} },
                { provide: StaticPropertyUtilService, useValue: {} },
            ],
        });
        component = runInInjectionContext(
            injector,
            () => new StaticRuntimeResolvableOneOfInputComponent(),
        );
    });

    it('marks a selected runtime option as configured', () => {
        const option = new Option();
        option.selected = true;
        const staticProperty = new RuntimeResolvableOneOfStaticProperty();
        staticProperty.internalName = 'table';
        staticProperty.options = [option];
        component.staticProperty = staticProperty;
        component.parentForm = new UntypedFormGroup({});
        const completedConfigurations = [];
        component.completedConfigurationsEmitter.subscribe(configuration =>
            completedConfigurations.push(configuration),
        );

        component.ngOnInit();

        expect(completedConfigurations).toContainEqual({
            staticPropertyInternalName: 'table',
            configured: true,
        });
    });
});
