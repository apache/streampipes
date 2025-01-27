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
    CurrentUserService,
    DialogService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../services/auth.service';
import { SpDataExplorerRoutes } from '../../data-explorer.routes';
import { SpDataExplorerOverviewDirective } from './data-explorer-overview.directive';
import { DataExplorerRoutingService } from '../../../data-explorer-shared/services/data-explorer-routing.service';

@Component({
    selector: 'sp-data-explorer-overview',
    templateUrl: './data-explorer-overview.component.html',
    styleUrls: ['./data-explorer-overview.component.scss'],
})
export class DataExplorerOverviewComponent extends SpDataExplorerOverviewDirective {
    resourceCount = 0;

    constructor(
        public dialogService: DialogService,
        private breadcrumbService: SpBreadcrumbService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        routingService: DataExplorerRoutingService,
    ) {
        super(dialogService, authService, currentUserService, routingService);
    }

    afterInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpDataExplorerRoutes.BASE),
        );
    }

    createNewDataView(): void {
        this.routingService.navigateToDataView(true);
    }

    applyChartFilters(elementIds: Set<string> = new Set<string>()): void {
        //this.da.applyDashboardFilters(elementIds);
    }
}
