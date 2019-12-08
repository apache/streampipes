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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { MappingPropertyUnary } from '../../model/MappingPropertyUnary';
import { DataSetDescription } from '../../model/DataSetDescription';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { Logger } from '../../../shared/logger/default-log.service';
import { ifError } from 'assert';
import { ValidateNumber } from '../../select-protocol-component/input.validator';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";

@Component({
    selector: 'app-static-number-input',
    templateUrl: './static-number-input.component.html',
    styleUrls: ['./static-number-input.component.css']
})
export class StaticNumberInputComponent implements OnInit {
    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<any> = new EventEmitter<any>();
    @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();



    private freeTextForm: FormGroup;
    private inputValue: String;
    private errorMessage = "Please enter a valid Number";
    private hasInput: Boolean;


    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }

    ngOnInit() {
        this.freeTextForm = new FormGroup({
            'freeStaticTextNumber': new FormControl(this.inputValue, [
                Validators.required,
                ValidateNumber
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;

        if (inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        //NUMBER VALIDATOR
        else if (!isNaN(inputValue)) {
            this.hasInput = true;
        }
        else {
            this.hasInput = false;
        }

        this.inputEmitter.emit(this.hasInput);

    }

    emitUpdate() {
        let valid = (this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value != undefined && this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value !== "");
        this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, valid));
    }

}