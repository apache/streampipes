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
import { map } from 'rxjs/operators';
import { SpConfigurationTabsService } from './configuration-tabs.service';

@Component({
    selector: 'sp-configuration-section-host',
    template: `
        @if (sectionComponent) {
            <ng-container *ngComponentOutlet="sectionComponent"></ng-container>
        }
    `,
    imports: [NgComponentOutlet],
})
export class ConfigurationSectionHostComponent {
    sectionComponent?: Type<unknown>;

    private route = inject(ActivatedRoute);
    private tabService = inject(SpConfigurationTabsService);

    constructor() {
        this.route.paramMap
            .pipe(
                map(params => params.get('configurationSectionId')),
                takeUntilDestroyed(),
            )
            .subscribe(sectionId => {
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
