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
import { AnyStaticProperty } from '@streampipes/platform-services';

@Component({
    selector: 'sp-app-static-any-input',
    templateUrl: './static-any-input.component.html',
    styleUrls: ['./static-any-input.component.css'],
})
export class StaticAnyInputComponent
    extends AbstractStaticPropertyRenderer<AnyStaticProperty>
    implements OnInit
{
    @Output() inputEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    ngOnInit() {
        this.inputEmitter.emit(true);
    }

    select(elementId: string) {
        this.staticProperty.options
            .filter(option => option.elementId === elementId)
            .forEach(option => (option.selected = !option.selected));
    }
}
