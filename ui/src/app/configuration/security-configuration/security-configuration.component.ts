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

import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { SpConfigurationTabsService } from '../configuration-tabs.service';
import { SpBreadcrumbService, SpNavigationItem } from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';
import { SecurityUserConfigComponent } from './security-user-configuration/security-user-config.component';
import { SecurityServiceConfigComponent } from './security-service-configuration/security-service-config.component';
import { SecurityRoleConfigComponent } from './role-configuration/role-configuration.component';
import { SecurityUserGroupConfigComponent } from './user-group-configuration/user-group-configuration.component';

@Component({
    selector: 'sp-security-configuration',
    templateUrl: './security-configuration.component.html',
    styleUrls: ['./security-configuration.component.scss'],
    standalone: false,
})
export class SecurityConfigurationComponent implements OnInit {
    tabs: SpNavigationItem[] = [];

    private breadcrumbService = inject(SpBreadcrumbService);
    private tabService = inject(SpConfigurationTabsService);

    @ViewChild(SecurityUserConfigComponent)
    userConfig!: SecurityUserConfigComponent;
    @ViewChild(SecurityServiceConfigComponent)
    serviceConfig!: SecurityServiceConfigComponent;
    @ViewChild(SecurityRoleConfigComponent)
    roleConfig!: SecurityRoleConfigComponent;
    @ViewChild(SecurityUserGroupConfigComponent)
    groupConfig!: SecurityUserGroupConfigComponent;

    ngOnInit(): void {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('security') },
        ]);
    }

    createUserAccount(): void {
        this.userConfig.createUser();
    }

    createServiceAccount(): void {
        this.serviceConfig.createUser();
    }

    createGroup(): void {
        this.groupConfig.createGroup();
    }

    createRole(): void {
        this.roleConfig.createRole();
    }
}
