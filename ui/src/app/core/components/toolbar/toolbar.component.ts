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

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { BaseNavigationComponent } from '../base-navigation.component';
import { Router } from '@angular/router';
import { RestApi } from '../../../services/rest-api.service';
import { MatMenuTrigger } from '@angular/material/menu';
import { UntypedFormControl } from '@angular/forms';
import { OverlayContainer } from '@angular/cdk/overlay';
import { ProfileService } from '../../../profile/profile.service';
import { AuthService } from '../../../services/auth.service';
import { AppConstants } from '../../../services/app.constants';
import { Subscription, timer } from 'rxjs';
import { exhaustMap } from 'rxjs/operators';
import { NotificationCountService } from '../../../services/notification-count-service';
import { CurrentUserService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.scss'],
})
export class ToolbarComponent
    extends BaseNavigationComponent
    implements OnInit, OnDestroy
{
    @ViewChild('feedbackOpen') feedbackOpen: MatMenuTrigger;
    @ViewChild('accountMenuOpen') accountMenuOpen: MatMenuTrigger;

    userEmail;
    darkMode: boolean;

    appearanceControl: UntypedFormControl;

    unreadNotificationCount = 0;
    unreadNotificationsSubscription: Subscription;

    constructor(
        router: Router,
        authService: AuthService,
        private profileService: ProfileService,
        private restApi: RestApi,
        private overlay: OverlayContainer,
        currentUserService: CurrentUserService,
        appConstants: AppConstants,
        public notificationCountService: NotificationCountService,
    ) {
        super(authService, currentUserService, router, appConstants);
    }

    ngOnInit(): void {
        this.unreadNotificationsSubscription = timer(0, 10000)
            .pipe(exhaustMap(() => this.restApi.getUnreadNotificationsCount()))
            .subscribe(response => {
                this.notificationCountService.unreadNotificationCount$.next(
                    response.count,
                );
            });

        this.notificationCountService.unreadNotificationCount$.subscribe(
            count => {
                this.unreadNotificationCount = count;
            },
        );
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
        if (darkMode) {
            this.overlay.getContainerElement().classList.remove('light-mode');
            this.overlay.getContainerElement().classList.add('dark-mode');
        } else {
            this.overlay.getContainerElement().classList.remove('dark-mode');
            this.overlay.getContainerElement().classList.add('light-mode');
        }
    }

    openDocumentation() {
        window.open('https://streampipes.apache.org/docs', '_blank');
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

    ngOnDestroy() {
        this.unreadNotificationsSubscription.unsubscribe();
    }
}
