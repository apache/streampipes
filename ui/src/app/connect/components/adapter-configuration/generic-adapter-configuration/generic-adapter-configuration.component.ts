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
import {
    GenericAdapterSetDescription,
    GenericAdapterStreamDescription,
    ProtocolDescription,
    PipelineElementTemplateService,
} from '@streampipes/platform-services';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { AdapterTemplateConfigurationDirective } from '../directives/adapter-template-configuration.directive';
import { AdapterTemplateService } from '../../../services/adapter-template.service';
import { DialogService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-generic-adapter-configuration',
    templateUrl: './generic-adapter-configuration.component.html',
    styleUrls: ['./generic-adapter-configuration.component.scss'],
})
export class GenericAdapterConfigurationComponent
    extends AdapterTemplateConfigurationDirective
    implements OnInit
{
    genericAdapterSettingsFormValid: boolean;

    genericAdapterForm: UntypedFormGroup;

    protocolDescription: ProtocolDescription;

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
        if (
            this.adapterDescription instanceof GenericAdapterSetDescription ||
            this.adapterDescription instanceof GenericAdapterStreamDescription
        ) {
            this.protocolDescription =
                this.adapterDescription.protocolDescription;
        }

        // initialize form for validation
        this.genericAdapterForm = this._formBuilder.group({});
        this.genericAdapterForm.statusChanges.subscribe(_ => {
            this.genericAdapterSettingsFormValid =
                this.genericAdapterForm.valid;
        });
    }

    openTemplateDialog(): void {
        const dialogRef = this.adapterTemplateService.getDialog(
            this.protocolDescription.config,
            this.protocolDescription.appId,
        );

        dialogRef.afterClosed().subscribe(_ => {
            this.loadPipelineElementTemplates();
        });
    }

    afterTemplateReceived(adapterDescription: any) {
        this.protocolDescription = ProtocolDescription.fromData(
            adapterDescription.protocolDescription,
        );
        if (
            this.adapterDescription instanceof GenericAdapterSetDescription ||
            this.adapterDescription instanceof GenericAdapterStreamDescription
        ) {
            this.adapterDescription.protocolDescription =
                this.protocolDescription;
            this.updateAdapterDescriptionEmitter.emit(this.adapterDescription);
        }
    }
}
