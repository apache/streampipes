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
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import { ShepherdService } from '../../../../services/tour/shepherd.service';
import {
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    CompactPipeline,
    Pipeline,
    PipelineService,
    SpAssetTreeNode,
} from '@streampipes/platform-services';
import { PipelineStorageOptions } from '../../../model/editor.model';
import { ValidateName } from '../../../../core-ui/static-properties/input.validator';

@Component({
    selector: 'sp-save-pipeline-settings',
    templateUrl: './save-pipeline-settings.component.html',
    styleUrls: ['./save-pipeline-settings.component.scss'],
    standalone: false,
})
export class SavePipelineSettingsComponent implements OnInit {
    @Input()
    submitPipelineForm: UntypedFormGroup = new UntypedFormGroup({});

    @Input()
    pipeline: Pipeline;

    @Input()
    storageOptions: PipelineStorageOptions;

    @Input()
    currentPipelineName: string;

    private shepherdService = inject(ShepherdService);
    private pipelineService = inject(PipelineService);

    compactPipeline: CompactPipeline;

    addToAssets: boolean = false;
    @Input()
    selectedAssets: SpAssetTreeNode[];
    @Input()
    deselectedAssets: SpAssetTreeNode[];
    @Input()
    originalAssets: SpAssetTreeNode[];

    @Output() selectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() deselectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() originalAssetsChange = new EventEmitter<SpAssetTreeNode[]>();

    ngOnInit() {
        this.submitPipelineForm.addControl(
            'pipelineName',
            new UntypedFormControl(this.pipeline.name, [
                Validators.required,
                Validators.minLength(3),
                Validators.maxLength(50),
                ValidateName(),
            ]),
        );
        this.submitPipelineForm.addControl(
            'pipelineDescription',
            new UntypedFormControl(this.pipeline.description, [
                Validators.maxLength(80),
            ]),
        );

        this.submitPipelineForm.controls['pipelineName'].valueChanges.subscribe(
            value => {
                this.pipeline.name = value;
            },
        );

        this.submitPipelineForm.controls[
            'pipelineDescription'
        ].valueChanges.subscribe(value => {
            this.pipeline.description = value;
        });
        this.pipelineService
            .convertToCompactPipeline(this.pipeline)
            .subscribe(p => (this.compactPipeline = p));
    }

    onSelectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.selectedAssets = updatedAssets;
        this.selectedAssetsChange.emit(this.selectedAssets);
    }

    onDeselectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.deselectedAssets = updatedAssets;
        this.deselectedAssetsChange.emit(this.deselectedAssets);
    }

    onOriginalAssetsEmitted(updatedAssets: SpAssetTreeNode[]): void {
        this.originalAssets = updatedAssets;
        this.originalAssetsChange.emit(this.originalAssets);
    }

    triggerTutorial() {
        if (this.shepherdService.isTourActive()) {
            this.shepherdService.trigger('save-pipeline-dialog');
        }
    }
}
