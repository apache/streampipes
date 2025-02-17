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

import { SpNavigationItem } from '@streampipes/shared-ui';
import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class SpConfigurationTabsService {
    allConfigurationTabs: SpNavigationItem[] = [
        {
            itemId: 'general',
            itemTitle: 'General',
            itemLink: ['configuration', 'general'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'datalake',
            itemTitle: 'Data Lake',
            itemLink: ['configuration', 'datalake'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'export',
            itemTitle: 'Export/Import',
            itemLink: ['configuration', 'export'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'extensions-installation',
            itemTitle: 'Extensions',
            itemLink: ['configuration', 'extensions-installation'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'extensions-services',
            itemTitle: 'Extension Services',
            itemLink: ['configuration', 'extensions-services'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'files',
            itemTitle: 'Files',
            itemLink: ['configuration', 'files'],
            roles: ['PRIVILEGE_WRITE_FILES'],
        },
        {
            itemId: 'labels',
            itemTitle: 'Labels',
            itemLink: ['configuration', 'labels'],
            roles: ['PRIVILEGE_WRITE_LABELS'],
        },
        {
            itemId: 'email',
            itemTitle: 'Mail',
            itemLink: ['configuration', 'email'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'messaging',
            itemTitle: 'Messaging',
            itemLink: ['configuration', 'messaging'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'security',
            itemTitle: 'Security',
            itemLink: ['configuration', 'security'],
            roles: ['ROLE_ADMIN'],
        },
        {
            itemId: 'sites',
            itemTitle: 'Sites',
            itemLink: ['configuration', 'sites'],
            roles: ['PRIVILEGE_WRITE_ASSETS'],
        },
    ];

    constructor(private authService: AuthService) {}

    public getTabs(): SpNavigationItem[] {
        return this.allConfigurationTabs.filter(c =>
            this.authService.hasAnyRole(c.roles),
        );
    }

    public getTabTitle(itemId: string): string {
        return this.allConfigurationTabs.find(t => t.itemId === itemId)
            .itemTitle;
    }

    public isTabActive(
        activeTabs: SpNavigationItem[],
        itemId: string,
    ): boolean {
        const links = activeTabs.map(tab => tab.itemLink[1]);
        return links.indexOf(itemId) !== -1;
    }
}
