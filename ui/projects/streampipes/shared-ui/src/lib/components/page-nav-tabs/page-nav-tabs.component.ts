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

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { SpNavigationItem } from '../../models/sp-navigation.model';

@Component({
    selector: 'sp-page-nav-tabs',
    templateUrl: './page-nav-tabs.component.html',
    styleUrls: ['./page-nav-tabs.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatIcon, RouterLink, TranslatePipe],
})
export class SpPageNavTabsComponent {
    @Input({ required: true })
    spNavigationItems: SpNavigationItem[];

    @Input({ required: true })
    activeLink: string;

    @Input()
    stretchTabs = false;

    @Input()
    ariaLabel = 'Details';
}
