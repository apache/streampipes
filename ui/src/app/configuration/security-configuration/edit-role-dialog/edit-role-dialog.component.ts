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
import {
    Privilege,
    PrivilegeService,
    Role,
    RoleService,
} from '@streampipes/platform-services';
import {
    FormBuilder,
    FormControl,
    FormGroup,
    Validators,
} from '@angular/forms';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-edit-role-dialog',
    templateUrl: './edit-role-dialog.component.html',
    styleUrls: ['./edit-role-dialog.component.scss'],
})
export class EditRoleDialogComponent implements OnInit {
    @Input()
    role: Role;

    @Input()
    editMode: boolean;

    parentForm: FormGroup;
    allPrivileges: Privilege[] = [];
    selectedPrivileges: Privilege[] = [];
    clonedRole: Role;

    constructor(
        private fb: FormBuilder,
        private privilegeService: PrivilegeService,
        private dialogRef: DialogRef<EditRoleDialogComponent>,
        private roleService: RoleService,
    ) {}

    ngOnInit() {
        this.privilegeService.findAll().subscribe(privileges => {
            this.selectedPrivileges = privileges.filter(
                p => this.role.privilegeIds.indexOf(p.elementId) > -1,
            );
            this.allPrivileges = privileges.filter(
                p =>
                    !this.selectedPrivileges.some(
                        selected => p.elementId === selected.elementId,
                    ),
            );
            this.sort(this.selectedPrivileges);
            this.sort(this.allPrivileges);
        });
        this.clonedRole = Role.fromData(this.role, new Role());
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'label',
            new FormControl(this.clonedRole.label, Validators.required),
        );
        this.parentForm.addControl(
            'elementId',
            new FormControl(this.clonedRole.elementId, [
                Validators.required,
                Validators.pattern(/^ROLE_[A-Z_]+$/),
            ]),
        );
    }

    assignPrivilege(privilege: Privilege) {
        if (!this.isAssigned(privilege)) {
            this.selectedPrivileges.push(privilege);
            this.allPrivileges = this.allPrivileges.filter(
                p => p.elementId !== privilege.elementId,
            );
            this.sort(this.selectedPrivileges);
            this.sort(this.allPrivileges);
        }
    }

    removePrivilege(privilege: Privilege) {
        this.selectedPrivileges = this.selectedPrivileges.filter(
            p => p.elementId !== privilege.elementId,
        );
        this.allPrivileges.push(privilege);
        this.sort(this.allPrivileges);
    }

    isAssigned(privilege: Privilege): boolean {
        return this.selectedPrivileges.some(
            p => p.elementId === privilege.elementId,
        );
    }

    close(refresh: boolean) {
        this.dialogRef.close(refresh);
    }

    sort(privileges: Privilege[]) {
        privileges.sort((a, b) => a.elementId.localeCompare(b.elementId));
    }

    save() {
        this.clonedRole.elementId = this.parentForm.get('elementId').value;
        this.clonedRole.label = this.parentForm.get('label').value;
        this.clonedRole.privilegeIds = this.selectedPrivileges.map(
            p => p.elementId,
        );
        if (this.editMode) {
            this.roleService
                .update(this.clonedRole)
                .subscribe(() => this.close(true));
        } else {
            this.roleService
                .create(this.clonedRole)
                .subscribe(() => this.close(true));
        }
    }
}
