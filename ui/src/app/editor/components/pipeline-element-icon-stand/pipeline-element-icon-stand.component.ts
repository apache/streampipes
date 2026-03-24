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

import { AfterViewInit, Component, Input, OnInit, inject } from '@angular/core';
import {
    PeCategory,
    PipelineElementType,
    PipelineElementUnion,
} from '../../model/editor.model';
import { EditorService } from '../../services/editor.service';
import { zip } from 'rxjs';
import { Router } from '@angular/router';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    MatFormField,
    MatPrefix,
    MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { NgClass, NgStyle } from '@angular/common';
import { ClassDirective, StyleDirective } from '@ngbracket/ngx-layout/extended';
import { MatTooltip } from '@angular/material/tooltip';
import { PipelineElementIconStandRowComponent } from './pipeline-element-icon-stand-row/pipeline-element-icon-stand-row.component';
import { TranslatePipe } from '@ngx-translate/core';
import { PipelineElementGroupFilterPipe } from '../../services/pipeline-element-group-filter.pipe';
import { PipelineElementNameFilterPipe } from '../../services/pipeline-element-name-filter.pipe';
import { PipelineElementTypeFilterPipe } from '../../services/pipeline-element-type-filter.pipe';

@Component({
    selector: 'sp-pipeline-element-icon-stand',
    templateUrl: './pipeline-element-icon-stand.component.html',
    styleUrls: ['./pipeline-element-icon-stand.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        MatFormField,
        MatInput,
        FormsModule,
        MatIcon,
        MatPrefix,
        MatIconButton,
        MatSuffix,
        LayoutAlignDirective,
        NgStyle,
        StyleDirective,
        MatTooltip,
        NgClass,
        ClassDirective,
        PipelineElementIconStandRowComponent,
        TranslatePipe,
        PipelineElementGroupFilterPipe,
        PipelineElementNameFilterPipe,
        PipelineElementTypeFilterPipe,
    ],
})
export class PipelineElementIconStandComponent
    implements OnInit, AfterViewInit
{
    private editorService = inject(EditorService);
    private router = inject(Router);

    availableTypes = [
        {
            title: 'Data Streams',
            filters: [PipelineElementType.DataStream],
            open: true,
            color: 'var(--color-data-source)',
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

    elementFilter = '';
    allCategories: Map<PipelineElementType, PeCategory[]> = new Map();
    categoriesReady = false;
    uncategorized: PeCategory = {
        code: 'UNCATEGORIZED',
        label: 'Uncategorized',
        description: '',
    };

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
                    $('#outerAssemblyArea').css('border', '2px dashed #39b54a');
                },
                stop(el, ui) {
                    $('#outerAssemblyArea').css('border', '0');
                },
            });
        });
    }

    toggleOpen(availableType: any): void {
        availableType.open = !availableType.open;
        this.makeDraggable();
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
