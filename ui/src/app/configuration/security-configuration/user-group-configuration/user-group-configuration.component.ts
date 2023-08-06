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
import { Group, UserGroupService } from '@streampipes/platform-services';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { EditGroupDialogComponent } from '../edit-group-dialog/edit-group-dialog.component';

@Component({
    selector: 'sp-security-user-group-config',
    templateUrl: './user-group-configuration.component.html',
    styleUrls: ['./user-group-configuration.component.scss'],
})
export class SecurityUserGroupConfigComponent implements OnInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSize = 1;
    @ViewChild(MatSort) sort: MatSort;

    dataSource: MatTableDataSource<Group>;

    displayedColumns: string[] = ['groupName', 'edit'];

    constructor(
        private userGroupService: UserGroupService,
        private dialogService: DialogService,
    ) {}

    ngOnInit(): void {
        this.loadAllGroups();
    }

    createGroup() {
        const group = new Group();
        group.roles = [];
        this.openGroupEditDialog(group, false);
    }

    loadAllGroups() {
        this.userGroupService.getAllUserGroups().subscribe(response => {
            this.dataSource = new MatTableDataSource(response);
        });
    }

    deleteGroup(group: Group) {
        this.userGroupService.deleteGroup(group).subscribe(response => {
            this.loadAllGroups();
        });
    }

    editGroup(group: Group) {
        this.openGroupEditDialog(group, true);
    }

    openGroupEditDialog(group: Group, editMode: boolean) {
        const dialogRef = this.dialogService.open(EditGroupDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: editMode ? 'Edit group ' + group.groupName : 'Add group',
            width: '50vw',
            data: {
                group: group,
                editMode: editMode,
            },
        });

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.loadAllGroups();
            }
        });
    }
}
