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

import { Directive, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {
    ServiceAccount,
    UserAccount,
    UserAdminService,
    UserService,
} from '@streampipes/platform-services';
import { Observable } from 'rxjs';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { EditUserDialogComponent } from './edit-user-dialog/edit-user-dialog.component';

@Directive()
export abstract class AbstractSecurityPrincipalConfig<
    T extends UserAccount | ServiceAccount,
> implements OnInit
{
    users: T[] = [];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSize = 1;
    @ViewChild(MatSort) sort: MatSort;

    dataSource: MatTableDataSource<T>;

    constructor(
        protected userService: UserService,
        protected userAdminService: UserAdminService,
        protected dialogService: DialogService,
    ) {}

    ngOnInit(): void {
        this.load();
    }

    openEditDialog(user: UserAccount | ServiceAccount, editMode: boolean) {
        const dialogRef = this.dialogService.open(EditUserDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: editMode ? 'Edit user ' + user.username : 'Add user',
            width: '50vw',
            data: {
                user: user,
                editMode: editMode,
            },
        });

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.load();
            }
        });
    }

    createUser() {
        const principal = this.getNewInstance();
        principal.roles = [];
        principal.groups = [];
        this.openEditDialog(principal, false);
    }

    load() {
        this.getObservable().subscribe(response => {
            this.users = response;
            this.dataSource = new MatTableDataSource(this.users);
        });
    }

    deleteUser(account: UserAccount | ServiceAccount) {
        this.userService.deleteUser(account.principalId).subscribe(() => {
            this.load();
        });
    }

    abstract getObservable(): Observable<T[]>;

    abstract getNewInstance(): T;
}
