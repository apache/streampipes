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
    Option,
    RuntimeResolvableOneOfStaticProperty,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { BaseRuntimeResolvableSelectionInput } from '../static-runtime-resolvable-input/base-runtime-resolvable-selection-input';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormControl,
} from '@angular/forms';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { SpExceptionMessageComponent } from '@streampipes/shared-ui';
import { MatRadioButton } from '@angular/material/radio';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-app-static-runtime-resolvable-oneof-input',
    templateUrl: './static-runtime-resolvable-oneof-input.component.html',
    styleUrls: ['./static-runtime-resolvable-oneof-input.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        FormsModule,
        ReactiveFormsModule,
        MatButton,
        SpExceptionMessageComponent,
        MatRadioButton,
        MatProgressSpinner,
        TranslatePipe,
    ],
})
export class StaticRuntimeResolvableOneOfInputComponent
    extends BaseRuntimeResolvableSelectionInput<RuntimeResolvableOneOfStaticProperty>
    implements OnInit, OnChanges
{
    ngOnInit() {
        super.onInit();
        this.parentForm.addControl(
            this.staticProperty.internalName,
            new UntypedFormControl(this.staticProperty.options, []),
        );
        this.performValidation();
    }

    afterOptionsLoaded(staticProperty: RuntimeResolvableOneOfStaticProperty) {
        if (
            this.staticProperty.options?.length > 0 &&
            this.isOptionSelected()
        ) {
            const selectedOption = this.staticProperty.options.find(
                o => o.selected,
            );
            this.addSelectedOption(staticProperty, selectedOption);
        } else {
            if (staticProperty.options?.length > 0) {
                staticProperty.options[0].selected = true;
            }
        }
        this.staticProperty.options = staticProperty.options;
    }

    isOptionSelected(): boolean {
        return this.staticProperty.options.find(o => o.selected) !== undefined;
    }

    addSelectedOption(
        staticProperty: RuntimeResolvableOneOfStaticProperty,
        selectedOption: Option,
    ): void {
        staticProperty.options
            .filter(o => {
                return o.internalName !== null
                    ? o.internalName === selectedOption.internalName
                    : o.name === selectedOption.name;
            })
            .forEach(o => {
                o.selected = true;
            });
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
