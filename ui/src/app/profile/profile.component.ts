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

import { Component, OnInit, inject } from '@angular/core';
import {
    SpBasicViewComponent,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { GeneralProfileSettingsComponent } from './components/general/general-profile-settings.component';
import { TokenManagementSettingsComponent } from './components/token/token-management-settings.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatTabGroup,
        MatTab,
        GeneralProfileSettingsComponent,
        TokenManagementSettingsComponent,
        TranslatePipe,
    ],
})
export class ProfileComponent implements OnInit {
    private breadcrumbService = inject(SpBreadcrumbService);

    selectedIndex = 0;

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([{ label: 'Profile' }]);
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }
}
