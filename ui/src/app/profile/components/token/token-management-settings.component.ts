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

import { Component, OnInit } from '@angular/core';
import { BasicProfileSettings } from '../basic-profile-settings';
import { RawUserApiToken, UserApiToken } from '@streampipes/platform-services';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
    MatTableDataSource,
} from '@angular/material/table';
import {
    FormControl,
    FormsModule,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { MatDivider } from '@angular/material/divider';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-token-management-settings',
    templateUrl: './token-management-settings.component.html',
    styleUrls: ['./token-management-settings.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        FormsModule,
        ReactiveFormsModule,
        MatError,
        MatButton,
        SpAlertBannerComponent,
        CdkCopyToClipboard,
        MatTable,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        MatDivider,
        RouterLink,
        TranslatePipe,
    ],
})
export class TokenManagementSettingsComponent
    extends BasicProfileSettings
    implements OnInit
{
    newTokenName: string;
    newTokenCreated = false;
    newlyCreatedToken: RawUserApiToken;

    tokenNameFormControl = new FormControl('', [
        Validators.required,
        Validators.minLength(3),
        Validators.pattern(/^[a-zA-Z0-9_-]+$/),
    ]);

    displayedColumns: string[] = ['name', 'action'];
    apiKeyDataSource: MatTableDataSource<UserApiToken>;

    ngOnInit(): void {
        this.receiveUserData();
    }

    requestNewKey() {
        const baseToken: RawUserApiToken = this.makeBaseToken();
        this.profileService
            .requestNewApiToken(this.userData.username, baseToken)
            .subscribe(result => {
                this.newlyCreatedToken = result;
                this.newTokenCreated = true;
                this.newTokenName = '';
                this.tokenNameFormControl.reset();
                this.receiveUserData();
            });
    }

    makeBaseToken(): RawUserApiToken {
        const baseToken = new RawUserApiToken();
        baseToken.tokenName = this.newTokenName;
        return baseToken;
    }

    revokeApiKey(apiKey: UserApiToken) {
        const removeIndex = this.userData.userApiTokens
            .map(token => token.tokenId)
            .indexOf(apiKey.tokenId);
        this.userData.userApiTokens.splice(removeIndex, 1);
        this.profileService
            .updateUserProfile(this.userData)
            .subscribe(_response => {
                this.receiveUserData();
            });
    }

    onUserDataReceived() {
        this.apiKeyDataSource = new MatTableDataSource<UserApiToken>(
            this.userData.userApiTokens,
        );
    }
}
