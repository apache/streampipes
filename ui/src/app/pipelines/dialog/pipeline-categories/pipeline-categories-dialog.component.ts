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

import { Component, Input, OnInit } from '@angular/core';
import {
    Pipeline,
    PipelineCategory,
    PipelineService,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-pipeline-categories-dialog',
    templateUrl: './pipeline-categories-dialog.component.html',
    styleUrls: ['./pipeline-categories-dialog.component.scss'],
})
export class PipelineCategoriesDialogComponent implements OnInit {
    addSelected: any;

    addPipelineToCategorySelected: any;
    categoryDetailsVisible: any;

    selectedPipelineId: string;
    pipelineCategories: PipelineCategory[];

    newCategoryName: string;
    newCategoryDescription: string;

    @Input()
    pipelines: Pipeline[];

    @Input()
    systemPipelines: Pipeline[];

    constructor(
        private pipelineService: PipelineService,
        private dialogRef: DialogRef<PipelineCategoriesDialogComponent>,
    ) {
        this.addSelected = false;
        this.addPipelineToCategorySelected = [];
        this.categoryDetailsVisible = [];
        this.selectedPipelineId = '';
    }

    ngOnInit() {
        this.fetchPipelineCategories();
        this.fetchPipelines();
    }

    toggleCategoryDetailsVisibility(categoryId) {
        this.categoryDetailsVisible[categoryId] =
            !this.categoryDetailsVisible[categoryId];
    }

    addPipelineToCategory(pipelineCategory) {
        console.log(pipelineCategory);
        const pipeline = this.findPipeline(this.selectedPipelineId);
        if (pipeline['pipelineCategories'] === undefined) {
            pipeline['pipelineCategories'] = [];
        }
        pipeline['pipelineCategories'].push(pipelineCategory._id);
        this.storeUpdatedPipeline(pipeline);
    }

    removePipelineFromCategory(pipeline, categoryId) {
        const index = pipeline.pipelineCategories.indexOf(categoryId);
        pipeline.pipelineCategories.splice(index, 1);
        this.storeUpdatedPipeline(pipeline);
    }

    storeUpdatedPipeline(pipeline) {
        this.pipelineService.updatePipeline(pipeline).subscribe(msg => {
            // this.refreshPipelines();
            // this.getPipelineCategories();
            this.fetchPipelineCategories();
        });
    }

    findPipeline(pipelineId) {
        let matchedPipeline = {};
        this.pipelines.forEach(pipeline => {
            if (pipeline._id === pipelineId) {
                matchedPipeline = pipeline;
            }
        });
        return matchedPipeline;
    }

    addPipelineCategory() {
        const newCategory: any = {};
        newCategory.categoryName = this.newCategoryName;
        newCategory.categoryDescription = this.newCategoryDescription;
        this.pipelineService
            .storePipelineCategory(newCategory)
            .subscribe(data => {
                this.fetchPipelineCategories();
                this.addSelected = false;
            });
    }

    fetchPipelineCategories() {
        this.pipelineService
            .getPipelineCategories()
            .subscribe(pipelineCategories => {
                this.pipelineCategories = pipelineCategories;
            });
    }

    fetchPipelines() {}

    showAddToCategoryInput(categoryId, show) {
        this.addPipelineToCategorySelected[categoryId] = show;
        this.categoryDetailsVisible[categoryId] = true;
        console.log(this.categoryDetailsVisible);
    }

    deletePipelineCategory(pipelineId) {
        this.pipelineService
            .deletePipelineCategory(pipelineId)
            .subscribe(data => {
                this.fetchPipelineCategories();
            });
    }

    showAddInput() {
        this.addSelected = true;
        this.newCategoryName = '';
        this.newCategoryDescription = '';
    }

    close() {
        this.dialogRef.close();
    }
}
