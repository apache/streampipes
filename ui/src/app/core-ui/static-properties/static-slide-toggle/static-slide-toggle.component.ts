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
import { SlideToggleStaticProperty } from '@streampipes/platform-services';
import { AbstractValidatedStaticPropertyRenderer } from '../base/abstract-validated-static-property';
import { Validators } from '@angular/forms';

@Component({
    selector: 'sp-static-slide-toggle',
    templateUrl: './static-slide-toggle.component.html',
})
export class StaticSlideToggleComponent
    extends AbstractValidatedStaticPropertyRenderer<SlideToggleStaticProperty>
    implements OnInit
{
    ngOnInit(): void {
        this.addValidator(this.staticProperty.selected, Validators.required);
        this.enableValidators();
    }

    emitUpdate() {
        this.updateEmitter.emit(
            new ConfigurationInfo(this.staticProperty.internalName, true),
        );
    }

    onStatusChange(status: any) {}

    onValueChange(value: any) {
        this.staticProperty.selected = value;
        this.emitUpdate();
    }
}
