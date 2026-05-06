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
import { LoginService } from '../login/services/login.service';
import { InfoTabComponent } from './components/info/info.component';
import { DocumentationTabComponent } from './components/documentation/documentation.component';
import { ShortcutsTabComponent } from './components/shortcuts/shortcuts.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatTabGroup,
        MatTab,
        TranslatePipe,
        InfoTabComponent,
        DocumentationTabComponent,
        ShortcutsTabComponent,
    ],
})
export class HelpComponent implements OnInit {
    private breadcrumbService = inject(SpBreadcrumbService);
    private loginService = inject(LoginService);
    private translateService = inject(TranslateService);

    selectedIndex = 0;
    documentationLink = '';
    showDocumentationTab = false;

    get shortcutsTabIndex(): number {
        return this.showDocumentationTab ? 2 : 1;
    }

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([
            { label: this.translateService.instant('Help') },
        ]);
        this.loginService.fetchLoginSettings().subscribe(res => {
            this.documentationLink = res.linkSettings?.documentationUrl || '';
            this.showDocumentationTab =
                !!res.linkSettings?.showDocumentationLinkInProfileMenu &&
                !!this.documentationLink;

            if (this.selectedIndex > this.shortcutsTabIndex) {
                this.selectedIndex = this.shortcutsTabIndex;
            }
        });
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }
}
