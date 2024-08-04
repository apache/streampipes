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
import { ShepherdService } from '../../../../services/tour/shepherd.service';
import {
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { Pipeline } from '@streampipes/platform-services';
import { PipelineStorageOptions } from '../../../model/editor.model';

@Component({
    selector: 'sp-save-pipeline-settings',
    templateUrl: './save-pipeline-settings.component.html',
    styleUrls: ['./save-pipeline-settings.component.scss'],
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

    constructor(private shepherdService: ShepherdService) {}

    ngOnInit() {
        this.submitPipelineForm.addControl(
            'pipelineName',
            new UntypedFormControl(this.pipeline.name, [
                Validators.required,
                Validators.maxLength(40),
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
    }

    triggerTutorial() {
        if (this.shepherdService.isTourActive()) {
            this.shepherdService.trigger('save-pipeline-dialog');
        }
    }
}
