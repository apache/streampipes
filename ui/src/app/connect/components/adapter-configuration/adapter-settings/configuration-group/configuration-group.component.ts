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

import { Component, Input, OnInit, inject } from '@angular/core';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormGroup,
} from '@angular/forms';
import {
    ExtensionDeploymentConfiguration,
    StaticPropertyUnion,
} from '@streampipes/platform-services';
import { ConfigurationInfo } from '../../../../model/ConfigurationInfo';
import { StaticPropertyUtilService } from '../../../../../core-ui/static-properties/static-property-util.service';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { StaticPropertyComponent } from '../../../../../core-ui/static-properties/static-property.component';

@Component({
    selector: 'sp-configuration-group',
    templateUrl: './configuration-group.component.html',
    styleUrls: ['./configuration-group.component.scss'],
    imports: [
        FormsModule,
        FlexDirective,
        ReactiveFormsModule,
        StaticPropertyComponent,
    ],
})
export class ConfigurationGroupComponent implements OnInit {
    private staticPropertyUtils = inject(StaticPropertyUtilService);

    @Input() configurationGroup: UntypedFormGroup;

    @Input() adapterId: string;

    @Input() configuration: StaticPropertyUnion[];

    @Input() deploymentConfiguration: ExtensionDeploymentConfiguration;

    completedConfigurations: ConfigurationInfo[] = [];

    ngOnInit() {
        this.completedConfigurations =
            this.staticPropertyUtils.initializeCompletedConfigurations(
                this.configuration,
            );
    }

    updateCompletedConfiguration(configurationInfo: ConfigurationInfo) {
        this.staticPropertyUtils.updateCompletedConfiguration(
            configurationInfo,
            this.completedConfigurations,
        );
        this.completedConfigurations = [...this.completedConfigurations];
    }
}
