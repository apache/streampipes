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

import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { CurrentUserService, DialogRef } from '@streampipes/shared-ui';
import {
    Group,
    MailConfigService,
    Role,
    ServiceAccount,
    UserAccount,
    UserGroupService,
    UserService,
} from '@streampipes/platform-services';
import {
    AbstractControl,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    ValidationErrors,
    ValidatorFn,
    Validators,
} from '@angular/forms';
import { UserRole } from '../../../_enums/user-role.enum';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { RoleDescription } from '../../../_models/auth.model';
import { AvailableRolesService } from '../../../services/available-roles.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-edit-user-dialog',
    templateUrl: './edit-user-dialog.component.html',
    styleUrls: ['./edit-user-dialog.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class EditUserDialogComponent implements OnInit {
    @Input()
    user: any;

    @Input()
    editMode: boolean;

    isUserAccount: boolean;
    parentForm: UntypedFormGroup;
    clonedUser: UserAccount | ServiceAccount;

    availableRoles: RoleDescription[];
    availableGroups: Group[] = [];

    registrationError: string;

    sendPasswordToUser = false;
    emailChanged = false;
    emailConfigured = false;

    constructor(
        private dialogRef: DialogRef<EditUserDialogComponent>,
        private availableRolesService: AvailableRolesService,
        private fb: UntypedFormBuilder,
        private userService: UserService,
        private userGroupService: UserGroupService,
        private currentUserService: CurrentUserService,
        private authService: AuthService,
        private router: Router,
        private mailConfigService: MailConfigService,
    ) {}

    ngOnInit(): void {
        const filterObject =
            this.user instanceof UserAccount
                ? UserRole.ROLE_SERVICE_ADMIN
                : UserRole.ROLE_ADMIN;
        this.availableRoles = this.availableRolesService.availableRoles.filter(
            role => role.role !== filterObject,
        );
        this.mailConfigService
            .getMailConfig()
            .subscribe(
                config => (this.emailConfigured = config.emailConfigured),
            );
        this.userGroupService.getAllUserGroups().subscribe(response => {
            this.availableGroups = response;
        });
        this.clonedUser =
            this.user instanceof UserAccount
                ? UserAccount.fromData(this.user, new UserAccount())
                : ServiceAccount.fromData(this.user, new ServiceAccount());
        this.isUserAccount = this.user instanceof UserAccount;
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'username',
            new UntypedFormControl(
                this.clonedUser.username,
                Validators.required,
            ),
        );
        if (this.isUserAccount) {
            this.parentForm.controls['username'].setValidators([
                Validators.required,
                Validators.email,
            ]);
        }
        this.parentForm.addControl(
            'accountEnabled',
            new UntypedFormControl(this.clonedUser.accountEnabled),
        );
        this.parentForm.addControl(
            'accountLocked',
            new UntypedFormControl(this.clonedUser.accountLocked),
        );
        if (this.clonedUser instanceof UserAccount) {
            this.parentForm.addControl(
                'fullName',
                new UntypedFormControl(this.clonedUser.fullName),
            );
        } else {
            this.parentForm.addControl(
                'clientSecret',
                new UntypedFormControl(this.clonedUser.clientSecret, [
                    Validators.required,
                    Validators.minLength(35),
                ]),
            );
        }

        if (!this.editMode && this.clonedUser instanceof UserAccount) {
            this.parentForm.addControl(
                'password',
                new UntypedFormControl(
                    this.clonedUser.password,
                    Validators.required,
                ),
            );
            this.parentForm.addControl(
                'repeatPassword',
                new UntypedFormControl(),
            );
            this.parentForm.addControl(
                'sendPasswordToUser',
                new UntypedFormControl(this.sendPasswordToUser),
            );
            this.parentForm.setValidators(this.checkPasswords);
        }

        this.parentForm.valueChanges.subscribe(v => {
            this.clonedUser.username = v.username;
            this.clonedUser.accountLocked = v.accountLocked;
            this.clonedUser.accountEnabled = v.accountEnabled;
            if (this.clonedUser instanceof UserAccount) {
                this.emailChanged =
                    this.clonedUser.username !== this.user.username &&
                    this.user.username ===
                        this.currentUserService.getCurrentUser().username &&
                    this.editMode;
                this.clonedUser.fullName = v.fullName;
                if (!this.editMode) {
                    this.sendPasswordToUser = v.sendPasswordToUser;
                    if (this.sendPasswordToUser) {
                        if (this.parentForm.controls['password']) {
                            this.parentForm.removeControl('password');
                            this.parentForm.removeControl('repeatPassword');
                            this.parentForm.removeValidators(
                                this.checkPasswords,
                            );
                            this.clonedUser.password = undefined;
                        }
                    } else {
                        if (!this.parentForm.controls['password']) {
                            this.parentForm.addControl(
                                'password',
                                new UntypedFormControl(
                                    this.clonedUser.password,
                                    Validators.required,
                                ),
                            );
                            this.parentForm.addControl(
                                'repeatPassword',
                                new UntypedFormControl(),
                            );
                            this.parentForm.setValidators(this.checkPasswords);
                        }
                        this.clonedUser.password = v.password;
                        this.parentForm.controls.password.setValidators(
                            Validators.required,
                        );
                    }
                }
            } else {
                if (this.user.clientSecret !== v.clientSecret) {
                    this.clonedUser.clientSecret = v.clientSecret;
                    this.clonedUser.secretEncrypted = false;
                }
            }
        });
    }

    checkPasswords: ValidatorFn = (
        group: AbstractControl,
    ): ValidationErrors | null => {
        const pass = group.get('password');
        const confirmPass = group.get('repeatPassword');

        if (!pass || !confirmPass) {
            return null;
        }
        return pass.value === confirmPass.value ? null : { notMatching: true };
    };

    save() {
        this.registrationError = undefined;
        if (this.editMode) {
            if (this.isUserAccount) {
                this.userService
                    .updateUser(this.clonedUser as UserAccount)
                    .subscribe(() => {
                        if (this.emailChanged) {
                            this.authService.logout();
                            this.close(false);
                            this.router.navigate(['login']);
                        } else {
                            this.close(true);
                        }
                    });
            } else {
                this.userService
                    .updateService(this.clonedUser as ServiceAccount)
                    .subscribe(() => {
                        this.close(true);
                    });
            }
        } else {
            if (this.isUserAccount) {
                this.userService
                    .createUser(this.clonedUser as UserAccount)
                    .subscribe(
                        () => {
                            this.close(true);
                        },
                        error => {
                            this.registrationError = error.error.notifications
                                ? error.error.notifications[0].title
                                : 'Unknown error';
                        },
                    );
            } else {
                this.userService
                    .createServiceAccount(this.clonedUser as ServiceAccount)
                    .subscribe(
                        () => {
                            this.close(true);
                        },
                        error => {
                            this.registrationError = error.error.notifications
                                ? error.error.notifications[0].title
                                : 'Unknown error';
                        },
                    );
            }
        }
    }

    close(refresh: boolean) {
        this.dialogRef.close(refresh);
    }

    changeGroupAssignment(event: MatCheckboxChange) {
        if (this.clonedUser.groups.indexOf(event.source.value) > -1) {
            this.clonedUser.groups.splice(
                this.clonedUser.groups.indexOf(event.source.value),
                1,
            );
        } else {
            this.clonedUser.groups.push(event.source.value);
        }
    }

    changeRoleAssignment(event: MatCheckboxChange) {
        if (this.clonedUser.roles.indexOf(event.source.value as Role) > -1) {
            this.removeRole(event.source.value);
        } else {
            this.addRole(event.source.value);
        }
    }

    removeRole(role: string) {
        this.clonedUser.roles.splice(
            this.clonedUser.roles.indexOf(role as Role),
            1,
        );
    }

    addRole(role: string) {
        this.clonedUser.roles.push(role as Role);
    }
}
