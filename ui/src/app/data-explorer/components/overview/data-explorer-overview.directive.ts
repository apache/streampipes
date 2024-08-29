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

import { Directive, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { UserRole } from '../../../_enums/user-role.enum';
import { CurrentUserService, DialogService } from '@streampipes/shared-ui';
import { AuthService } from '../../../services/auth.service';
import { DataExplorerRoutingService } from '../../services/data-explorer-routing.service';

@Directive()
export abstract class SpDataExplorerOverviewDirective
    implements OnInit, OnDestroy
{
    isAdmin = false;

    public hasDataExplorerWritePrivileges = false;

    authSubscription: Subscription;

    protected constructor(
        public dialogService: DialogService,
        protected authService: AuthService,
        protected currentUserService: CurrentUserService,
        protected routingService: DataExplorerRoutingService,
    ) {}

    ngOnInit() {
        this.authSubscription = this.currentUserService.user$.subscribe(
            user => {
                this.hasDataExplorerWritePrivileges = this.authService.hasRole(
                    UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
                );
                this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
                this.afterInit();
            },
        );
    }

    abstract afterInit(): void;

    ngOnDestroy() {
        this.authSubscription?.unsubscribe();
    }
}
