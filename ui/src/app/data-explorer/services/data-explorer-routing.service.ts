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

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class DataExplorerRoutingService {
    private dataViewPath = ['dataexplorer', 'data-view'];
    private dashboardPath = ['dataexplorer', 'dashboard'];

    constructor(private router: Router) {}

    navigateToOverview(omitConfirm: boolean = false): void {
        this.router.navigate(['dataexplorer'], { state: { omitConfirm } });
    }

    navigateToDataView(
        editMode: boolean,
        dataViewElementId?: string,
        newTab = false,
    ) {
        return this.navigate(
            editMode,
            this.dataViewPath,
            dataViewElementId,
            newTab,
        );
    }

    navigateToDashboard(
        editMode: boolean,
        dashboardElementId?: string,
        newTab = false,
    ) {
        return this.navigate(
            editMode,
            this.dashboardPath,
            dashboardElementId,
            newTab,
        );
    }

    private navigate(
        editMode: boolean,
        initialPath: string[],
        resourceElementId?: string,
        openInNewTab = false,
    ) {
        const queryParams = editMode ? { editMode: true } : {};
        const navigationExtras = {
            queryParams,
        };

        // Generate the URL
        const urlTree = this.router.createUrlTree(
            this.getPath(initialPath, resourceElementId),
            navigationExtras,
        );
        const url = this.router.serializeUrl(urlTree);

        if (openInNewTab) {
            window.open('/#' + url, '_blank');
        } else {
            // Navigate using the router
            return this.router.navigateByUrl(urlTree);
        }
    }

    private getPath(
        initialPath: string[],
        resourceElementId?: string,
    ): string[] {
        return resourceElementId
            ? [...initialPath, resourceElementId]
            : initialPath;
    }
}
