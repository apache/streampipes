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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { ProfileService } from '../../profile.service';
import { BasicProfileSettings } from '../basic-profile-settings';
import { AppConstants } from '../../../services/app.constants';
import { AuthService } from '../../../services/auth.service';
import {
    CurrentUserService,
    DialogRef,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { ChangeEmailDialogComponent } from '../../dialog/change-email/change-email-dialog.component';
import { ChangePasswordDialogComponent } from '../../dialog/change-password/change-password-dialog.component';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-general-profile-settings',
    templateUrl: './general-profile-settings.component.html',
    styleUrls: ['./general-profile-settings.component.scss'],
})
export class GeneralProfileSettingsComponent
    extends BasicProfileSettings
    implements OnInit, OnDestroy
{
    darkMode = false;
    originalDarkMode = false;
    darkModeChanged = false;

    constructor(
        authService: AuthService,
        profileService: ProfileService,
        appConstants: AppConstants,
        currentUserService: CurrentUserService,
        private dialogService: DialogService,
        private router: Router,
    ) {
        super(profileService, appConstants, currentUserService, authService);
    }

    ngOnInit(): void {
        this.currentUserService.darkMode$.subscribe(
            darkMode => (this.darkMode = darkMode),
        );
        this.receiveUserData();
    }

    ngOnDestroy(): void {
        if (!this.darkModeChanged) {
            this.currentUserService.darkMode$.next(this.originalDarkMode);
        }
    }

    changeModePreview(value: boolean) {
        this.currentUserService.darkMode$.next(value);
    }

    onUserDataReceived() {
        this.originalDarkMode = this.userData.darkMode;
        this.currentUserService.darkMode$.next(this.userData.darkMode);
    }

    updateAppearanceMode() {
        this.profileService
            .updateAppearanceMode(this.userData.username, this.darkMode)
            .subscribe(response => {
                this.darkModeChanged = true;
            });
    }

    openChangeEmailDialog() {
        const dialogRef = this.dialogService.open(ChangeEmailDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Change email',
            width: '50vw',
            data: {
                user: this.userData,
            },
        });

        this.afterClose(dialogRef);
    }

    openChangePasswordDialog() {
        const dialogRef = this.dialogService.open(
            ChangePasswordDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Change password',
                width: '50vw',
                data: {
                    user: this.userData,
                },
            },
        );

        this.afterClose(dialogRef);
    }

    afterClose(dialogRef: DialogRef<any>) {
        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.authService.logout();
                this.router.navigate(['login']);
            }
        });
    }
}
