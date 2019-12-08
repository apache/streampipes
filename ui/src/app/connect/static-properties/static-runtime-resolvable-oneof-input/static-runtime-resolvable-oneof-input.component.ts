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

import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {RuntimeResolvableOneOfStaticProperty} from "../../model/RuntimeResolvableOneOfStaticProperty";
import {StaticProperty} from "../../model/StaticProperty";
import {RestService} from "../../rest.service";
import {RuntimeOptionsRequest} from "../../model/connect/runtime/RuntimeOptionsRequest";
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";

@Component({
    selector: 'app-static-runtime-resolvable-oneof-input',
    templateUrl: './static-runtime-resolvable-oneof-input.component.html',
    styleUrls: ['./static-runtime-resolvable-oneof-input.component.css']
})
export class StaticRuntimeResolvableOneOfInputComponent implements OnInit, OnChanges {

    @Input()
    staticProperty: RuntimeResolvableOneOfStaticProperty;

    @Input()
    staticProperties: StaticProperty[];

    @Input()
    adapterId: string;

    @Input()
    completedStaticProperty: ConfigurationInfo;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    showOptions: boolean = false;
    loading: boolean = false;
    dependentStaticProperties: any = new Map();

    constructor(private RestService: RestService) {
    }

    ngOnInit() {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }

        if (this.staticProperty.options.length == 0 && (!this.staticProperty.dependsOn || this.staticProperty.dependsOn.length == 0)) {
            this.loadOptionsFromRestApi();
        }

        if (this.staticProperty.dependsOn && this.staticProperty.dependsOn.length > 0) {
            this.staticProperty.dependsOn.forEach(dp => {
                this.dependentStaticProperties.set(dp, false);
            });
        }
    }

    loadOptionsFromRestApi() {
        var resolvableOptionsParameterRequest = new RuntimeOptionsRequest();
        resolvableOptionsParameterRequest.staticProperties = this.staticProperties;
        resolvableOptionsParameterRequest.requestId = this.staticProperty.internalName;

        this.showOptions = false;
        this.loading = true;
        this.RestService.fetchRemoteOptions(resolvableOptionsParameterRequest, this.adapterId).subscribe(msg => {
            this.staticProperty.options = msg.options;
            if (this.staticProperty.options && this.staticProperty.options.length > 0) {
                this.staticProperty.options[0].selected = true;
            }
            this.loading = false;
            this.showOptions = true;
        });
    }

    select(id) {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }
        this.staticProperty.options.find(option => option.id === id).selected = true;
        this.inputEmitter.emit(true)
    }


    ngOnChanges(changes: SimpleChanges): void {
        if (changes['completedStaticProperty']) {
            if (this.completedStaticProperty != undefined) {
                this.dependentStaticProperties.set(this.completedStaticProperty.staticPropertyInternalName, this.completedStaticProperty.configured);
                if (Array.from(this.dependentStaticProperties.values()).every(v => v === true)) {
                    this.loadOptionsFromRestApi();
                }
            }
        }
    }
}
