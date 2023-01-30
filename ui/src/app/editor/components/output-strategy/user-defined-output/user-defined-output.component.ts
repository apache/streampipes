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
import { BaseOutputStrategy } from '../base/BaseOutputStrategy';
import {
    EventPropertyPrimitive,
    UserDefinedOutputStrategy,
} from '@streampipes/platform-services';
import { UntypedFormControl } from '@angular/forms';

@Component({
    selector: 'sp-user-defined-output-strategy',
    templateUrl: './user-defined-output.component.html',
    styleUrls: ['./user-defined-output.component.scss'],
})
export class UserDefinedOutputStrategyComponent
    extends BaseOutputStrategy<UserDefinedOutputStrategy>
    implements OnInit
{
    private prefix = 'urn:streampipes.org:spi:';
    private chars =
        '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';

    collectedPropertiesFirstStream: any;
    collectedPropertiesSecondStream: any;

    primitiveClasses = [
        { label: 'String', id: 'http://www.w3.org/2001/XMLSchema#string' },
        { label: 'Boolean', id: 'http://www.w3.org/2001/XMLSchema#boolean' },
        { label: 'Integer', id: 'http://www.w3.org/2001/XMLSchema#integer' },
        { label: 'Long', id: 'http://www.w3.org/2001/XMLSchema#long' },
        { label: 'Double', id: 'http://www.w3.org/2001/XMLSchema#double' },
        { label: 'Float', id: 'http://www.w3.org/2001/XMLSchema#float' },
    ];

    constructor() {
        super();
    }

    ngOnInit() {
        this.parentForm.addControl('output-strategy', new UntypedFormControl());
        if (!this.outputStrategy.eventProperties) {
            this.outputStrategy.eventProperties = [];
        }
        this.checkFormValidity();
    }

    applyDefaultSchema() {
        this.outputStrategy.eventProperties = [
            ...this.selectedElement.inputStreams[0].eventSchema.eventProperties,
        ];
        this.checkFormValidity();
    }

    removeProperty(ep: any) {
        this.outputStrategy.eventProperties.splice(
            this.outputStrategy.eventProperties.indexOf(ep),
            1,
        );
        this.checkFormValidity();
    }

    addProperty() {
        this.outputStrategy.eventProperties.push(this.makeDefaultProperty());
        this.checkFormValidity();
    }

    makeDefaultProperty() {
        const ep = {} as EventPropertyPrimitive;
        ep['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyPrimitive';
        ep.domainProperties = [];
        ep.elementId =
            'urn:streampipes.org:spi:eventpropertyprimitive:' + this.makeId();

        return ep;
    }

    makeId() {
        return this.prefix + this.randomString(6);
    }

    randomString(length) {
        let result = '';
        for (let i = length; i > 0; --i) {
            result += this.chars[Math.floor(Math.random() * this.chars.length)];
        }
        return result;
    }

    checkFormValidity() {
        if (
            !this.outputStrategy.eventProperties ||
            this.outputStrategy.eventProperties.length === 0
        ) {
            this.parentForm.controls['output-strategy'].setErrors({});
        } else {
            this.parentForm.controls['output-strategy'].setErrors(undefined);
        }
    }
}
