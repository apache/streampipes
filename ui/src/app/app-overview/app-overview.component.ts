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

import { Component, OnInit } from '@angular/core';
import { App } from './apps.model';
import { AvailableAppsService } from './apps';
import { Router } from '@angular/router';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpAppRoutes } from './apps.routes';

@Component({
    templateUrl: './app-overview.component.html',
    styleUrls: ['./app-overview.component.css'],
})
export class AppOverviewComponent implements OnInit {
    apps: App[] = [];

    constructor(
        private router: Router,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpAppRoutes.BASE),
        );
        this.apps = AvailableAppsService.apps;
    }

    selectApp(appLink: string) {
        this.router.navigate(['apps', appLink]);
    }
}
