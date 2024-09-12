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
import { SpConfigurationTabsService } from '../configuration-tabs.service';
import {
    LocationConfig,
    LocationConfigService,
} from '@streampipes/platform-services';
import { SpBreadcrumbService, SpNavigationItem } from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';

@Component({
    selector: 'sp-sites-configuration',
    templateUrl: './sites-configuration.component.html',
})
export class SitesConfigurationComponent implements OnInit {
    tabs: SpNavigationItem[] = [];

    locationConfig: LocationConfig;

    constructor(
        private locationConfigService: LocationConfigService,
        private tabService: SpConfigurationTabsService,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit() {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('sites') },
        ]);
        this.locationConfigService.getLocationConfig().subscribe(res => {
            this.locationConfig = res;
        });
    }
}
