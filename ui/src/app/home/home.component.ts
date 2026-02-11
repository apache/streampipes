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
import { UserRole } from '../_enums/user-role.enum';
import { MissingElementsForTutorialComponent } from '../editor/dialog/missing-elements-for-tutorial/missing-elements-for-tutorial.component';
import { WelcomeTourComponent } from './dialog/welcome-tour/welcome-tour.component';
import { ShepherdService } from '../services/tour/shepherd.service';
import {
    AdapterDescription,
    AdapterService,
    AssetConstants,
    AssetLinkType,
    AssetManagementService,
    AssetSiteDesc,
    GenericStorageService,
    LocationConfig,
    LocationConfigService,
    NamedStreamPipesEntity,
    PipelineElementService,
    SpAssetModel,
    UserInfo,
} from '@streampipes/platform-services';
import { forkJoin, Subscription, zip } from 'rxjs';
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

    availablePipelineElements: NamedStreamPipesEntity[] = [];
    availableAdapters: AdapterDescription[] = [];

    statusBoxes: StatusBox[] = [];
    locationConfig: LocationConfig;
    assets: SpAssetModel[] = [];
    filteredAssets: SpAssetModel[] = [];
    sites: Record<string, AssetSiteDesc> = {};
    assetLinkTypes: Record<string, AssetLinkType> = {};

    requiredAdapterForTutorialAppId: any =
        'org.apache.streampipes.connect.iiot.adapters.simulator.machine';
    requiredProcessorForTutorialAppId: any =
        'org.apache.streampipes.processors.filters.jvm.numericalfilter';
    requiredSinkForTutorialAppId: any =
        'org.apache.streampipes.sinks.internal.jvm.datalake';
    missingElementsForTutorial: any = [];

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
    private pipelineElementService = inject(PipelineElementService);
    private adapterService = inject(AdapterService);
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
        if (isAdmin) {
            this.loadResources();
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
        if (this.currentUser.showTutorial) {
            if (this.requiredPipelineElementsForTourPresent()) {
                this.isTutorialOpen = true;
                const dialogRef = this.dialogService.open(
                    WelcomeTourComponent,
                    {
                        panelType: PanelType.STANDARD_PANEL,
                        title: 'Welcome to ' + this.appConstants.APP_NAME,
                        data: {
                            userInfo: this.currentUser,
                        },
                    },
                );
                dialogRef.afterClosed().subscribe(startTutorial => {
                    if (startTutorial) {
                        this.startTutorial();
                    }
                });
            }
        }
    }

    startTutorial() {
        if (this.requiredPipelineElementsForTourPresent()) {
            this.router.navigate(['connect']).then(() => {
                this.shepherdService.startAdapterTour();
            });
        } else {
            this.missingElementsForTutorial = [];
            if (!this.requiredAdapterForTutorialAppId()) {
                this.missingElementsForTutorial.push({
                    name: 'Machine Data Simulator',
                    appId: this.requiredAdapterForTutorialAppId,
                });
            }
            if (!this.requiredProcessorForTourPresent()) {
                this.missingElementsForTutorial.push({
                    name: 'Numerical Filter',
                    appId: this.requiredProcessorForTutorialAppId,
                });
            }
            if (!this.requiredSinkForTourPresent()) {
                this.missingElementsForTutorial.push({
                    name: 'Dashboard Sink',
                    appId: this.requiredSinkForTutorialAppId,
                });
            }

            this.dialogService.open(MissingElementsForTutorialComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Tutorial requires pipeline elements',
                data: {
                    missingElementsForTutorial: this.missingElementsForTutorial,
                },
            });
        }
    }

    requiredPipelineElementsForTourPresent() {
        return (
            this.requiredAdapterForTourPresent() &&
            this.requiredProcessorForTourPresent() &&
            this.requiredSinkForTourPresent()
        );
    }

    requiredAdapterForTourPresent() {
        return this.requiredPeForTourPresent(
            this.availableAdapters,
            this.requiredAdapterForTutorialAppId,
        );
    }

    requiredProcessorForTourPresent() {
        return this.requiredPeForTourPresent(
            this.availablePipelineElements,
            this.requiredProcessorForTutorialAppId,
        );
    }

    requiredSinkForTourPresent() {
        return this.requiredPeForTourPresent(
            this.availablePipelineElements,
            this.requiredSinkForTutorialAppId,
        );
    }

    requiredPeForTourPresent(list: NamedStreamPipesEntity[], appId: string) {
        return (
            list &&
            list.some(el => {
                return el.appId === appId;
            })
        );
    }

    loadResources(): void {
        zip(
            this.adapterService.getAdapterDescriptions(),
            this.pipelineElementService.getDataStreams(),
            this.pipelineElementService.getDataProcessors(),
            this.pipelineElementService.getDataSinks(),
        ).subscribe(res => {
            this.availableAdapters = res[0];
            this.availablePipelineElements = this.availablePipelineElements
                .concat(...res[1])
                .concat(...res[2])
                .concat(...res[3]);
            this.checkForTutorial();
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
