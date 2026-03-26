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

import { Component, inject } from '@angular/core';
import { VersionInfo } from './service/version-info.model';
import { SystemInfo } from './service/system-info.model';
import { RestApi } from '../../../../services/rest-api.service';
import {
    LayoutDirective,
    LayoutGapDirective,
    FlexDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    SpBasicHeaderTitleComponent,
    SpBasicInnerPanelComponent,
} from '@streampipes/shared-ui';

@Component({
    selector: 'sp-versions',
    templateUrl: './versions.component.html',
    imports: [
        LayoutDirective,
        LayoutGapDirective,
        SpBasicHeaderTitleComponent,
        SpBasicInnerPanelComponent,
        FlexDirective,
    ],
})
export class VersionsComponent {
    private restApi = inject(RestApi);

    versionInfo: VersionInfo;
    systemInfo: SystemInfo;

    constructor() {
        this.getVersionInfo();
        this.getSystemInfo();
    }

    getVersionInfo(): void {
        this.restApi.getVersionInfo().subscribe(
            response => {
                this.versionInfo = response as VersionInfo;
            },
            error => {
                console.error(error);
            },
        );
    }

    getSystemInfo(): void {
        this.restApi.getSystemInfo().subscribe(
            response => {
                this.systemInfo = response as SystemInfo;
            },
            error => {
                console.error(error);
            },
        );
    }
}
