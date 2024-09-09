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

import { Component, Input, OnInit } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    AssetConstants,
    AssetSiteDesc,
    GenericStorageService,
    LocationConfig,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-manage-site-dialog-component',
    templateUrl: './manage-site-dialog.component.html',
    styleUrls: ['./manage-site-dialog.component.scss'],
})
export class ManageSiteDialogComponent implements OnInit {
    @Input()
    site: AssetSiteDesc;

    @Input()
    locationConfig: LocationConfig;

    clonedSite: AssetSiteDesc;
    createMode = false;

    constructor(
        private dialogRef: DialogRef<ManageSiteDialogComponent>,
        private genericStorageService: GenericStorageService,
    ) {}

    ngOnInit(): void {
        if (this.site !== undefined) {
            this.clonedSite = JSON.parse(JSON.stringify(this.site));
        } else {
            this.initializeNewSite();
        }
    }

    close(emitReload = false): void {
        this.dialogRef.close(emitReload);
    }

    initializeNewSite(): void {
        this.clonedSite = {
            appDocType: AssetConstants.ASSET_SITES_APP_DOC_NAME,
            _id: undefined,
            label: 'New site',
            location: { coordinates: { latitude: 0, longitude: 0 } },
            areas: [],
        };
        this.createMode = true;
    }

    store(): void {
        const observable = this.createMode
            ? this.genericStorageService.createDocument(
                  AssetConstants.ASSET_SITES_APP_DOC_NAME,
                  this.clonedSite,
              )
            : this.genericStorageService.updateDocument(
                  AssetConstants.ASSET_SITES_APP_DOC_NAME,
                  this.clonedSite,
              );
        observable.subscribe(res => this.close(true));
    }
}
