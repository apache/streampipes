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
    SpBasicNavTabsComponent,
    SpBreadcrumbService,
    SplitSectionComponent,
    SpNavigationItem,
} from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpRegisteredExtensionsServiceComponent } from './registered-extensions-services/registered-extensions-services.component';
import { SpExtensionsServiceConfigurationComponent } from './extensions-service-configuration/extensions-service-configuration.component';
import { CertificateConfigurationComponent } from './certificate-configuration/certificate-configuration.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-extensions-service-management',
    templateUrl: './extensions-service-management.component.html',
    imports: [
        SpBasicNavTabsComponent,
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        SplitSectionComponent,
        SpRegisteredExtensionsServiceComponent,
        SpExtensionsServiceConfigurationComponent,
        CertificateConfigurationComponent,
        TranslatePipe,
    ],
})
export class ExtensionsServiceManagementComponent implements OnInit {
    tabs: SpNavigationItem[] = [];

    constructor(
        private breadcrumbService: SpBreadcrumbService,
        private tabService: SpConfigurationTabsService,
    ) {}

    ngOnInit() {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('extensions-services') },
        ]);
    }
}
