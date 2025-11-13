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

import { Directive, inject, OnInit, ViewChild } from '@angular/core';
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
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { EditUserDialogComponent } from './edit-user-dialog/edit-user-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';

@Directive()
export abstract class AbstractSecurityPrincipalConfig<
    T extends UserAccount | ServiceAccount,
> implements OnInit
{
    users: T[] = [];

    @ViewChild(MatSort) sort: MatSort;

    dataSource: MatTableDataSource<T>;

    protected userService = inject(UserService);
    protected userAdminService = inject(UserAdminService);
    protected dialogService = inject(DialogService);
    private dialog = inject(MatDialog);
    private translateService = inject(TranslateService);

    ngOnInit(): void {
        this.load();
    }

    openEditDialog(user: UserAccount | ServiceAccount, editMode: boolean) {
        const dialogRef = this.dialogService.open(EditUserDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: editMode
                ? this.translateService.instant('Edit user {{user}}', {
                      user: user.username,
                  })
                : this.translateService.instant('Add user'),
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
            setTimeout(() => {
                this.dataSource.sort = this.sort;
            });
        });
    }

    deleteUser(account: UserAccount | ServiceAccount) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to delete this account?',
                ),
                subtitle: this.translateService.instant(
                    'This action cannot be reversed!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                okTitle: this.translateService.instant('Delete User'),
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.userService
                    .deleteUser(account.principalId)
                    .subscribe(() => {
                        this.load();
                    });
            }
        });
    }

    abstract getObservable(): Observable<T[]>;

    abstract getNewInstance(): T;
}
