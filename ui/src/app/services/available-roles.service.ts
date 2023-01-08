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

import { Injectable } from '@angular/core';
import { RoleDescription } from '../_models/auth.model';
import { UserRole } from '../_enums/user-role.enum';

@Injectable()
export class AvailableRolesService {
    availableRoles: RoleDescription[] = [
        { role: UserRole.ROLE_ADMIN, roleTitle: 'Admin', roleDescription: '' },
        {
            role: UserRole.ROLE_SERVICE_ADMIN,
            roleTitle: 'Service Admin',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_APP_USER,
            roleTitle: 'App User',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_DASHBOARD_USER,
            roleTitle: 'Dashboard User',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_DASHBOARD_ADMIN,
            roleTitle: 'Dashboard Admin',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_DATA_EXPLORER_USER,
            roleTitle: 'Data Explorer User',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_DATA_EXPLORER_ADMIN,
            roleTitle: 'Data Explorer Admin',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_CONNECT_ADMIN,
            roleTitle: 'Connect Admin',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_PIPELINE_USER,
            roleTitle: 'Pipeline User',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_PIPELINE_ADMIN,
            roleTitle: 'Pipeline Admin',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_ASSET_USER,
            roleTitle: 'Asset User',
            roleDescription: '',
        },
        {
            role: UserRole.ROLE_ASSET_ADMIN,
            roleTitle: 'Asset Admin',
            roleDescription: '',
        },
    ];

    public getAvailableRoles(): RoleDescription[] {
        return this.availableRoles;
    }
}
