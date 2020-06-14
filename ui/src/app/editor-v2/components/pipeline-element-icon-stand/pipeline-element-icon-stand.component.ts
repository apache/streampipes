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

import {Component, Input, OnInit,} from "@angular/core";
import * as angular from "angular";
import {RestApi} from "../../../services/rest-api.service";
import {PipelineElementType, PipelineElementUnion} from "../../model/editor.model";
import {PipelineElementTypeUtils} from "../../utils/editor.utils";
import {EditorService} from "../../services/editor.service";


@Component({
    selector: 'pipeline-element-icon-stand',
    templateUrl: './pipeline-element-icon-stand.component.html',
    styleUrls: ['./pipeline-element-icon-stand.component.css']
})
export class PipelineElementIconStandComponent implements OnInit {

    @Input()
    currentElements: PipelineElementUnion[];

    elementFilter: string;
    availableOptions: any = [];
    selectedOptions: any = [];

    _activeType: PipelineElementType;
    activeCssClass: string;

    currentElementName: string;

    constructor(private RestApi: RestApi,
                private EditorService: EditorService) {

    }

    ngOnInit(): void {
        this.loadOptions(this.activeType);
    }

    ngAfterViewInit() {
        this.makeDraggable();
    }

    openHelpDialog(pipelineElement) {
        this.EditorService.openHelpDialog(pipelineElement);
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
    set activeType(value: PipelineElementType) {
        this._activeType = value;
        this.activeCssClass = this.makeActiveCssClass(value);
        setTimeout(() => {
            this.makeDraggable();
        })
    };

    makeActiveCssClass(elementType: PipelineElementType): string {
        return PipelineElementTypeUtils.toCssShortHand(elementType);
    }

    makeDraggable() {
        (<any>$('.draggable-icon')).draggable({
            revert: 'invalid',
            helper: 'clone',
            stack: '.draggable-icon',
            start: function (el, ui) {
                ui.helper.appendTo('#content');
                $('#outerAssemblyArea').css('border', '3px dashed #39b54a');
            },
            stop: function (el, ui) {
                $('#outerAssemblyArea').css('border', '3px solid rgb(156, 156, 156)');
            }
        });
    };

}