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

import { Component } from '@angular/core';
import { BaseAssetDetailsDirective } from '../base-asset-details.directive';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import {
    GenericStorageService,
    LocationConfig,
    LocationConfigService,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'sp-view-asset',
    templateUrl: './view-asset.component.html',
    styleUrls: ['./view-asset.component.scss'],
})
export class SpViewAssetComponent extends BaseAssetDetailsDirective {
    locationConfig: LocationConfig;

    constructor(
        breadcrumbService: SpBreadcrumbService,
        genericStorageService: GenericStorageService,
        route: ActivatedRoute,
        private locationConfigService: LocationConfigService,
    ) {
        super(breadcrumbService, genericStorageService, route);
    }

    onAssetAvailable() {
        this.locationConfigService
            .getLocationConfig()
            .subscribe(config => (this.locationConfig = config));
    }
}
