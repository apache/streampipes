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

import { Component, OnInit, ViewChild } from '@angular/core';
import { Role, RoleService } from '@streampipes/platform-services';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { EditRoleDialogComponent } from '../edit-role-dialog/edit-role-dialog.component';

@Component({
    selector: 'sp-security-role-config',
    templateUrl: './role-configuration.component.html',
    styleUrls: ['./role-configuration.component.scss'],
})
export class SecurityRoleConfigComponent implements OnInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    availableRoles: Role[] = [];
    dataSource: MatTableDataSource<Role>;

    displayedColumns: string[] = ['roleName', 'roleType', 'edit'];

    constructor(
        private roleService: RoleService,
        private dialogService: DialogService,
        private dialog: MatDialog,
    ) {}

    ngOnInit(): void {
        this.loadRoles();
    }

    loadRoles(): void {
        this.roleService.findAll().subscribe(roles => {
            this.availableRoles = roles;
            this.dataSource = new MatTableDataSource(roles);
        });
    }

    createRole() {
        const role = new Role();
        role.privilegeIds = [];
        this.openRoleDialog(role, false);
    }

    deleteRole(role: Role) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: 'Are you sure you want to delete this role?',
                subtitle: 'This action cannot be reversed!',
                cancelTitle: 'Cancel',
                okTitle: 'Delete Role',
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.roleService.delete(role).subscribe(response => {
                    this.loadRoles();
                });
            }
        });
    }

    editRole(role: Role) {
        this.openRoleDialog(role, true);
    }

    openRoleDialog(role: Role, editMode: boolean) {
        const dialogRef = this.dialogService.open(EditRoleDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: editMode ? 'Edit role ' + role.label : 'Add role',
            width: '50vw',
            data: {
                role: role,
                editMode: editMode,
            },
        });

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.loadRoles();
            }
        });
    }
}
