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
import { UserAccount } from '@streampipes/platform-services';
import { AbstractSecurityPrincipalConfig } from '../abstract-security-principal-config';
import { Observable } from 'rxjs';

@Component({
    selector: 'sp-security-user-config',
    templateUrl: './security-user-config.component.html',
    styleUrls: ['./security-user-config.component.scss'],
})
export class SecurityUserConfigComponent extends AbstractSecurityPrincipalConfig<UserAccount> {
    displayedColumns: string[] = ['username', 'fullName', 'edit'];

    getObservable(): Observable<UserAccount[]> {
        return this.userAdminService.getAllUserAccounts();
    }

    editUser(account: UserAccount) {
        this.openEditDialog(account, true);
    }

    getNewInstance(): UserAccount {
        return new UserAccount();
    }
}
