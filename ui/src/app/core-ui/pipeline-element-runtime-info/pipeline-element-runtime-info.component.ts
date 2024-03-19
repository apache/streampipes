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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { SpDataStream } from '@streampipes/platform-services';
import { RestService } from '../../connect/services/rest.service';

@Component({
    selector: 'sp-pipeline-element-runtime-info',
    templateUrl: './pipeline-element-runtime-info.component.html',
    styleUrls: ['./pipeline-element-runtime-info.component.scss'],
})
export class PipelineElementRuntimeInfoComponent implements OnInit, OnDestroy {
    @Input()
    streamDescription: SpDataStream;

    _pollingActive: boolean;

    runtimeData: Record<string, any>;
    timer: any;
    runtimeDataError = false;

    constructor(private restService: RestService) {}

    ngOnInit(): void {
        this.checkPollingStart();
    }

    checkPollingStart() {
        if (this._pollingActive) {
            this.getLatestRuntimeInfo();
        }
    }

    getLatestRuntimeInfo() {
        this.restService
            .getRuntimeInfo(this.streamDescription)
            .subscribe(data => {
                this.runtimeDataError = !data;
                this.runtimeData = data;

                this.timer = setTimeout(
                    () => this.getLatestRuntimeInfo(),
                    1000,
                );
            });
    }

    @Input()
    set pollingActive(pollingActive: boolean) {
        this._pollingActive = pollingActive;
        this.checkPollingStart();
    }

    get pollingActive(): boolean {
        return this._pollingActive;
    }

    ngOnDestroy(): void {
        this.pollingActive = false;
        clearTimeout(this.timer);
    }
}
