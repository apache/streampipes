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

import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { BaseNavigationComponent } from '../base-navigation.component';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { UntypedFormControl } from '@angular/forms';
import { OverlayContainer } from '@angular/cdk/overlay';
import { ProfileService } from '../../../profile/profile.service';
import { LoginService } from '../../../login/services/login.service';
import {
    AssetBrowserToolbarComponent,
    SpAssetBrowserService,
} from '@streampipes/shared-ui';
import { MatToolbar } from '@angular/material/toolbar';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpBreadcrumbComponent } from '../breadcrumb/breadcrumb.component';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { MatDivider } from '@angular/material/divider';
import { ShortenPipe } from '../../pipes/shorten.pipe';

@Component({
    selector: 'sp-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.scss'],
    imports: [
        MatToolbar,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        SpBreadcrumbComponent,
        LayoutGapDirective,
        AssetBrowserToolbarComponent,
        NgClass,
        ClassDirective,
        MatIconButton,
        MatTooltip,
        MatIcon,
        MatMenuTrigger,
        MatMenu,
        MatDivider,
        MatMenuItem,
        ShortenPipe,
    ],
})
export class ToolbarComponent
    extends BaseNavigationComponent
    implements OnInit
{
    @ViewChild('feedbackOpen') feedbackOpen: MatMenuTrigger;
    @ViewChild('accountMenuOpen') accountMenuOpen: MatMenuTrigger;

    userEmail;
    darkMode: boolean;

    appearanceControl: UntypedFormControl;

    documentationLinkActive = false;
    documentationLink = '';

    private loginService = inject(LoginService);
    private profileService = inject(ProfileService);
    private overlay = inject(OverlayContainer);
    private assetFilterService = inject(SpAssetBrowserService);

    ngOnInit(): void {
        this.assetFilterService.applyAssetLinkType('');
        this.currentUserService.user$.subscribe(user => {
            const displayName = user.displayName;
            this.userEmail =
                displayName.length > 33
                    ? displayName.slice(0, 32) + '...'
                    : displayName;
            this.profileService
                .getUserProfile(user.username)
                .subscribe(userInfo => {
                    this.currentUserService.darkMode$.next(userInfo.darkMode);
                    this.darkMode =
                        this.currentUserService.darkMode$.getValue();
                    this.modifyAppearance(userInfo.darkMode);
                });
        });
        this.loginService.fetchLoginSettings().subscribe(res => {
            this.documentationLinkActive =
                res.linkSettings?.showDocumentationLinkInProfileMenu;
            this.documentationLink = res.linkSettings?.documentationUrl || '';
        });

        this.appearanceControl = new UntypedFormControl(
            this.currentUserService.darkMode$.getValue(),
        );
        this.appearanceControl.valueChanges.subscribe(darkMode => {
            this.currentUserService.darkMode$.next(darkMode);
            this.modifyAppearance(darkMode);
        });
        super.onInit();
    }

    modifyAppearance(darkMode: boolean) {
        const targets = [
            document.documentElement,
            document.body,
            this.overlay.getContainerElement(),
        ];
        const [addClass, removeClass] = darkMode
            ? ['dark-mode', 'light-mode']
            : ['light-mode', 'dark-mode'];
        targets.forEach(el => {
            el.classList.remove(removeClass);
            el.classList.add(addClass);
        });
    }

    openDocumentation() {
        window.open(this.documentationLink, '_blank');
    }

    openInfo() {
        this.router.navigate(['info']);
        this.activePage = 'Info';
    }

    openProfile() {
        this.router.navigate(['profile']);
        this.activePage = 'Profile';
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['login']);
    }
}
