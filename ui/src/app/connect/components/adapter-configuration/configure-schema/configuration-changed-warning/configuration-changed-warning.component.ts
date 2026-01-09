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

import { Component, inject, Input } from '@angular/core';
import { AdapterConfigurationStateService } from '../../adapter-configuration-state-service/adapter-configuration-state.service';
import { AdapterDescription } from '@streampipes/platform-services';

@Component({
    selector: 'sp-configuration-changed-warning',
    standalone: false,
    templateUrl: './configuration-changed-warning.component.html',
    styleUrl: './configuration-changed-warning.component.scss',
})
export class ConfigurationChangedWarningComponent {
    private stateService = inject(AdapterConfigurationStateService);

    @Input()
    adapterDescription: AdapterDescription;

    refreshSampleEvent() {
        this.stateService.getSampleEvent(this.adapterDescription);
    }

    acknowledgeNoChanges() {
        this.stateService.updateState({
            adapterDescription: this.adapterDescription,
            isConfigurationChanged: false,
            adapterSettingsConfigString: JSON.stringify(
                this.adapterDescription.config,
            ),
        });
    }
}
