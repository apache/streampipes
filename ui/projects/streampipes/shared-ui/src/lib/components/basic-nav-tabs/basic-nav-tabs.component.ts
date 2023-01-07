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

import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { SpNavigationItem } from '../../models/sp-navigation.model';

@Component({
    selector: 'sp-basic-nav-tabs',
    templateUrl: './basic-nav-tabs.component.html',
    styleUrls: ['./basic-nav-tabs.component.scss'],
})
export class SpBasicNavTabsComponent {
    @Input()
    spNavigationItems: SpNavigationItem[];

    @Input()
    activeLink: string;

    @Input()
    showBackLink = false;

    @Input()
    backLinkTarget: string[] = [];

    constructor(private router: Router) {}

    navigateTo(spNavigationItem: SpNavigationItem) {
        this.router.navigate(spNavigationItem.itemLink);
    }

    navigateBack() {
        this.router.navigate(this.backLinkTarget);
    }
}
