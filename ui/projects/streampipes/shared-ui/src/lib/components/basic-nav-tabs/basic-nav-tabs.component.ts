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

import {
    ChangeDetectionStrategy,
    Component,
    Input,
    inject,
} from '@angular/core';
import { Router } from '@angular/router';
import { SpNavigationItem } from '../../models/sp-navigation.model';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { SpPageNavTabsComponent } from '../page-nav-tabs/page-nav-tabs.component';

@Component({
    selector: 'sp-basic-nav-tabs',
    templateUrl: './basic-nav-tabs.component.html',
    styleUrls: ['./basic-nav-tabs.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatIconButton,
        MatTooltip,
        MatIcon,
        SpPageNavTabsComponent,
        TranslatePipe,
    ],
})
export class SpBasicNavTabsComponent {
    private router = inject(Router);

    @Input()
    spNavigationItems: SpNavigationItem[];

    @Input()
    activeLink: string;

    @Input()
    showBackLink = false;

    @Input()
    backLinkTarget: string[] = [];

    @Input()
    backLinkLabel = 'Back';

    @Input()
    padding: string | undefined;

    @Input()
    margin: string | undefined;

    @Input()
    framed = false;

    navigateBack(): void {
        if (this.backLinkTarget.length) {
            this.router.navigate(this.backLinkTarget);
        }
    }
}
