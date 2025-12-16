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

import { Component, inject, Input, OnInit } from '@angular/core';
import { PipelineDetailsModule } from '../../../pipeline-details/pipeline-details.module';
import {
    DefaultFlexDirective,
    DefaultLayoutDirective,
    FlexFillDirective,
} from '@ngbracket/ngx-layout';
import { SharedUiModule, TimeSelectionService } from '@streampipes/shared-ui';
import { MatDivider } from '@angular/material/list';
import { forkJoin } from 'rxjs';
import {
    AssetConstants,
    AssetLinkType,
    Dashboard,
    DashboardService,
    DataExplorerWidgetModel,
    GenericStorageService,
    TimeSettings,
} from '@streampipes/platform-services';
import { ChartSharedService } from '../../../chart-shared/services/chart-shared.service';
import { DataExplorerDashboardService } from '../../../dashboard-shared/services/dashboard.service';
import { DashboardSharedModule } from '../../../dashboard-shared/dashboard-shared.module';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-dashboard-feature-card',
    templateUrl: './dashboard-feature-card.component.html',
    styleUrls: ['./dashboard-feature-card.component.scss'],
    imports: [
        PipelineDetailsModule,
        DefaultFlexDirective,
        DefaultLayoutDirective,
        SharedUiModule,
        FlexFillDirective,
        MatDivider,
        DashboardSharedModule,
    ],
})
export class DashboardFeatureCardComponent implements OnInit {
    @Input()
    resourceId: string;

    @Input()
    onClose?: () => void;

    dashboard: Dashboard;
    widgets: DataExplorerWidgetModel[] = [];
    timeSettings: TimeSettings;

    assetLinkType: AssetLinkType;

    private dashboardService = inject(DashboardService);
    private genericStorageService = inject(GenericStorageService);
    private chartSharedService = inject(ChartSharedService);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
    private timeSelectionService = inject(TimeSelectionService);
    private router = inject(Router);

    observableGenerator = this.chartSharedService.defaultObservableGenerator();

    ngOnInit(): void {
        this.getDashboard();
    }

    getDashboard(): void {
        forkJoin([
            this.dashboardService.getCompositeDashboard(this.resourceId),
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
            ),
        ]).subscribe(resp => {
            this.assetLinkType = resp[1].find(a => a.linkType === 'dashboard');
            if (resp[0]?.ok) {
                const compositeDashboard = resp[0].body;
                compositeDashboard.dashboard.widgets.forEach(w => {
                    w.id ??=
                        this.dataExplorerDashboardService.makeUniqueWidgetId();
                });
                this.dashboard = compositeDashboard.dashboard;
                this.widgets = compositeDashboard.widgets;
            }
            if (
                this.dashboard.dashboardGeneralSettings.globalTimeEnabled ===
                undefined
            ) {
                this.dashboard.dashboardGeneralSettings.globalTimeEnabled =
                    true;
            }
            if (!this.dashboard.dashboardTimeSettings.startTime) {
                this.dashboard.dashboardTimeSettings =
                    this.timeSelectionService.getDefaultTimeSettings();
            } else {
                this.timeSelectionService.updateTimeSettings(
                    this.timeSelectionService.defaultQuickTimeSelections,
                    this.dashboard.dashboardTimeSettings,
                    new Date(),
                );
            }
            this.timeSettings = this.dashboard.dashboardTimeSettings;
        });
    }

    navigateToDashboard(): void {
        this.onClose();
        this.router.navigate(['dashboard', this.resourceId]);
    }
}
