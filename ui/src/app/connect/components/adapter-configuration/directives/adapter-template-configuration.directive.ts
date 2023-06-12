/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import {
    AdapterDescription,
    PipelineElementTemplate,
    PipelineElementTemplateService,
} from '@streampipes/platform-services';
import { UntypedFormBuilder } from '@angular/forms';
import { AdapterTemplateService } from '../../../services/adapter-template.service';
import { DialogService } from '@streampipes/shared-ui';

@Directive()
export abstract class AdapterTemplateConfigurationDirective {
    /**
     * Adapter description the selected format is added to
     */
    @Input() adapterDescription: AdapterDescription;

    cachedAdapterDescription: AdapterDescription;

    /**
     * Cancels the adapter configuration process
     */
    @Output() removeSelectionEmitter: EventEmitter<boolean> =
        new EventEmitter();

    /**
     * Go to next configuration step when this is complete
     */
    @Output() clickNextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    @Output()
    updateAdapterDescriptionEmitter: EventEmitter<AdapterDescription> =
        new EventEmitter<AdapterDescription>();

    availableTemplates: PipelineElementTemplate[];
    selectedTemplate: any = false;

    protected constructor(
        protected _formBuilder: UntypedFormBuilder,
        protected dialogService: DialogService,
        protected pipelineElementTemplateService: PipelineElementTemplateService,
        protected adapterTemplateService: AdapterTemplateService,
    ) {}

    onInit(): void {
        this.loadPipelineElementTemplates();
    }

    public removeSelection() {
        this.removeSelectionEmitter.emit();
    }

    public clickNext() {
        this.clickNextEmitter.emit();
    }

    loadPipelineElementTemplates() {
        this.pipelineElementTemplateService
            .getPipelineElementTemplates(this.adapterDescription.appId)
            .subscribe(templates => {
                this.availableTemplates = templates;
            });
    }

    loadTemplate(event: any) {
        if (!event.value) {
            this.adapterDescription = { ...this.cachedAdapterDescription };
            this.selectedTemplate = false;
        } else {
            this.selectedTemplate = event.value;
            this.pipelineElementTemplateService
                .getConfiguredAdapterForTemplate(
                    event.value._id,
                    this.adapterDescription,
                )
                .subscribe(adapterDescription => {
                    this.afterTemplateReceived(adapterDescription);
                });
        }
    }

    abstract openTemplateDialog(): void;

    abstract afterTemplateReceived(adapterDescription: any);
}
