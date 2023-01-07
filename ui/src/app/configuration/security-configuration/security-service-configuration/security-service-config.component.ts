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

@Component({
    selector: 'sp-security-service-config',
    templateUrl: './security-service-config.component.html',
    styleUrls: ['./security-service-config.component.scss'],
})
export class SecurityServiceConfigComponent extends AbstractSecurityPrincipalConfig<ServiceAccount> {
    displayedColumns: string[] = ['username', 'edit'];

    getObservable(): Observable<ServiceAccount[]> {
        return this.userService.getAllServiceAccounts();
    }

    editService(account: ServiceAccount) {
        this.openEditDialog(account, true);
    }

    getNewInstance(): ServiceAccount {
        return new ServiceAccount();
    }
}
