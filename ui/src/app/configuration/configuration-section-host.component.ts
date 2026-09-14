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

import { NgComponentOutlet } from '@angular/common';
import { Component, Type, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import {
    SpBasicViewComponent,
    SpNavigationItem,
    SpPageHeaderComponent,
    SpPageNavTabsComponent,
} from '@streampipes/shared-ui';
import { map } from 'rxjs/operators';
import { SpConfigurationTabsService } from './configuration-tabs.service';

@Component({
    selector: 'sp-configuration-section-host',
    templateUrl: './configuration-section-host.component.html',
    styleUrls: ['./configuration-section-host.component.scss'],
    imports: [
        NgComponentOutlet,
        SpBasicViewComponent,
        SpPageHeaderComponent,
        SpPageNavTabsComponent,
        TranslatePipe,
    ],
})
export class ConfigurationSectionHostComponent {
    sectionComponent?: Type<unknown>;
    tabs: SpNavigationItem[] = [];
    activeLink = '';

    private route = inject(ActivatedRoute);
    private tabService = inject(SpConfigurationTabsService);

    constructor() {
        this.tabs = this.tabService.getTabs();
        this.route.paramMap
            .pipe(
                map(params => params.get('configurationSectionId')),
                takeUntilDestroyed(),
            )
            .subscribe(sectionId => {
                this.activeLink = sectionId ?? '';
                void this.updateSectionComponent(sectionId);
            });
    }

    private async updateSectionComponent(
        sectionId: string | null,
    ): Promise<void> {
        this.sectionComponent = sectionId
            ? await this.tabService.getSectionComponent(sectionId)
            : undefined;
    }
}
