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

import { Component } from '@angular/core';
import { Pipeline, PipelineService } from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { forkJoin } from 'rxjs';

@Component({
    selector: 'sp-import-pipeline-dialog',
    templateUrl: './import-pipeline-dialog.component.html',
    styleUrls: ['./import-pipeline-dialog.component.scss'],
})
export class ImportPipelineDialogComponent {
    currentStatus: any;
    page = 'upload-pipelines';

    availablePipelines: Pipeline[] = [];
    selectedPipelines: Pipeline[] = [];

    importing = false;

    pages = [
        {
            type: 'upload-pipelines',
            title: 'Upload',
            description:
                'Upload a json file containing the pipelines to import',
        },
        {
            type: 'select-pipelines',
            title: 'Select pipelines',
            description: 'Select the pipelines to import',
        },
        {
            type: 'import-pipelines',
            title: 'Import',
            description: '',
        },
    ];

    constructor(
        private pipelineService: PipelineService,
        private dialogRef: DialogRef<ImportPipelineDialogComponent>,
    ) {}

    handleFileInput(files: any) {
        const file = files[0];
        const aReader = new FileReader();
        aReader.readAsText(file, 'UTF-8');
        aReader.onload = evt => {
            this.availablePipelines = JSON.parse(aReader.result as string);
            console.log(this.availablePipelines);
            this.page = 'select-pipelines';
        };
    }

    close(refreshPipelines: boolean) {
        this.dialogRef.close(refreshPipelines);
    }

    back() {
        if (this.page === 'select-pipelines') {
            this.page = 'upload-pipelines';
        } else if (this.page === 'import-pipelines') {
            this.page = 'select-pipelines';
        }
    }

    toggleSelectedPipeline(pipeline: Pipeline) {
        if (this.selectedPipelines.some(p => p._id === pipeline._id)) {
            this.selectedPipelines.splice(
                this.selectedPipelines.findIndex(sp => sp._id === pipeline._id),
                1,
            );
        } else {
            this.selectedPipelines.push(pipeline);
        }
    }

    storePipelines() {
        const promises = [];
        this.selectedPipelines.forEach(pipeline => {
            pipeline._rev = undefined;
            pipeline._id = undefined;
            promises.push(this.pipelineService.storePipeline(pipeline));
        });

        forkJoin(promises).subscribe(results => {
            this.importing = false;
            this.close(true);
        });
    }

    startImport() {
        this.page = 'import-pipelines';
        this.importing = true;
        this.storePipelines();
    }
}
