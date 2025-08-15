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
import { AvailableRolesService } from '../../../services/available-roles.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'sp-edit-user-dialog',
    templateUrl: './edit-user-dialog.component.html',
    styleUrls: ['./edit-user-dialog.component.scss'],
    encapsulation: ViewEncapsulation.None,
    standalone: false,
})
export class EditUserDialogComponent implements OnInit {
    @Input()
    user: any;

    @Input()
    editMode: boolean;

    isUserAccount: boolean;
    isExternalProvider = false;
    isExternallyManagedRoles = false;
    parentForm: UntypedFormGroup;
    clonedUser: UserAccount | ServiceAccount;

    availableRoles$: Observable<Role[]>;
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
        this.initRoleFilter();
        this.loadInitialData();
        this.cloneUser();
        this.initForm();
        this.handleFormChanges();
    }

    save() {
        this.registrationError = undefined;

        const saveCallback = () => this.close(true);
        const errorCallback = (error: any) => {
            this.registrationError = error.error.notifications
                ? error.error.notifications[0].title
                : 'Unknown error';
        };

        if (this.editMode) {
            const update$ = this.isUserAccount
                ? this.userService.updateUser(this.clonedUser as UserAccount)
                : this.userService.updateService(
                      this.clonedUser as ServiceAccount,
                  );

            update$.subscribe(() => {
                if (this.emailChanged) {
                    this.authService.logout();
                    this.close(false);
                    this.router.navigate(['login']);
                } else {
                    saveCallback();
                }
            });
        } else {
            const create$ = this.isUserAccount
                ? this.userService.createUser(this.clonedUser as UserAccount)
                : this.userService.createServiceAccount(
                      this.clonedUser as ServiceAccount,
                  );

            create$.subscribe(saveCallback, errorCallback);
        }
    }

    private initRoleFilter(): void {
        const filterRole =
            this.user instanceof UserAccount
                ? UserRole.ROLE_SERVICE_ADMIN
                : UserRole.ROLE_ADMIN;

        this.availableRoles$ = this.availableRolesService
            .getAvailableRoles()
            .pipe(
                map(roles =>
                    roles
                        .filter(role => role.elementId !== filterRole)
                        .sort((a, b) => a.label.localeCompare(b.label)),
                ),
            );
    }

    private loadInitialData(): void {
        this.mailConfigService.getMailConfig().subscribe(config => {
            this.emailConfigured = config.emailConfigured;
        });

        this.userGroupService.getAllUserGroups().subscribe(groups => {
            this.availableGroups = groups;
        });
    }

    private cloneUser(): void {
        const isUserAccount = this.user instanceof UserAccount;

        this.isUserAccount = isUserAccount;
        this.isExternalProvider =
            isUserAccount && this.user.provider !== 'local';
        this.isExternallyManagedRoles =
            isUserAccount && this.user.externallyManagedRoles;

        this.clonedUser = isUserAccount
            ? UserAccount.fromData(this.user, new UserAccount())
            : ServiceAccount.fromData(this.user, new ServiceAccount());
    }

    private initForm(): void {
        const form: Record<string, any> = {};

        form['username'] = [
            this.clonedUser.username,
            this.getUsernameValidators(),
        ];

        if (!this.isExternalProvider) {
            form['accountEnabled'] = [this.clonedUser.accountEnabled];
            form['accountLocked'] = [this.clonedUser.accountLocked];
        }

        if (this.clonedUser instanceof UserAccount) {
            form['fullName'] = [this.clonedUser.fullName];
        } else {
            form['clientSecret'] = [
                this.clonedUser.clientSecret,
                [Validators.required, Validators.minLength(35)],
            ];
        }

        if (!this.editMode && this.clonedUser instanceof UserAccount) {
            form['password'] = [this.clonedUser.password, Validators.required];
            form['repeatPassword'] = [''];
            form['sendPasswordToUser'] = [this.sendPasswordToUser];
        }

        this.parentForm = this.fb.group(form, {
            validators:
                this.editMode || !this.isUserAccount
                    ? null
                    : this.checkPasswords,
        });

        if (this.isExternalProvider) {
            this.parentForm.get('username')?.disable();
            this.parentForm.get('fullName')?.disable();
        }
    }

    private handleFormChanges(): void {
        this.parentForm.valueChanges.subscribe(v => {
            const raw = this.parentForm.getRawValue();
            if (!this.isUserAccount || !this.isExternalProvider) {
                this.clonedUser.username = v.username;
            }

            if (!this.isExternalProvider) {
                this.clonedUser.accountLocked = raw.accountLocked;
                this.clonedUser.accountEnabled = raw.accountEnabled;
            }

            if (this.clonedUser instanceof UserAccount) {
                this.emailChanged =
                    this.clonedUser.username !== this.user.username &&
                    this.user.username ===
                        this.currentUserService.getCurrentUser().username &&
                    this.editMode;

                if (!this.isExternalProvider) {
                    this.clonedUser.fullName = v.fullName;
                }

                if (!this.editMode) {
                    this.sendPasswordToUser = v.sendPasswordToUser;

                    if (this.sendPasswordToUser) {
                        this.removePasswordControls();
                    } else {
                        this.addPasswordControlsIfMissing();
                        this.clonedUser.password = v.password;
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

    private removePasswordControls(): void {
        this.parentForm.removeControl('password');
        this.parentForm.removeControl('repeatPassword');
        this.parentForm.clearValidators();
        if (this.clonedUser instanceof UserAccount) {
            this.clonedUser.password = undefined;
        }
    }

    private addPasswordControlsIfMissing(): void {
        if (!this.parentForm.get('password')) {
            this.parentForm.addControl(
                'password',
                new UntypedFormControl('', Validators.required),
            );
            this.parentForm.addControl(
                'repeatPassword',
                new UntypedFormControl(),
            );
            this.parentForm.setValidators(this.checkPasswords);
        }
    }

    private getUsernameValidators(): ValidatorFn[] {
        if (this.isUserAccount) {
            return this.user.provider === 'local'
                ? [Validators.required, Validators.email]
                : [Validators.email];
        }
        return [Validators.required];
    }

    close(refresh: boolean) {
        this.dialogRef.close(refresh);
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
        if (this.clonedUser.roles.indexOf(event.source.value) > -1) {
            this.removeRole(event.source.value);
        } else {
            this.addRole(event.source.value);
        }
    }

    removeRole(role: string) {
        this.clonedUser.roles.splice(this.clonedUser.roles.indexOf(role), 1);
    }

    addRole(role: string) {
        this.clonedUser.roles.push(role);
    }
}
