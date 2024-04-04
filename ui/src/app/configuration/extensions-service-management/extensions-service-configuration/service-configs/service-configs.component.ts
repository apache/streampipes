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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SpServiceConfiguration } from '@streampipes/platform-services';
import { SemanticTypeService } from '../../../../core-services/types/semantic-type.service';

@Component({
    selector: 'sp-service-configs',
    templateUrl: './service-configs.component.html',
})
export class ServiceConfigsComponent {
    @Input() serviceConfiguration: SpServiceConfiguration;
    @Output() updateServiceConfiguration: EventEmitter<SpServiceConfiguration> =
        new EventEmitter<SpServiceConfiguration>();

    constructor(public semanticTypeService: SemanticTypeService) {}

    updateConfiguration(): void {
        this.updateServiceConfiguration.emit(this.serviceConfiguration);
    }
}
