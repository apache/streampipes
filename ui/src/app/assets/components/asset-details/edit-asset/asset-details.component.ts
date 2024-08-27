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
import {
    SpAssetBrowserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { ActivatedRoute, Router } from '@angular/router';
import {
    AssetConstants,
    GenericStorageService,
} from '@streampipes/platform-services';
import { BaseAssetDetailsDirective } from '../base-asset-details.directive';

@Component({
    selector: 'sp-asset-details',
    templateUrl: './asset-details.component.html',
    styleUrls: ['./asset-details.component.scss'],
})
export class SpAssetDetailsComponent extends BaseAssetDetailsDirective {
    activeTab = 'basic';

    constructor(
        breadcrumbService: SpBreadcrumbService,
        genericStorageService: GenericStorageService,
        private assetBrowserService: SpAssetBrowserService,
        route: ActivatedRoute,
        private router: Router,
    ) {
        super(breadcrumbService, genericStorageService, route);
    }

    saveAsset() {
        this.genericStorageService
            .updateDocument(AssetConstants.ASSET_APP_DOC_NAME, this.asset)
            .subscribe(res => {
                this.assetBrowserService.loadAssetData();
                this.router.navigate(['assets']);
            });
    }

    onAssetAvailable() {}
}
