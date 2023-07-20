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

import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    PermissionsService,
    Group,
    Permission,
    PermissionEntry,
    ServiceAccount,
    UserAccount,
    UserService,
    UserGroupService,
    UserAdminService,
} from '@streampipes/platform-services';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable, zip } from 'rxjs';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { map, startWith } from 'rxjs/operators';

@Component({
    selector: 'sp-object-permission-dialog',
    templateUrl: './object-permission-dialog.component.html',
    styleUrls: ['./object-permission-dialog.component.scss'],
})
export class ObjectPermissionDialogComponent implements OnInit {
    separatorKeysCodes: number[] = [ENTER, COMMA];

    @Input()
    objectInstanceId: string;

    @Input()
    headerTitle: string;

    parentForm: UntypedFormGroup;

    permission: Permission;

    owner: UserAccount | ServiceAccount;
    grantedUserAuthorities: (UserAccount | ServiceAccount)[];
    grantedGroupAuthorities: Group[];

    allUsers: (UserAccount | ServiceAccount)[];
    allGroups: Group[];

    filteredUsers: Observable<(UserAccount | ServiceAccount)[]>;
    filteredGroups: Observable<Group[]>;

    usersLoaded = false;

    @ViewChild('userInput') userInput: ElementRef<HTMLInputElement>;
    @ViewChild('groupInput') groupInput: ElementRef<HTMLInputElement>;
    userCtrl = new UntypedFormControl();
    groupCtrl = new UntypedFormControl();

    constructor(
        private fb: UntypedFormBuilder,
        private dialogRef: DialogRef<ObjectPermissionDialogComponent>,
        private permissionsService: PermissionsService,
        private userService: UserService,
        private userAdminService: UserAdminService,
        private groupService: UserGroupService,
    ) {
        this.grantedGroupAuthorities = [];
        this.grantedUserAuthorities = [];
    }

    ngOnInit(): void {
        this.loadUsersAndGroups();
        this.parentForm = this.fb.group({});
        this.parentForm.valueChanges.subscribe(v => {
            this.permission.publicElement = v.publicElement;
            if (v.publicElement) {
                this.permission.grantedAuthorities = [];
                this.grantedGroupAuthorities = [];
                this.grantedUserAuthorities = [];
            }
            if (v.owner) {
                this.permission.ownerSid = v.owner;
            }
        });
    }

    loadUsersAndGroups() {
        zip(
            this.userAdminService.getAllUserAccounts(),
            this.userAdminService.getAllServiceAccounts(),
            this.groupService.getAllUserGroups(),
            this.permissionsService.getPermissionsForObject(
                this.objectInstanceId,
            ),
        ).subscribe(results => {
            this.allUsers = results[0];
            this.allUsers.concat(results[1]);
            this.allGroups = results[2];
            this.processPermissions(results[3]);
            this.usersLoaded = true;
        });
    }

    processPermissions(permissions: Permission[]) {
        if (permissions.length > 0) {
            this.permission = permissions[0];
            this.parentForm.addControl(
                'publicElement',
                new UntypedFormControl(
                    this.permission.publicElement,
                    Validators.required,
                ),
            );
            this.parentForm.addControl(
                'owner',
                new UntypedFormControl(
                    this.permission.ownerSid,
                    Validators.required,
                ),
            );
            this.filteredUsers = this.userCtrl.valueChanges.pipe(
                startWith(null),
                map((username: string | null) => {
                    return username
                        ? this._filter(username)
                        : this.allUsers
                              .filter(u => !this.isOwnerOrAdded(u))
                              .slice();
                }),
            );

            this.filteredGroups = this.groupCtrl.valueChanges.pipe(
                startWith(null),
                map((groupName: string | null) => {
                    return groupName
                        ? this._filterGroup(groupName)
                        : this.allGroups
                              .filter(g => !this.isGroupAdded(g))
                              .slice();
                }),
            );

            this.permission.grantedAuthorities.forEach(authority => {
                if (authority.principalType === 'GROUP') {
                    this.addGroupToSelection(authority);
                } else {
                    this.addUserToSelection(authority);
                }
            });
        } else {
            console.log('No permission entry found for item');
        }
    }

    save() {
        this.permission.grantedAuthorities = this.grantedUserAuthorities
            .map(u => {
                return { principalType: u.principalType, sid: u.principalId };
            })
            .concat(
                this.grantedGroupAuthorities.map(g => {
                    return { principalType: 'GROUP', sid: g.groupId };
                }),
            );
        this.permissionsService
            .updatePermission(this.permission)
            .subscribe(result => {
                this.dialogRef.close(true);
            });
    }

    close(refresh: boolean) {
        this.dialogRef.close(refresh);
    }

    removeUser(user: UserAccount | ServiceAccount) {
        const currentIndex = this.grantedUserAuthorities.findIndex(
            u => u.principalId === user.principalId,
        );
        this.grantedUserAuthorities.splice(currentIndex, 1);
    }

    removeGroup(group: Group) {
        const currentIndex = this.grantedGroupAuthorities.findIndex(
            u => u.groupId === group.groupId,
        );
        this.grantedGroupAuthorities.splice(currentIndex, 1);
    }

    addUser(event: MatChipInputEvent) {
        event.chipInput.clear();
        this.userCtrl.setValue(null);
    }

    addGroup(event: MatChipInputEvent) {
        event.chipInput.clear();
        this.groupCtrl.setValue(null);
    }

    userSelected(event: MatAutocompleteSelectedEvent) {
        this.grantedUserAuthorities.push(event.option.value);
        this.userInput.nativeElement.value = '';
        this.userCtrl.setValue(null);
    }

    groupSelected(event: MatAutocompleteSelectedEvent) {
        this.grantedGroupAuthorities.push(event.option.value);
        this.groupInput.nativeElement.value = '';
        this.groupCtrl.setValue(null);
    }

    private addUserToSelection(authority: PermissionEntry) {
        const user = this.allUsers.find(u => u.principalId === authority.sid);
        this.grantedUserAuthorities.push(user);
    }

    private addGroupToSelection(authority: PermissionEntry) {
        const group = this.allGroups.find(u => u.groupId === authority.sid);
        this.grantedGroupAuthorities.push(group);
    }

    private _filter(value: any): (UserAccount | ServiceAccount)[] {
        const isUserAccount =
            value instanceof UserAccount || value instanceof ServiceAccount;
        const filterValue = isUserAccount
            ? value.username.toLowerCase()
            : value.toLowerCase();
        return this.allUsers.filter(u => {
            return (
                u.username.toLowerCase().startsWith(filterValue) &&
                !this.isOwnerOrAdded(u)
            );
        });
    }

    private _filterGroup(value: any): Group[] {
        const isGroup = value instanceof Group;
        const filterValue = isGroup
            ? value.groupName.toLowerCase()
            : value.toLowerCase();
        return this.allGroups.filter(g => {
            return (
                g.groupName.toLowerCase().startsWith(filterValue) &&
                !this.isGroupAdded(g)
            );
        });
    }

    private isOwnerOrAdded(user: UserAccount | ServiceAccount): boolean {
        return (
            this.permission.ownerSid === user.principalId ||
            this.grantedUserAuthorities.find(
                authority => authority.principalId === user.principalId,
            ) !== undefined
        );
    }

    private isGroupAdded(group: Group): boolean {
        return (
            this.grantedGroupAuthorities.find(
                authority => authority.groupId === group.groupId,
            ) !== undefined
        );
    }
}
