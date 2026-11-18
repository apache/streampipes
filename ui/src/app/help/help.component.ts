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
    Component,
    OnInit,
    inject,
    ChangeDetectionStrategy,
} from '@angular/core';
import {
    CurrentUserService,
    SpBasicViewComponent,
    SpBreadcrumbService,
    SpPageHeaderComponent,
} from '@streampipes/shared-ui';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatTab, MatTabGroup, MatTabLabel } from '@angular/material/tabs';
import { LoginSettingsService } from '../login/services/login-settings.service';
import { InfoTabComponent } from './components/info/info.component';
import { DocumentationTabComponent } from './components/documentation/documentation.component';
import { ShortcutsTabComponent } from './components/shortcuts/shortcuts.component';
import { SbomTabComponent } from './components/sbom/sbom.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

export type HelpTabId = 'info' | 'documentation' | 'sbom' | 'shortcuts';

@Component({
    selector: 'sp-help',
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        SpBasicViewComponent,
        SpPageHeaderComponent,
        FlexDirective,
        LayoutDirective,
        MatTabGroup,
        MatTab,
        MatTabLabel,
        TranslatePipe,
        InfoTabComponent,
        DocumentationTabComponent,
        ShortcutsTabComponent,
        SbomTabComponent,
    ],
})
export class HelpComponent implements OnInit {
    private breadcrumbService = inject(SpBreadcrumbService);
    private loginSettingsService = inject(LoginSettingsService);
    private currentUserService = inject(CurrentUserService);
    private translateService = inject(TranslateService);

    tabs: HelpTabId[] = [];
    selectedIndex = 0;
    documentationLink = '';
    showDocumentationTab = false;
    /** The SBOM tab is only shown to administrators; the files themselves are public. */
    showSbomTab = false;

    get selectedTab(): HelpTabId | undefined {
        return this.tabs[this.selectedIndex];
    }

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([
            { label: this.translateService.instant('Help') },
        ]);
        this.showSbomTab = this.currentUserService.hasRole('ROLE_ADMIN');
        this.rebuildTabs();
        this.loginSettingsService.getSettings().subscribe(res => {
            this.documentationLink = res.linkSettings?.documentationUrl || '';
            this.showDocumentationTab =
                !!res.linkSettings?.showDocumentationLinkInProfileMenu &&
                !!this.documentationLink;
            this.rebuildTabs();
        });
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    private rebuildTabs(): void {
        const current = this.selectedTab;
        this.tabs = [
            'info',
            ...(this.showDocumentationTab ? (['documentation'] as const) : []),
            ...(this.showSbomTab ? (['sbom'] as const) : []),
            'shortcuts',
        ];
        const index = current ? this.tabs.indexOf(current) : -1;
        this.selectedIndex = index >= 0 ? index : 0;
    }
}
