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
import {
    AdapterDescription,
    PipelineElementTemplate,
    PipelineElementTemplateService,
} from '@streampipes/platform-services';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { AdapterTemplateService } from '../../../services/adapter-template.service';
import { MatStepper } from '@angular/material/stepper';

@Component({
    selector: 'sp-adapter-settings',
    templateUrl: './adapter-settings.component.html',
    styleUrls: ['./adapter-settings.component.scss'],
    standalone: false,
})
export class AdapterSettingsComponent implements OnInit {
    private _formBuilder = inject(UntypedFormBuilder);
    private pipelineElementTemplateService = inject(
        PipelineElementTemplateService,
    );
    private adapterTemplateService = inject(AdapterTemplateService);

    /**
     * Adapter description the selected format is added to
     */
    @Input()
    adapterDescription: AdapterDescription;

    @Output()
    updateAdapterDescriptionEmitter: EventEmitter<AdapterDescription> =
        new EventEmitter<AdapterDescription>();

    /**
     * Cancels the adapter configuration process
     */
    @Output() removeSelectionEmitter: EventEmitter<boolean> =
        new EventEmitter();

    /**
     * Go to next configuration step when this is complete
     */
    @Output() clickNextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    cachedAdapterDescription: AdapterDescription;
    availableTemplates: PipelineElementTemplate[];

    selectedTemplate: any = false;

    specificAdapterSettingsFormValid: boolean;

    specificAdapterForm: UntypedFormGroup;

    ngOnInit(): void {
        this.loadPipelineElementTemplates();
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

    loadPipelineElementTemplates() {
        this.pipelineElementTemplateService
            .getPipelineElementTemplates(this.adapterDescription.appId)
            .subscribe(templates => {
                this.availableTemplates = templates;
            });
    }

    public removeSelection() {
        this.removeSelectionEmitter.emit();
    }

    public clickNext() {
        this.clickNextEmitter.emit();
    }

    loadTemplate(event: any) {
        if (!event.value) {
            this.adapterDescription = { ...this.cachedAdapterDescription };
            this.selectedTemplate = false;
        } else {
            this.selectedTemplate = event.value;
            this.pipelineElementTemplateService
                .getConfiguredAdapterForTemplate(
                    event.value.elementId,
                    this.adapterDescription,
                )
                .subscribe(adapterDescription => {
                    this.afterTemplateReceived(adapterDescription);
                });
        }
    }

    afterTemplateReceived(adapterDescription: any) {
        this.adapterDescription = adapterDescription;
        this.updateAdapterDescriptionEmitter.emit(this.adapterDescription);
    }
}
