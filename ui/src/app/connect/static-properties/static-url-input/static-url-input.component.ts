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
import {ValidateUrl} from '../../select-protocol-component/input.validator';
import {StaticPropertyUtilService} from '../static-property-util.service';

@Component({
    selector: 'app-static-url-input',
    templateUrl: './static-url-input.component.html',
    styleUrls: ['./static-url-input.component.css']
})
export class StaticUrlInputComponent implements OnInit {

    constructor(public staticPropertyUtil: StaticPropertyUtilService){

    }

    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    
    freeTextForm: FormGroup;
    private inputValue: String;
    hasInput: Boolean;
    private errorMessage = "Please enter a valid Url";
    ngOnInit() {
        this.freeTextForm = new FormGroup({
            'freeStaticTextUrl':new FormControl(this.inputValue, [
                Validators.required,
                ValidateUrl
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        if(inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        else if(inputValue.match(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g) != null) {
            this.hasInput = true;
        }
        else{
            this.hasInput = false;
        }

        this.inputEmitter.emit(this.hasInput);
    }

}