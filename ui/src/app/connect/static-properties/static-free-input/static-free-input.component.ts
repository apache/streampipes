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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {StaticProperty} from '../../model/StaticProperty';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";
import {AbstractStaticPropertyRenderer} from "../base/abstract-static-property";
import {FreeTextStaticProperty} from "../../../core-model/gen/streampipes-model";


@Component({
    selector: 'app-static-free-input',
    templateUrl: './static-free-input.component.html',
    styleUrls: ['./static-free-input.component.css']
})
export class StaticFreeInputComponent
    extends AbstractStaticPropertyRenderer<FreeTextStaticProperty> implements OnInit {

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();
    
    freeTextForm: FormGroup;
    inputValue: String;
    hasInput: Boolean;
    errorMessage = "Please enter a value";

    constructor(public staticPropertyUtil: StaticPropertyUtilService){
        super();
    }


    ngOnInit() {
        this.freeTextForm = new FormGroup({
            'freeStaticText':new FormControl(this.inputValue, [
                Validators.required,
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        if(inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        else{
            this.hasInput = true;
        }

        this.inputEmitter.emit(this.hasInput);
    }

    emitUpdate() {
        let valid = (this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value != undefined && this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value !== "");
        this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, valid));
    }
}