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
import { StreampipesPeContainer } from '../shared/streampipes-pe-container.model';

@Component({
    selector: 'sp-consul-service',
    templateUrl: './consul-service.component.html',
    styleUrls: ['./consul-service.component.css'],
})
export class ConsulServiceComponent {
    @Input() consulService: StreampipesPeContainer;
    @Output() updateConsulService: EventEmitter<StreampipesPeContainer> =
        new EventEmitter<StreampipesPeContainer>();
    showConfiguration = false;

    constructor() {}

    toggleConfiguration(): void {
        this.showConfiguration = !this.showConfiguration;
    }

    updateConfiguration(): void {
        this.updateConsulService.emit(this.consulService);
    }
}
