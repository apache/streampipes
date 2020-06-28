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

import {Component, OnInit} from '@angular/core';
import {ValidatorFn, Validators} from '@angular/forms';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";
import {FreeTextStaticProperty} from "../../../core-model/gen/streampipes-model";
import {xsService} from "../../../NS/XS.service";
import {
    ValidateNumber,
    ValidateString,
    ValidateUrl
} from "../input.validator";
import {AbstractValidatedStaticPropertyRenderer} from "../base/abstract-validated-static-property";


@Component({
    selector: 'app-static-free-input',
    templateUrl: './static-free-input.component.html',
    styleUrls: ['./static-free-input.component.css']
})
export class StaticFreeInputComponent
    extends AbstractValidatedStaticPropertyRenderer<FreeTextStaticProperty> implements OnInit {


    constructor(public staticPropertyUtil: StaticPropertyUtilService,
                private xsService: xsService){
        super();
    }


    ngOnInit() {
        this.addValidator(this.staticProperty.value, this.collectValidators());
        this.enableValidators();
    }

    collectValidators() {
        let validators: ValidatorFn[] = [];
        validators.push(Validators.required);
        if (this.xsService.isNumber(this.staticProperty.requiredDatatype) ||
            this.xsService.isNumber(this.staticProperty.requiredDomainProperty)) {
            validators.push(ValidateNumber);
            this.errorMessage = "The value should be a number";
        } else if (this.staticProperty.requiredDomainProperty === this.xsService.SO_URL) {
            validators.push(ValidateUrl);
            this.errorMessage = "Please enter a valid URL";
        } else if (this.staticProperty.requiredDatatype === this.xsService.XS_STRING1) {
            validators.push(ValidateString);
            this.errorMessage = "Please enter a valid String";
        }

        return validators;
    }

    emitUpdate() {
        let valid = (this.staticProperty.value != undefined && this.staticProperty.value !== "");
        this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, valid));
    }

    onStatusChange(status: any) {

    }

    onValueChange(value: any) {
        this.staticProperty.value = value;
    }
}