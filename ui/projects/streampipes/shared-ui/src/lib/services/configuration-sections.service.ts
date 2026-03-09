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

import { Injectable, inject, Type } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SpNavigationItem } from '../models/sp-navigation.model';
import { CurrentUserService } from './current-user.service';
import { SpConfigurationSection } from '../models/sp-configuration-section.model';
import { SP_CONFIGURATION_SECTIONS } from './configuration-sections.token';

@Injectable({ providedIn: 'root' })
export class SpConfigurationSectionsService {
    private currentUserService = inject(CurrentUserService);
    private translateService = inject(TranslateService);
    private registeredSections =
        inject(SP_CONFIGURATION_SECTIONS, { optional: true }) ?? [];

    public getTabs(): SpNavigationItem[] {
        return this.getVisibleSections().map(section => ({
            itemId: section.itemId,
            itemTitle: this.translateService.instant(section.itemTitle),
            itemLink: ['configuration', section.itemId],
            roles: section.roles,
        }));
    }

    public getTabTitle(itemId: string): string {
        const section = this.getSection(itemId);
        return section
            ? this.translateService.instant(section.itemTitle)
            : itemId;
    }

    public getSection(itemId: string): SpConfigurationSection | undefined {
        return this.getSections().find(section => section.itemId === itemId);
    }

    public async getSectionComponent(
        itemId: string,
    ): Promise<Type<unknown> | undefined> {
        const section = this.getSection(itemId);
        if (!section) {
            return undefined;
        }

        if ('component' in section) {
            return section.component;
        }

        if ('loadComponent' in section) {
            return section.loadComponent();
        }

        return undefined;
    }

    public getDefaultTab(): SpNavigationItem | undefined {
        return this.getTabs()[0];
    }

    public isTabActive(
        activeTabs: SpNavigationItem[],
        itemId: string,
    ): boolean {
        return activeTabs.some(tab => tab.itemId === itemId);
    }

    private getVisibleSections(): SpConfigurationSection[] {
        return this.getSections().filter(section =>
            this.currentUserService.hasAnyRole(section.roles),
        );
    }

    private getSections(): SpConfigurationSection[] {
        const sections = new Map<string, SpConfigurationSection>();
        this.registeredSections.forEach(section =>
            sections.set(section.itemId, section),
        );

        return Array.from(sections.values()).sort((left, right) => {
            const leftOrder = left.order ?? Number.MAX_SAFE_INTEGER;
            const rightOrder = right.order ?? Number.MAX_SAFE_INTEGER;
            if (leftOrder === rightOrder) {
                return left.itemId.localeCompare(right.itemId);
            }
            return leftOrder - rightOrder;
        });
    }
}
