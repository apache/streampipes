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
    DataType,
    EventPropertyPrimitive,
    UserDefinedOutputStrategy,
} from '@streampipes/platform-services';
import { UntypedFormControl } from '@angular/forms';
import { IdGeneratorService } from '../../../../core-services/id-generator/id-generator.service';

@Component({
    selector: 'sp-user-defined-output-strategy',
    templateUrl: './user-defined-output.component.html',
    styleUrls: ['./user-defined-output.component.scss'],
})
export class UserDefinedOutputStrategyComponent
    extends BaseOutputStrategy<UserDefinedOutputStrategy>
    implements OnInit
{
    primitiveClasses = [];

    constructor(private idGeneratorService: IdGeneratorService) {
        super();
        this.primitiveClasses = [
            { label: 'String', id: DataType.STRING },
            { label: 'Boolean', id: DataType.BOOLEAN },
            { label: 'Integer', id: DataType.INTEGER },
            { label: 'Long', id: DataType.LONG },
            { label: 'Double', id: DataType.DOUBLE },
            { label: 'Float', id: DataType.FLOAT },
        ];
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
        ep.semanticType = undefined;
        ep.elementId =
            'urn:streampipes.org:spi:eventpropertyprimitive:' +
            this.idGeneratorService.generatePrefixedId();

        return ep;
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
