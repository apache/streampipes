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

import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { HomeService } from './home.service';
import { Router } from '@angular/router';
import { AppConstants } from '../services/app.constants';
import {
    CurrentUserService,
    DialogService,
    LocalStorageService,
    PanelType,
    SpAlertBannerComponent,
    SpAssetBrowserService,
    SpBreadcrumbService,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { UserRole } from '../core/auth/user-role.enum';
import { WelcomeTourComponent } from './dialog/welcome-tour/welcome-tour.component';
import { ShepherdService } from '../services/tour/shepherd.service';
import {
    AssetConstants,
    AssetLinkType,
    AssetManagementService,
    AssetSiteDesc,
    GenericStorageService,
    LocationConfig,
    LocationConfigService,
    SpAssetModel,
    UserInfo,
} from '@streampipes/platform-services';
import { forkJoin, Subscription } from 'rxjs';
import { StatusBox } from './models/home.model';
import {
    FlexDirective,
    FlexFillDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { StatusComponent } from './components/status.component';
import {
    MatButtonToggle,
    MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import { HomeAssetMapComponent } from './components/asset-map/home-asset-map.component';
import { HomeAssetTableComponent } from './components/asset-table/home-asset-table.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
    imports: [
        LayoutDirective,
        WelcomeComponent,
        LayoutAlignDirective,
        LayoutGapDirective,
        StatusComponent,
        FlexFillDirective,
        SplitSectionComponent,
        MatButtonToggleGroup,
        MatButtonToggle,
        FlexDirective,
        HomeAssetMapComponent,
        SpAlertBannerComponent,
        HomeAssetTableComponent,
        TranslatePipe,
    ],
})
export class HomeComponent implements OnInit, OnDestroy {
    serviceLinks = [];
    showStatus = false;

    statusBoxes: StatusBox[] = [];
    locationConfig: LocationConfig;
    assets: SpAssetModel[] = [];
    filteredAssets: SpAssetModel[] = [];
    sites: Record<string, AssetSiteDesc> = {};
    assetLinkTypes: Record<string, AssetLinkType> = {};

    isTutorialOpen = false;
    currentUser: UserInfo;
    selectedView = signal<string>('table');
    contentLoaded = false;

    private homeService = inject(HomeService);
    private currentUserService = inject(CurrentUserService);
    private router = inject(Router);
    public appConstants = inject(AppConstants);
    private breadcrumbService = inject(SpBreadcrumbService);
    private dialogService = inject(DialogService);
    private shepherdService = inject(ShepherdService);
    private genericStorageService = inject(GenericStorageService);
    private locationService = inject(LocationConfigService);
    private assetService = inject(AssetManagementService);
    private localStorageService = inject(LocalStorageService);
    private assetFilterService = inject(SpAssetBrowserService);

    assetFilter$: Subscription;

    constructor() {
        this.serviceLinks = this.homeService.getFilteredServiceLinks();
        this.statusBoxes = this.homeService
            .getFilteredServiceLinks()
            .filter(s => s.showStatusBox)
            .map(s => s.statusBox);
        this.selectedView.set(
            this.localStorageService.get('default-asset-view', 'table'),
        );
    }

    ngOnInit() {
        this.currentUser = this.currentUserService.getCurrentUser();
        this.assetFilter$ =
            this.assetFilterService.currentAssetFilter$.subscribe(filter => {
                this.filteredAssets = filter.selectedAssets as SpAssetModel[];
                if (this.filteredAssets) {
                    this.sortAssetLinks(this.filteredAssets);
                }
            });
        const isAdmin = this.hasRole(UserRole.ROLE_ADMIN);
        forkJoin([
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
            ),
            this.locationService.getLocationConfig(),
            this.assetService.getAllAssets(),
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_SITES_APP_DOC_NAME,
            ),
        ]).subscribe(res => {
            res[0].forEach(doc => {
                this.assetLinkTypes[doc.linkType] = doc;
            });
            this.locationConfig = res[1];
            this.assets = res[2];
            this.sortAssetLinks(this.assets);
            res[3].forEach(doc => {
                this.sites[doc._id] = doc;
            });
            this.contentLoaded = true;
            this.showStatus = true;
        });
        console.log(isAdmin);
        console.log(this.currentUser.showTutorial);
        if (isAdmin && this.currentUser.showTutorial) {
            this.checkForTutorial();
        }
        this.breadcrumbService.updateBreadcrumb([]);
    }

    hasRole(role: UserRole): boolean {
        return this.currentUser.roles.indexOf(role) > -1;
    }

    sortAssetLinks(assets: SpAssetModel[]) {
        assets.forEach(asset => {
            asset.assetLinks = [...asset.assetLinks].sort((a, b) => {
                const typeCompare = a.linkType.localeCompare(b.linkType);
                if (typeCompare !== 0) {
                    return typeCompare;
                }

                return a.linkLabel.localeCompare(b.linkLabel);
            });
        });
    }

    checkForTutorial() {
        this.isTutorialOpen = true;
        const dialogRef = this.dialogService.open(WelcomeTourComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: 'Welcome to ' + this.appConstants.APP_NAME,
            data: {
                userInfo: this.currentUser,
            },
        });
        dialogRef.afterClosed().subscribe(startTutorial => {
            if (startTutorial) {
                this.startTutorial();
            } else {
                this.isTutorialOpen = false;
            }
        });
    }

    startTutorial() {
        this.router.navigate(['connect']).then(() => {
            this.shepherdService.startAdapterTour();
            this.isTutorialOpen = false;
        });
    }

    updateView(view: string): void {
        this.selectedView.set(view);
        this.localStorageService.set('default-asset-view', view);
    }

    ngOnDestroy() {
        this.assetFilter$?.unsubscribe();
    }
}
