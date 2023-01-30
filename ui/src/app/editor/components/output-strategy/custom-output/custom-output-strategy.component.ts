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
import { CustomOutputStrategy } from '@streampipes/platform-services';
import { BaseOutputStrategy } from '../base/BaseOutputStrategy';
import { PropertySelectorService } from '../../../../services/property-selector.service';
import { UntypedFormControl } from '@angular/forms';

@Component({
    selector: 'sp-custom-output-strategy',
    templateUrl: './custom-output-strategy.component.html',
    styleUrls: ['./custom-output-strategy.component.scss'],
})
export class CustomOutputStrategyComponent
    extends BaseOutputStrategy<CustomOutputStrategy>
    implements OnInit
{
    collectedPropertiesFirstStream: any;
    collectedPropertiesSecondStream: any;

    constructor(private propertySelectorService: PropertySelectorService) {
        super();
    }

    ngOnInit() {
        this.parentForm.addControl('output-strategy', new UntypedFormControl());
        this.collectedPropertiesFirstStream =
            this.propertySelectorService.makeProperties(
                this.getProperties(0),
                this.outputStrategy.availablePropertyKeys,
                this.propertySelectorService.firstStreamPrefix,
            );
        this.collectedPropertiesSecondStream =
            this.propertySelectorService.makeProperties(
                this.getProperties(1),
                this.outputStrategy.availablePropertyKeys,
                this.propertySelectorService.secondStreamPrefix,
            );
        this.checkFormValidity();
    }

    getProperties(streamIndex) {
        return this.selectedElement.inputStreams[streamIndex] === undefined
            ? []
            : this.selectedElement.inputStreams[streamIndex].eventSchema
                  .eventProperties;
    }

    selectAll(collectedProperties) {
        collectedProperties.forEach(ep =>
            this.outputStrategy.selectedPropertyKeys.push(ep.runtimeId),
        );
        // This is needed to trigger update of scope
        this.outputStrategy.selectedPropertyKeys =
            this.outputStrategy.selectedPropertyKeys.filter(el => true);
        this.checkFormValidity();
    }

    deselectAll(collectedProperties) {
        collectedProperties.forEach(
            ep =>
                (this.outputStrategy.selectedPropertyKeys =
                    this.outputStrategy.selectedPropertyKeys.filter(
                        item => item !== ep.runtimeId,
                    )),
        );
        this.checkFormValidity();
    }

    checkFormValidity() {
        if (
            !this.outputStrategy.selectedPropertyKeys ||
            this.outputStrategy.selectedPropertyKeys.length === 0
        ) {
            this.parentForm.controls['output-strategy'].setErrors({});
        } else {
            this.parentForm.controls['output-strategy'].setErrors(undefined);
        }
    }
}
