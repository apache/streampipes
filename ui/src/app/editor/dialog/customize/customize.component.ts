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
    ChangeDetectorRef,
    Component,
    Input,
    OnInit,
    ViewEncapsulation,
} from '@angular/core';
import {
    InvocablePipelineElementUnion,
    PipelineElementConfig,
    PipelineElementConfigurationStatus,
} from '../../model/editor.model';
import { DialogRef } from '@streampipes/shared-ui';
import { JsplumbService } from '../../services/jsplumb.service';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    EventSchema,
    PipelineElementTemplate,
    PipelineElementTemplateService,
} from '@streampipes/platform-services';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import { PipelineStyleService } from '../../services/pipeline-style.service';

@Component({
    selector: 'sp-customize-pipeline-element',
    templateUrl: './customize.component.html',
    styleUrls: ['./customize.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CustomizeComponent implements OnInit, AfterViewInit {
    @Input()
    pipelineElement: PipelineElementConfig;

    cachedPipelineElement: InvocablePipelineElementUnion;
    eventSchemas: EventSchema[] = [];

    displayRecommended = true;
    _showDocumentation = false;

    selection: any;
    matchingSelectionLeft: any;
    matchingSelectionRight: any;
    invalid: any;
    helpDialogVisible: any;
    validationErrors: any;

    sourceEndpoint: any;
    sepa: any;

    parentForm: UntypedFormGroup;
    formValid: boolean;
    viewInitialized = false;

    isDataProcessor = false;
    originalDialogWidth: string | number;
    completedStaticProperty: ConfigurationInfo;

    availableTemplates: PipelineElementTemplate[];
    selectedTemplate: any = false;
    templateMode = false;
    template: PipelineElementTemplate;
    templateConfigs: Map<string, any>[] = [];

    constructor(
        private dialogRef: DialogRef<CustomizeComponent>,
        private jsPlumbService: JsplumbService,
        private shepherdService: ShepherdService,
        private fb: UntypedFormBuilder,
        private changeDetectorRef: ChangeDetectorRef,
        private pipelineElementTemplateService: PipelineElementTemplateService,
        private pipelineStyleService: PipelineStyleService,
    ) {}

    ngOnInit(): void {
        this.originalDialogWidth = this.dialogRef.currentConfig().width;
        this.cachedPipelineElement = this.jsPlumbService.clone(
            this.pipelineElement.payload,
        ) as InvocablePipelineElementUnion;
        this.isDataProcessor =
            this.cachedPipelineElement instanceof DataProcessorInvocation;
        this.cachedPipelineElement.inputStreams.forEach(is => {
            this.eventSchemas = this.eventSchemas.concat(is.eventSchema);
        });
        this.formValid =
            this.pipelineElement.settings.completed ===
            PipelineElementConfigurationStatus.OK;

        this.parentForm = this.fb.group({});

        this.parentForm.valueChanges.subscribe(v => {});

        this.parentForm.statusChanges.subscribe(status => {
            this.formValid = this.viewInitialized && this.parentForm.valid;
        });
        if (this.shepherdService.isTourActive()) {
            this.shepherdService.trigger(
                'customize-' + this.pipelineElement.type,
            );
        }
        this.loadPipelineElementTemplates();
    }

    loadPipelineElementTemplates() {
        this.pipelineElementTemplateService
            .getPipelineElementTemplates(this.cachedPipelineElement.appId)
            .subscribe(templates => {
                this.availableTemplates = templates;
            });
    }

    close() {
        this.dialogRef.close();
    }

    save() {
        this.pipelineElement.payload = this.cachedPipelineElement;
        this.pipelineStyleService.updatePeConfigurationStatus(
            this.pipelineElement,
            PipelineElementConfigurationStatus.OK,
        );
        this.pipelineElement.payload.configured = true;
        if (this.shepherdService.isTourActive()) {
            this.shepherdService.trigger('save-' + this.pipelineElement.type);
        }
        this.dialogRef.close(this.pipelineElement);
    }

    validConfiguration(event: any) {}

    set showDocumentation(value: boolean) {
        if (value) {
            this.dialogRef.changeDialogSize({ width: '90vw' });
        } else {
            this.dialogRef.changeDialogSize({
                width: this.originalDialogWidth,
            });
        }
        this._showDocumentation = value;
    }

    get showDocumentation(): boolean {
        return this._showDocumentation;
    }

    ngAfterViewInit(): void {
        this.viewInitialized = true;
        this.formValid = this.viewInitialized && this.parentForm.valid;
        this.changeDetectorRef.detectChanges();
    }

    triggerUpdate(configurationInfo: ConfigurationInfo) {
        this.completedStaticProperty = { ...configurationInfo };
    }

    triggerTemplateMode() {
        this.template = new PipelineElementTemplate();
        this.templateMode = true;
    }

    saveTemplate() {
        this.template.templateConfigs = this.convert(this.templateConfigs);
        this.pipelineElementTemplateService
            .storePipelineElementTemplate(this.template)
            .subscribe(result => {
                this.loadPipelineElementTemplates();
                this.templateMode = false;
            });
    }

    convert(templateConfigs: Map<string, any>[]): Record<string, any>[] {
        return templateConfigs.map(map => {
            const obj: Record<string, any> = {};
            map.forEach((value, key) => {
                obj[key] = value;
            });
            return obj;
        });
    }

    cancelTemplateMode() {
        this.templateMode = false;
    }

    loadTemplate(event: any) {
        if (!event.value) {
            this.cachedPipelineElement = this.jsPlumbService.clone(
                this.pipelineElement.payload,
            ) as InvocablePipelineElementUnion;
            this.selectedTemplate = false;
        } else {
            this.selectedTemplate = event.value;
            if (this.cachedPipelineElement instanceof DataProcessorInvocation) {
                this.pipelineElementTemplateService
                    .getConfiguredDataProcessorForTemplate(
                        event.value.elementId,
                        this.cachedPipelineElement,
                    )
                    .subscribe(pe => {
                        this.cachedPipelineElement =
                            pe as InvocablePipelineElementUnion;
                    });
            } else {
                this.pipelineElementTemplateService
                    .getConfiguredDataSinkForTemplate(
                        event.value.elementId,
                        this.cachedPipelineElement as DataSinkInvocation,
                    )
                    .subscribe(pe => {
                        this.cachedPipelineElement =
                            pe as InvocablePipelineElementUnion;
                    });
            }
        }
    }
}
