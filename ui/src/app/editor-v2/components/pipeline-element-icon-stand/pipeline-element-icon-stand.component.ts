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

import {
    Component, Input,
    OnInit,
} from "@angular/core";
import * as angular from "angular";
import {RestApi} from "../../../services/rest-api.service";
import {
    DataProcessorInvocation, DataSinkInvocation,
    SpDataSet,
    SpDataStream
} from "../../../core-model/gen/streampipes-model";
import {EditorComponent} from "../../editor.component";


@Component({
    selector: 'pipeline-element-icon-stand',
    templateUrl: './pipeline-element-icon-stand.component.html',
    styleUrls: ['./pipeline-element-icon-stand.component.css']
})
export class PipelineElementIconStandComponent implements OnInit {

    @Input()
    currentElements: (SpDataSet | SpDataStream | DataProcessorInvocation | DataSinkInvocation)[];

    elementFilter: string;
    availableOptions: any = [];
    selectedOptions: any = [];

    _activeType: string;
    activeCssClass: string;

    currentElementName: string;

    constructor(private RestApi: RestApi) {

    }

    ngOnInit(): void {
        console.log(this.activeType);
        this.loadOptions(this.activeType);
    }

    openHelpDialog(pipelineElement) {
        //this.EditorDialogManager.openHelpDialog(pipelineElement);
    }

    updateMouseOver(elementAppId: string) {
        this.currentElementName = elementAppId;
    }

    loadOptions(type?) {
        this.RestApi.getEpCategories()
            .then(msg => {
                let s = msg;
                this.handleCategoriesSuccess("stream", s);
                this.handleCategoriesSuccess("set", s);
            });

        this.RestApi.getEpaCategories()
            .then(s => this.handleCategoriesSuccess("sepa", s));

        this.RestApi.getEcCategories()
            .then(s => this.handleCategoriesSuccess("action", s));

    };

    getOptions(type) {
        this.selectAllOptions();
        //return this.availableOptions[type];
    }


    handleCategoriesSuccess(type, result) {
        this.availableOptions[type] = result.data;
        this.selectAllOptions();
    }

    toggleFilter(option) {
        this.selectedOptions = [];
        this.selectedOptions.push(option.type);
    }

    optionSelected(option) {
        return this.selectedOptions.indexOf(option.type) > -1;
    }

    selectAllOptions() {
        this.selectedOptions = [];
        angular.forEach(this.availableOptions[this.activeType], o => {
            this.selectedOptions.push(o.type);
        });
    }

    deselectAllOptions() {
        this.selectedOptions = [];
    }

    @Input()
    set activeType(value: string) {
        this._activeType = value;
        this.activeCssClass = this.makeActiveCssClass(value);
    };

    makeActiveCssClass(elementType: string) {
        if (EditorComponent.DATA_STREAM_IDENTIFIER === elementType) {
            return "stream";
        } else if (EditorComponent.DATA_SET_IDENTIFIER === elementType) {
            return "set";
        } else if (EditorComponent.DATA_PROCESSOR_IDENTIFIER === elementType) {
            return "sepa";
        } else {
            return "action";
        }
    }

}