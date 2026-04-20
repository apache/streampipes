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

import { Component, Input, OnInit, inject } from '@angular/core';
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
    FormsModule,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms';
import {
    DialogRef,
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { AlternateIdConfigurationComponent } from '../alternate-id-configuration/alternate-id-configuration.component';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-role-dialog',
    templateUrl: './edit-role-dialog.component.html',
    styleUrls: ['./edit-role-dialog.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        FormsModule,
        ReactiveFormsModule,
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        MatError,
        LayoutGapDirective,
        LayoutAlignDirective,
        MatIconButton,
        MatIcon,
        AlternateIdConfigurationComponent,
        MatDivider,
        MatButton,
        TranslatePipe,
    ],
})
export class EditRoleDialogComponent implements OnInit {
    private fb = inject(FormBuilder);
    private privilegeService = inject(PrivilegeService);
    private dialogRef = inject<DialogRef<EditRoleDialogComponent>>(DialogRef);
    private roleService = inject(RoleService);

    @Input()
    role: Role;

    @Input()
    editMode: boolean;

    parentForm: FormGroup;
    allPrivileges: Privilege[] = [];
    selectedPrivileges: Privilege[] = [];
    clonedRole: Role;

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
            new FormControl(
                {
                    value: this.clonedRole.label,
                    disabled: this.clonedRole.defaultRole,
                },
                Validators.required,
            ),
        );
        this.parentForm.addControl(
            'elementId',
            new FormControl(
                {
                    value: this.clonedRole.elementId,
                    disabled: this.clonedRole.defaultRole,
                },
                [Validators.required, Validators.pattern(/^ROLE_[A-Z_]+$/)],
            ),
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
