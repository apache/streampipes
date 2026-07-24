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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    ExtensionDeploymentConfiguration,
    ServiceTagService,
    SpServiceTag,
} from '@streampipes/platform-services';
import { FormsModule } from '@angular/forms';
import {
    MatRadioButton,
    MatRadioChange,
    MatRadioGroup,
} from '@angular/material/radio';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';
import {
    FormFieldComponent,
    SearchSelectComponent,
} from '@streampipes/shared-ui';

@Component({
    selector: 'sp-adapter-deployment-settings',
    templateUrl: './adapter-deployment-settings.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        MatRadioGroup,
        FormsModule,
        MatRadioButton,
        TranslatePipe,
        FormFieldComponent,
        SearchSelectComponent,
    ],
})
export class SpAdapterDeploymentSettingsComponent implements OnInit {
    private serviceTagService = inject(ServiceTagService);

    @Input()
    deploymentConfiguration: ExtensionDeploymentConfiguration;

    availableServiceTags: SpServiceTag[] = [];

    deploymentMode = 'all';

    ngOnInit(): void {
        if (this.deploymentConfiguration.desiredServiceTags.length > 0) {
            this.deploymentMode = 'filter';
        }
        this.serviceTagService.getCustomServiceTags().subscribe(res => {
            this.availableServiceTags = res;
        });
    }

    onServiceTagsChange(
        serviceTags: SpServiceTag | SpServiceTag[] | undefined,
    ): void {
        this.deploymentConfiguration.desiredServiceTags = Array.isArray(
            serviceTags,
        )
            ? serviceTags
            : [];
    }

    handleSelectionChange(event: MatRadioChange): void {
        if (event.value === 'all') {
            this.deploymentConfiguration.desiredServiceTags = [];
        }
    }
}
