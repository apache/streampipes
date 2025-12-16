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

import { Component, inject, Input, OnInit, ViewChild } from '@angular/core';
import {
    AssetLinkType,
    AssetSiteDesc,
    Isa95TypeService,
    LocationConfig,
    SpAssetModel,
} from '@streampipes/platform-services';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-home-asset-table',
    templateUrl: './home-asset-table.component.html',
    styleUrls: ['./home-asset-table.component.scss'],
    standalone: false,
})
export class HomeAssetTableComponent implements OnInit {
    @Input()
    locationConfig: LocationConfig;

    @Input()
    assets: SpAssetModel[] = [];

    @Input()
    sites: Record<string, AssetSiteDesc> = {};

    @Input()
    assetLinkTypes: Record<string, AssetLinkType> = {};

    displayedColumns: string[] = [
        'assetName',
        'assetType',
        'location',
        'area',
        'assetLinks',
    ];

    @ViewChild(MatSort)
    sort: MatSort;

    dataSource: MatTableDataSource<SpAssetModel> =
        new MatTableDataSource<SpAssetModel>();

    private isa95TypeService = inject(Isa95TypeService);
    private router = inject(Router);

    ngOnInit() {
        this.dataSource.data = this.assets;
    }

    getIsa95Type(asset: SpAssetModel): string {
        return this.isa95TypeService.toLabel(asset.assetType?.isa95AssetType);
    }

    getSite(asset: SpAssetModel): string {
        if (!asset.assetSite?.siteId) {
            return '-';
        } else {
            return this.sites[asset.assetSite.siteId].label;
        }
    }

    navigateToAsset(asset: SpAssetModel): void {
        this.router.navigate(['assets', 'details', asset.elementId, 'view']);
    }
}
