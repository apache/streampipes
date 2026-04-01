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

import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { BasicProfileSettings } from '../basic-profile-settings';
import {
    DialogRef,
    DialogService,
    FormFieldComponent,
    PanelType,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { ChangeEmailDialogComponent } from '../../dialog/change-email/change-email-dialog.component';
import { ChangePasswordDialogComponent } from '../../dialog/change-password/change-password-dialog.component';
import { Router } from '@angular/router';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-general-profile-settings',
    templateUrl: './general-profile-settings.component.html',
    styleUrls: ['./general-profile-settings.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        SplitSectionComponent,
        SpAlertBannerComponent,
        MatButton,
        FormFieldComponent,
        MatFormField,
        MatInput,
        FormsModule,
        MatSelect,
        MatOption,
        MatRadioGroup,
        MatRadioButton,
        TranslatePipe,
    ],
})
export class GeneralProfileSettingsComponent
    extends BasicProfileSettings
    implements OnInit, OnDestroy
{
    private dialogService = inject(DialogService);
    private router = inject(Router);

    darkMode = false;
    originalDarkMode = false;
    darkModeChanged = false;
    isExternalUser = false;

    availableLanguages: { label: string; id: string }[] = [
        { label: 'Browser language', id: 'browser' },
        { label: 'English', id: 'en' },
        { label: 'Deutsch', id: 'de' },
        { label: 'Polski', id: 'pl' },
    ];

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
        this.updateAppearanceMode();
    }

    onUserDataReceived() {
        this.selectedLanguage = this.userData.language;
        this.originalDarkMode = this.userData.darkMode;
        this.currentUserService.darkMode$.next(this.userData.darkMode);
        this.isExternalUser = this.userData.provider !== 'local';
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
