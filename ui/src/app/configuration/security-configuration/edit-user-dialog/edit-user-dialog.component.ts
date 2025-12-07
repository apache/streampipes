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
    FormControl,
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
import { TranslateService } from '@ngx-translate/core';

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
    formAvailable = false;

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
        private translateService: TranslateService,
    ) {}

    ngOnInit(): void {
        this.initRoleFilter();
        this.loadInitialData();
        this.cloneUser();
    }

    save() {
        this.registrationError = undefined;

        const saveCallback = () => this.close(true);
        const errorCallback = (error: any) => {
            this.registrationError = error.error.notifications
                ? error.error.notifications[0].title
                : this.translateService.instant('Unknown error');
        };

        if (!this.isUserAccount || !this.isExternalProvider) {
            this.clonedUser.username = this.parentForm.get('username').value;
        }

        if (!this.isExternalProvider) {
            this.clonedUser.accountLocked =
                this.parentForm.get('accountLocked').value;
            this.clonedUser.accountEnabled =
                this.parentForm.get('accountEnabled').value;
        }

        if (this.clonedUser instanceof UserAccount) {
            this.emailChanged =
                this.clonedUser.username !== this.user.username &&
                this.user.username ===
                    this.currentUserService.getCurrentUser().username &&
                this.editMode;

            if (!this.isExternalProvider) {
                this.clonedUser.fullName =
                    this.parentForm.get('fullName').value;
            }
            if (!this.editMode) {
                if (
                    this.emailConfigured &&
                    this.parentForm.get('sendPasswordToUser').value
                ) {
                    this.sendPasswordToUser =
                        this.parentForm.get('sendPasswordToUser').value;
                } else {
                    this.clonedUser.password =
                        this.parentForm.get('password').value;
                }
            }
        } else {
            const clientSecret = this.parentForm.get('clientSecret').value;
            if (this.user.clientSecret !== clientSecret) {
                this.clonedUser.clientSecret = clientSecret;
                this.clonedUser.secretEncrypted = false;
            }
        }

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
            this.initForm();
            this.handleFormChanges();
            this.formAvailable = true;
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

        this.parentForm = this.fb.group(form, {
            validators:
                this.editMode || !this.isUserAccount
                    ? null
                    : this.checkPasswords,
        });

        if (!this.editMode && this.clonedUser instanceof UserAccount) {
            if (this.emailConfigured) {
                this.parentForm.addControl(
                    'sendPasswordToUser',
                    new FormControl(this.sendPasswordToUser),
                );
            }
            this.parentForm.addControl(
                'password',
                new FormControl(null, [Validators.required]),
            );
            this.parentForm.addControl('repeatPassword', new FormControl(null));
        }

        if (this.isExternalProvider) {
            this.parentForm.get('username')?.disable();
            this.parentForm.get('fullName')?.disable();
        }
    }

    private handleFormChanges(): void {
        this.parentForm.valueChanges.subscribe(v => {
            this.emailChanged = v.username !== this.clonedUser.username;
            if (this.clonedUser instanceof UserAccount && !this.editMode) {
                if (this.sendPasswordToUser !== v.sendPasswordToUser) {
                    this.sendPasswordToUser = v.sendPasswordToUser;
                    if (this.sendPasswordToUser) {
                        this.removePasswordControls();
                    } else {
                        this.addPasswordControlsIfMissing();
                    }
                }
            }
        });
    }

    private removePasswordControls(): void {
        const pw = this.parentForm.get('password');
        const rp = this.parentForm.get('repeatPassword');
        pw.setValue(null);
        rp.setValue(null);

        pw?.clearValidators();
        rp?.clearValidators();

        pw?.disable({ emitEvent: false });
        rp?.disable({ emitEvent: false });

        pw?.updateValueAndValidity({ emitEvent: false });
        rp?.updateValueAndValidity({ emitEvent: false });

        this.parentForm.setValidators(null);
        this.parentForm.updateValueAndValidity({ emitEvent: false });

        if (this.clonedUser instanceof UserAccount) {
            this.clonedUser.password = undefined;
        }
    }

    private addPasswordControlsIfMissing(): void {
        const pw = this.parentForm.get('password');
        const rp = this.parentForm.get('repeatPassword');

        pw?.enable({ emitEvent: false });
        rp?.enable({ emitEvent: false });

        pw?.setValidators([Validators.required]);
        rp?.setValidators([Validators.required]);

        this.parentForm.addValidators(this.checkPasswords);

        pw?.updateValueAndValidity({ emitEvent: false });
        rp?.updateValueAndValidity({ emitEvent: false });
        this.parentForm.updateValueAndValidity({ emitEvent: false });
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
