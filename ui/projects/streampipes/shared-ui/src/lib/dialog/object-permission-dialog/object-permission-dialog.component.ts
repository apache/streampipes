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
import {
    Component,
    ElementRef,
    inject,
    Input,
    OnInit,
    ViewChild,
} from '@angular/core';
import { DialogRef } from '../base-dialog/dialog-ref';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    Group,
    Permission,
    PermissionEntry,
    PermissionsService,
    PrincipalType,
    ShortUserInfo,
    UserGroupService,
    UserService,
} from '@streampipes/platform-services';
import {
    MatChipGrid,
    MatChipInput,
    MatChipInputEvent,
    MatChipRemove,
    MatChipRow,
} from '@angular/material/chips';
import { combineLatest, Observable, shareReplay, zip } from 'rxjs';
import {
    MatAutocomplete,
    MatAutocompleteSelectedEvent,
    MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { map, startWith } from 'rxjs/operators';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { SplitSectionComponent } from '../../components/split-section/split-section.component';
import { FormFieldComponent } from '../../components/form-field/form-field.component';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { MatDivider } from '@angular/material/divider';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-object-permission-dialog',
    templateUrl: './object-permission-dialog.component.html',
    styleUrls: ['./object-permission-dialog.component.scss'],
    imports: [
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        MatProgressSpinner,
        FormsModule,
        ReactiveFormsModule,
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatSelect,
        MatOption,
        MatCheckbox,
        MatChipGrid,
        MatChipRow,
        MatChipRemove,
        MatIcon,
        MatInput,
        MatAutocompleteTrigger,
        MatChipInput,
        MatAutocomplete,
        LayoutGapDirective,
        MatIconButton,
        MatTooltip,
        CdkCopyToClipboard,
        MatDivider,
        MatButton,
        AsyncPipe,
        TranslatePipe,
    ],
})
export class ObjectPermissionDialogComponent implements OnInit {
    separatorKeysCodes: number[] = [ENTER, COMMA];

    @Input()
    objectInstanceId: string;

    @Input()
    headerTitle: string;

    @Input()
    anonymousReadSupported = false;

    @Input()
    publicLink = '';

    parentForm: UntypedFormGroup;

    permission: Permission;

    owner: ShortUserInfo;
    grantedUserAuthorities: ShortUserInfo[] = [];
    grantedGroupAuthorities: Group[] = [];

    allUsers: ShortUserInfo[];
    allGroups: Group[];

    filteredUsers$: Observable<ShortUserInfo[]>;
    filteredGroups$: Observable<Group[]>;

    loading = true;
    permissionDenied = false;

    @ViewChild('userInput') userInput: ElementRef<HTMLInputElement>;
    @ViewChild('groupInput') groupInput: ElementRef<HTMLInputElement>;
    userCtrl = new UntypedFormControl();
    groupCtrl = new UntypedFormControl();

    private fb = inject(UntypedFormBuilder);
    private dialogRef = inject(DialogRef<ObjectPermissionDialogComponent>);
    private permissionsService = inject(PermissionsService);
    private userService = inject(UserService);
    private groupService = inject(UserGroupService);

    ngOnInit(): void {
        this.loadUsersAndGroups();
        this.parentForm = this.fb.group({});
    }

    loadUsersAndGroups() {
        this.loading = true;
        zip(
            this.userService.listUsers(true),
            this.groupService.getAllUserGroups(),
            this.permissionsService.getPermissionsForObject(
                this.objectInstanceId,
            ),
        ).subscribe(
            results => {
                this.allUsers = results[0];
                this.allGroups = results[1];
                this.processPermissions(results[2]);
                this.permissionDenied = false;
                this.loading = false;
            },
            _error => {
                this.permissionDenied = true;
                this.loading = false;
            },
        );
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
            if (this.anonymousReadSupported) {
                this.parentForm.addControl(
                    'readAnonymous',
                    new UntypedFormControl(this.permission.readAnonymous),
                );
            }
            this.filteredUsers$ = combineLatest([
                this.userCtrl.valueChanges.pipe(startWith(null)),
                this.parentForm
                    .get('owner')!
                    .valueChanges.pipe(
                        startWith(this.parentForm.get('owner')!.value),
                    ),
            ]).pipe(
                map(([username]) => {
                    const base = this.allUsers.filter(
                        u => !this.isOwnerOrAdded(u),
                    );
                    return username ? this._filter(username) : base.slice();
                }),
                shareReplay({ bufferSize: 1, refCount: true }),
            );

            this.filteredGroups$ = this.groupCtrl.valueChanges.pipe(
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
        }
    }

    save() {
        const { owner, publicElement, readAnonymous } =
            this.parentForm.getRawValue();
        this.permission.publicElement = publicElement;
        if (this.anonymousReadSupported) {
            this.permission.readAnonymous = readAnonymous || false;
        }
        if (this.permission.publicElement) {
            this.permission.grantedAuthorities = [];
            this.grantedGroupAuthorities = [];
            this.grantedUserAuthorities = [];
        }
        if (owner) {
            this.permission.ownerSid = owner;
        }

        this.permission.grantedAuthorities = this.grantedUserAuthorities
            .map(u => {
                return {
                    principalType: u.principalType as PrincipalType,
                    sid: u.principalId,
                };
            })
            .concat(
                this.grantedGroupAuthorities.map(g => {
                    return { principalType: 'GROUP', sid: g.groupId };
                }),
            );
        this.permissionsService
            .updatePermission(this.permission)
            .subscribe(_result => {
                this.dialogRef.close(true);
            });
    }

    close(refresh: boolean) {
        this.dialogRef.close(refresh);
    }

    removeUser(user: ShortUserInfo) {
        const currentIndex = this.grantedUserAuthorities.findIndex(
            u => u.principalId === user.principalId,
        );
        this.grantedUserAuthorities.splice(currentIndex, 1);
        this.userCtrl.setValue(null);
    }

    removeGroup(group: Group) {
        const currentIndex = this.grantedGroupAuthorities.findIndex(
            u => u.groupId === group.groupId,
        );
        this.grantedGroupAuthorities.splice(currentIndex, 1);
        this.groupCtrl.setValue(null);
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
        if (user !== undefined) {
            this.grantedUserAuthorities.push(user);
        }
    }

    private addGroupToSelection(authority: PermissionEntry) {
        const group = this.allGroups.find(u => u.groupId === authority.sid);
        this.grantedGroupAuthorities.push(group);
    }

    private _filter(value: any): ShortUserInfo[] {
        const isUserAccount = value instanceof ShortUserInfo;
        const filterValue = isUserAccount
            ? value.email.toLowerCase()
            : value.toLowerCase();
        return this.allUsers.filter(u => {
            return (
                u.email.toLowerCase().startsWith(filterValue) &&
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

    private isOwnerOrAdded(user: ShortUserInfo): boolean {
        return (
            this.parentForm.get('owner').getRawValue() === user.principalId ||
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
