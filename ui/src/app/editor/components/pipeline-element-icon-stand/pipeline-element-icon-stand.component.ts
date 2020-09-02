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
import {RestApi} from "../../../services/rest-api.service";
import {
    PipelineElementIdentifier,
    PipelineElementType,
    PipelineElementUnion
} from "../../model/editor.model";
import {PipelineElementTypeUtils} from "../../utils/editor.utils";
import {EditorService} from "../../services/editor.service";
import {zip} from "rxjs";


@Component({
    selector: 'pipeline-element-icon-stand',
    templateUrl: './pipeline-element-icon-stand.component.html',
    styleUrls: ['./pipeline-element-icon-stand.component.scss']
})
export class PipelineElementIconStandComponent implements OnInit {


    _currentElements: PipelineElementUnion[];

    currentlyFilteredElements: PipelineElementUnion[];

    elementFilter: string = "";
    allCategories: any = [];
    currentCategories: any = [];
    selectedOptions: any = [];

    _activeType: PipelineElementType;
    activeCssClass: string;

    currentElementName: string;

    constructor(private RestApi: RestApi,
                private EditorService: EditorService) {

    }

    ngOnInit(): void {
        this.loadOptions();
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

    loadOptions() {
        zip(this.EditorService.getEpCategories(),
            this.EditorService.getEpaCategories(),
            this.EditorService.getEcCategories()).subscribe((results) => {
                this.allCategories[PipelineElementType.DataStream] = results[0];
                this.allCategories[PipelineElementType.DataSet] = results[0];
                this.allCategories[PipelineElementType.DataProcessor] = results[1];
                this.allCategories[PipelineElementType.DataSink] = results[2];
                this.currentCategories = this.allCategories[0];
                this.selectedOptions = [...this.currentCategories];
            });
    };

    applyFilter() {
        this.currentlyFilteredElements = this.currentElements.filter(el => {
            return this.matchesText(el) && this.matchesCategory(el);
        })
        this.makeDraggable();
    }

    matchesText(el: PipelineElementUnion): boolean {
        return this.elementFilter === "" || el.name.toLowerCase().includes(this.elementFilter.toLowerCase());
    }

    matchesCategory(el: PipelineElementUnion): boolean {
        return this._activeType === PipelineElementType.DataStream ||
            this._activeType === PipelineElementType.DataSet ?
            this.selectedOptions.some(c => el.category.some(cg => c.type === cg)) :
            this.selectedOptions.some(c => el.category.some(cg => c.code === cg));
    }

    toggleOption(option) {
        if (this.optionSelected(option)) {
            this.selectedOptions.splice(option, 1);
        } else {
            this.selectedOptions.push(option);
        }
        this.applyFilter();
    }

    optionSelected(option) {
        return this._activeType === PipelineElementType.DataStream ||
        this._activeType === PipelineElementType.DataSet ?
            this.selectedOptions.map(o => o.type).indexOf(option.type) > -1 :
            this.selectedOptions.map(o => o.code).indexOf(option.code) > -1;
    }

    selectAllOptions() {
        this.selectedOptions = [...this.currentCategories];
        this.applyFilter();
    }

    deselectAllOptions() {
        this.selectedOptions = [];
        this.applyFilter();
    }

    @Input()
    set activeType(value: PipelineElementIdentifier) {
        let activeType = PipelineElementTypeUtils.fromClassName(value);
        this._activeType = activeType;
        if (this.allCategories.length > 0) {
            this.currentCategories = this.allCategories[this._activeType];
            this.selectedOptions = [...this.currentCategories];
        }
        this.activeCssClass = this.makeActiveCssClass(activeType);
        setTimeout(() => {
            this.makeDraggable();
        })
    };

    @Input()
    set currentElements(value: PipelineElementUnion[]) {
        this._currentElements = value;
        this.currentlyFilteredElements = this._currentElements;
    }

    get currentElements() {
        return this._currentElements;
    }

    makeActiveCssClass(elementType: PipelineElementType): string {
        return PipelineElementTypeUtils.toCssShortHand(elementType);
    }

    makeDraggable() {
       setTimeout(() => {
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
       });
    };

}