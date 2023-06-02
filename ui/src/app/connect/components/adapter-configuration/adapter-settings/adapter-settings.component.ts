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

import { Component, OnInit } from '@angular/core';
import { PipelineElementTemplateService } from '@streampipes/platform-services';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { DialogService } from '@streampipes/shared-ui';
import { AdapterTemplateService } from '../../../services/adapter-template.service';
import { AdapterTemplateConfigurationDirective } from '../directives/adapter-template-configuration.directive';

@Component({
    selector: 'sp-adapter-settings',
    templateUrl: './adapter-settings.component.html',
    styleUrls: ['./adapter-settings.component.scss'],
})
export class AdapterSettingsComponent
    extends AdapterTemplateConfigurationDirective
    implements OnInit
{
    specificAdapterSettingsFormValid: boolean;

    specificAdapterForm: UntypedFormGroup;

    constructor(
        _formBuilder: UntypedFormBuilder,
        dialogService: DialogService,
        pipelineElementTemplateService: PipelineElementTemplateService,
        adapterTemplateService: AdapterTemplateService,
    ) {
        super(
            _formBuilder,
            dialogService,
            pipelineElementTemplateService,
            adapterTemplateService,
        );
    }

    ngOnInit(): void {
        super.onInit();
        this.cachedAdapterDescription = { ...this.adapterDescription };
        // initialize form for validation
        this.specificAdapterForm = this._formBuilder.group({});
        this.specificAdapterForm.statusChanges.subscribe(_ => {
            this.specificAdapterSettingsFormValid =
                this.specificAdapterForm.valid;
        });

        // Go directly to event schema configuration when adapter has no configuration properties
        if (this.adapterDescription.config.length === 0) {
            this.specificAdapterSettingsFormValid = true;
        }
    }

    openTemplateDialog(): void {
        const dialogRef = this.adapterTemplateService.getDialog(
            this.adapterDescription.config,
            this.adapterDescription.appId,
        );

        dialogRef.afterClosed().subscribe(_ => {
            this.loadPipelineElementTemplates();
        });
    }

    afterTemplateReceived(adapterDescription: any) {
        this.adapterDescription = adapterDescription;
        this.updateAdapterDescriptionEmitter.emit(this.adapterDescription);
    }
}
