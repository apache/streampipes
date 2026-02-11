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

import { Component } from '@angular/core';
import { ServiceAccount } from '@streampipes/platform-services';
import { AbstractSecurityPrincipalConfig } from '../abstract-security-principal-config';
import { Observable } from 'rxjs';
import {
    FlexDirective,
    FlexOrderDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpTableComponent } from '@streampipes/shared-ui';
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
    selector: 'sp-security-service-config',
    templateUrl: './security-service-config.component.html',
    styleUrls: ['./security-service-config.component.scss'],
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
        FlexOrderDirective,
        LayoutAlignDirective,
        MatButton,
        MatTooltip,
        TranslatePipe,
    ],
})
export class SecurityServiceConfigComponent extends AbstractSecurityPrincipalConfig<ServiceAccount> {
    displayedColumns: string[] = ['username', 'edit'];

    getObservable(): Observable<ServiceAccount[]> {
        return this.userAdminService.getAllServiceAccounts();
    }

    editService(account: ServiceAccount) {
        this.openEditDialog(account, true);
    }

    getNewInstance(): ServiceAccount {
        return new ServiceAccount();
    }
}
