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
    AfterViewInit,
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import { RestApi } from '../../../services/rest-api.service';
import {
    PeCategory,
    PipelineElementType,
    PipelineElementUnion,
} from '../../model/editor.model';
import { EditorService } from '../../services/editor.service';
import { zip } from 'rxjs';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-pipeline-element-icon-stand',
    templateUrl: './pipeline-element-icon-stand.component.html',
    styleUrls: ['./pipeline-element-icon-stand.component.scss'],
})
export class PipelineElementIconStandComponent
    implements OnInit, AfterViewInit
{
    availableTypes = [
        {
            title: 'Data Sources',
            filters: [
                PipelineElementType.DataStream,
                PipelineElementType.DataSet,
            ],
            open: true,
            color: 'var(--color-stream)',
            sort: 'name',
        },
        {
            title: 'Data Processors',
            filters: [PipelineElementType.DataProcessor],
            open: true,
            color: 'var(--color-processor)',
            sort: 'name',
        },
        {
            title: 'Data Sinks',
            filters: [PipelineElementType.DataSink],
            open: true,
            color: 'var(--color-sink)',
            sort: 'name',
        },
    ];

    @Input()
    allElements: PipelineElementUnion[];

    @Output()
    startTourEmitter: EventEmitter<void> = new EventEmitter<void>();

    elementFilter = '';
    allCategories: Map<PipelineElementType, PeCategory[]> = new Map();
    categoriesReady = false;
    uncategorized: PeCategory = {
        code: 'UNCATEGORIZED',
        label: 'Uncategorized',
        description: '',
    };

    constructor(
        private restApi: RestApi,
        private editorService: EditorService,
        private router: Router,
    ) {}

    ngOnInit(): void {
        this.loadOptions();
    }

    ngAfterViewInit() {
        this.makeDraggable();
    }

    loadOptions() {
        zip(
            this.editorService.getEpCategories(),
            this.editorService.getEpaCategories(),
            this.editorService.getEcCategories(),
        ).subscribe(results => {
            results[0] = this.sort(results[0]).filter(category =>
                this.filterForExistingCategories(category),
            );
            results[1] = this.sort(results[1]).filter(category =>
                this.filterForExistingCategories(category),
            );
            results[2] = this.sort(results[2]).filter(category =>
                this.filterForExistingCategories(category),
            );
            this.allCategories.set(PipelineElementType.DataStream, results[0]);
            this.allCategories.set(
                PipelineElementType.DataProcessor,
                results[1],
            );
            this.allCategories.set(PipelineElementType.DataSink, results[2]);
            this.categoriesReady = true;
        });
    }

    filterForExistingCategories(category: PeCategory): boolean {
        return (
            this.allElements
                .filter(element => element.category)
                .find(element =>
                    element.category.find(elCat => elCat === category.code),
                ) !== undefined ||
            (category.code === this.uncategorized.code &&
                this.allElements.find(element => !element.category) !==
                    undefined)
        );
    }

    sort(categories: PeCategory[]) {
        return categories.sort((a, b) => {
            return a.label.localeCompare(b.label);
        });
    }

    makeDraggable() {
        setTimeout(() => {
            ($('.draggable-pipeline-element') as any).draggable({
                revert: 'invalid',
                helper: ev => {
                    const draggable = $(ev.currentTarget)
                        .find('.draggable-icon-editor')
                        .first()
                        .clone();
                    const draggableContainer = $(draggable)
                        .find('.pe-container')
                        .first();
                    $(draggable).removeClass('draggable-icon-editor');
                    $(draggable).addClass('draggable-icon-drag');
                    $(draggableContainer).removeClass('pe-container');
                    $(draggableContainer).addClass('pe-container-drag');
                    return draggable.clone();
                },
                stack: '.draggable-pipeline-element',
                start(el, ui) {
                    ui.helper.appendTo('#content');
                    $('#outerAssemblyArea').css('border', '3px dashed #39b54a');
                },
                stop(el, ui) {
                    $('#outerAssemblyArea').css(
                        'border',
                        '1px solid var(--color-bg-3)',
                    );
                },
            });
        });
    }

    toggleOpen(availableType: any): void {
        availableType.open = !availableType.open;
        this.makeDraggable();
    }

    startCreatePipelineTour() {
        this.startTourEmitter.emit();
    }

    changeSorting(availableType: any, sortMode: string) {
        availableType.sort = sortMode;
        this.makeDraggable();
    }

    clearInput() {
        this.elementFilter = '';
        this.makeDraggable();
    }

    navigateToConnect() {
        this.router.navigate(['connect']);
    }
}
