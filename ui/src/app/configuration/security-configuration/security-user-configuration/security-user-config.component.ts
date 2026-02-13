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

import { Component, inject } from '@angular/core';
import { UserAccount } from '@streampipes/platform-services';
import { AbstractSecurityPrincipalConfig } from '../abstract-security-principal-config';
import { Observable } from 'rxjs';
import {
    DateFormatService,
    SpLabelComponent,
    SpTableComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    FlexOrderDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
} from '@angular/material/table';
import { MatButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-security-user-config',
    templateUrl: './security-user-config.component.html',
    styleUrls: ['./security-user-config.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        SpLabelComponent,
        FlexOrderDirective,
        LayoutAlignDirective,
        MatButton,
        MatTooltip,
        TranslatePipe,
    ],
})
export class SecurityUserConfigComponent extends AbstractSecurityPrincipalConfig<UserAccount> {
    displayedColumns: string[] = [
        'username',
        'provider',
        'fullName',
        'createdAtMillis',
        'lastLoginAtMillis',
        'edit',
    ];

    public dateFormatService = inject(DateFormatService);

    getObservable(): Observable<UserAccount[]> {
        return this.userAdminService.getAllUserAccounts();
    }

    editUser(account: UserAccount) {
        this.openEditDialog(account, true);
    }

    getNewInstance(): UserAccount {
        const user = new UserAccount();
        user.provider = 'local';
        return user;
    }

    formatDate(timestamp?: number): string {
        return this.dateFormatService.formatDate(timestamp);
    }
}
