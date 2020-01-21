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
import { FormControl, Validators, FormGroup } from '@angular/forms';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {EventSchema} from '../../schema-editor/model/EventSchema';
import {PropertySelectorService} from "../../../services/property-selector.service";
import {MappingPropertyUnary} from "../../model/MappingPropertyUnary";
import {EventProperty} from "../../schema-editor/model/EventProperty";


@Component({
    selector: 'app-static-mapping-unary',
    templateUrl: './static-mapping-unary.component.html',
    styleUrls: ['./static-mapping-unary.component.css']
})
export class StaticMappingUnaryComponent implements OnInit {


    @Input() staticProperty: MappingPropertyUnary;
    @Input() eventSchema: EventSchema;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    
    private unaryTextForm: FormGroup;
    private inputValue: String;
    private hasInput: Boolean;
    private errorMessage = "Please enter a value";
    private availableProperties: Array<EventProperty>;

    constructor(private staticPropertyUtil: StaticPropertyUtilService,
                private PropertySelectorService: PropertySelectorService){

    }


    ngOnInit() {
        this.availableProperties = this.extractPossibleSelections();
        this.unaryTextForm = new FormGroup({
            'unaryStaticText':new FormControl(this.inputValue, [
                Validators.required,
            ]),
        })
    }

    extractPossibleSelections(): Array<EventProperty> {
        return this.eventSchema.eventProperties.filter(ep => this.isInSelection(ep));
    }

    isInSelection(ep: EventProperty): boolean {
        return this.staticProperty.mapsFromOptions.some(maps => maps === "s0::" + ep.runtimeName);
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

    getName(eventProperty) {
    return eventProperty.label
      ? eventProperty.label
      : eventProperty.runTimeName;
  }

}