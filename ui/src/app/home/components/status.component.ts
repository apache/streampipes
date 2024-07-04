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

import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserInfo } from '@streampipes/platform-services';
import { StatusBox } from '../models/home.model';
import { UserRole } from '../../_enums/user-role.enum';
import { zip } from 'rxjs';

@Component({
    selector: 'sp-status',
    templateUrl: './status.component.html',
    styleUrls: ['./status.component.scss'],
})
export class StatusComponent implements OnInit {
    @Input()
    statusBox: StatusBox;

    @Input()
    resourceCount: number = 0;

    @Input()
    currentUser: UserInfo;

    showCreateLink = true;

    constructor(private router: Router) {}

    ngOnInit() {
        zip(this.statusBox.dataFns).subscribe(res => {
            let totalLength = 0;
            res.forEach(response => {
                totalLength += response.length;
            });

            this.resourceCount = totalLength;
        });
        this.showCreateLink = this.shouldShowCreateLink();
    }

    shouldShowCreateLink(): boolean {
        if (this.statusBox.createRoles.length === 0) {
            return false;
        } else if (this.hasRole(UserRole.ROLE_ADMIN)) {
            return true;
        } else {
            const userRoles = this.currentUser.roles;
            return userRoles.some(role =>
                this.statusBox.createRoles.includes(role as UserRole),
            );
        }
    }

    hasRole(role: UserRole): boolean {
        return this.currentUser.roles.indexOf(role) > -1;
    }

    navigate(command: string[]) {
        this.router.navigate(command);
    }
}
