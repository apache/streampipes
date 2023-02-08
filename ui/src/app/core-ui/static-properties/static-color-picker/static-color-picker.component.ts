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
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import { StaticPropertyUtilService } from '../static-property-util.service';
import { UntypedFormGroup, Validators } from '@angular/forms';
import { ColorPickerStaticProperty } from '@streampipes/platform-services';
import { AbstractValidatedStaticPropertyRenderer } from '../base/abstract-validated-static-property';

@Component({
    selector: 'sp-app-static-color-picker',
    templateUrl: './static-color-picker.component.html',
    styleUrls: ['./static-color-picker.component.css'],
})
export class StaticColorPickerComponent
    extends AbstractValidatedStaticPropertyRenderer<ColorPickerStaticProperty>
    implements OnInit
{
    constructor(public staticPropertyUtil: StaticPropertyUtilService) {
        super();
    }

    inputValue: String;
    hasInput: Boolean;
    colorPickerForm: UntypedFormGroup;

    presetColors: any[] = [
        '#39B54A',
        '#1B1464',
        '#f44336',
        '#4CAF50',
        '#FFEB3B',
        '#FFFFFF',
        '#000000',
    ];

    ngOnInit() {
        this.addValidator(
            this.staticProperty.selectedColor,
            Validators.required,
        );
        this.enableValidators();
    }

    emitUpdate() {
        this.updateEmitter.emit(
            new ConfigurationInfo(
                this.staticProperty.internalName,
                this.staticPropertyUtil.asColorPickerStaticProperty(
                    this.staticProperty,
                ).selectedColor &&
                    this.staticPropertyUtil.asColorPickerStaticProperty(
                        this.staticProperty,
                    ).selectedColor !== '',
            ),
        );
    }

    onStatusChange(status: any) {}

    onValueChange(value: any) {}
}
