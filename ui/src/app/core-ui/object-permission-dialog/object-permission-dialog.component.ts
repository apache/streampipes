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
import { DialogRef } from '../dialog/base-dialog/dialog-ref';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { PermissionsService } from '../../platform-services/apis/permissions.service';
import {
  Group,
  Permission,
  PermissionEntry,
  ServiceAccount,
  UserAccount
} from '../../core-model/gen/streampipes-model-client';
import { UserService } from '../../platform-services/apis/user.service';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable, zip } from 'rxjs';
import { UserGroupService } from '../../platform-services/apis/user-group.service';
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

  parentForm: FormGroup;

  permission: Permission;

  owner: UserAccount | ServiceAccount;
  grantedUserAuthorities: (UserAccount | ServiceAccount)[];
  grantedGroupAuthorities: Group[];

  allUsers: (UserAccount | ServiceAccount)[];
  allGroups: Group[];

  filteredUsers: Observable<(UserAccount | ServiceAccount)[]>;

  usersLoaded = false;

  @ViewChild('userInput') fruitInput: ElementRef<HTMLInputElement>;
  userCtrl = new FormControl();


  constructor(
      private fb: FormBuilder,
      private dialogRef: DialogRef<ObjectPermissionDialogComponent>,
      private permissionsService: PermissionsService,
      private userService: UserService,
      private groupService: UserGroupService
  ) {
    this.grantedGroupAuthorities = [];
    this.grantedUserAuthorities = [];
  }

  ngOnInit(): void {
    this.loadUsersAndGroups();
    this.parentForm = this.fb.group({});

    this.parentForm.valueChanges.subscribe(v => {
      this.permission.publicElement = v.publicElement;
      //this.permission.ownerSid = v.owner;
    });
  }

  loadUsersAndGroups() {
    zip(this.userService.getAllUserAccounts(),
        this.userService.getAllServiceAccounts(),
        this.groupService.getAllUserGroups(),
        this.permissionsService.getPermissionsForObject(this.objectInstanceId))
        .subscribe(results => {
          this.allUsers = results[0];
          this.allUsers.concat(results[1]);
          this.allGroups = results[2];
          this.processPermissions(results[3]);
          this.usersLoaded = true;
          console.log(this.allUsers);
        });
  }

  processPermissions(permissions: Permission[]) {
    if (permissions.length > 0) {
      this.permission = permissions[0];
      this.parentForm.addControl('publicElement', new FormControl(this.permission.publicElement, Validators.required));
      this.parentForm.addControl('owner', new FormControl(this.permission.ownerSid, Validators.required));
      this.parentForm.addControl('userSelection', new FormControl(this.permission.ownerSid));
      this.filteredUsers = this.userCtrl.valueChanges.pipe(
          startWith(null),
          map((fruit: UserAccount | ServiceAccount | null) => fruit ? this._filter(fruit) : this.allUsers.slice()));

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

  }

  close(refresh: boolean) {
    this.dialogRef.close(refresh);
  }

  removeUser(user: UserAccount | ServiceAccount) {
    const currentIndex = this.grantedUserAuthorities.findIndex(u => u.principalId === user.principalId);
    this.grantedUserAuthorities.splice(currentIndex, 1);
  }

  addUser(event: MatChipInputEvent) {
    event.chipInput.clear();
  }

  userSelected(event: MatAutocompleteSelectedEvent) {
    this.grantedUserAuthorities.push(event.option.value);
  }

  private addUserToSelection(authority: PermissionEntry) {
    const user = this.allUsers.find(u => u.principalId === authority.sid);
    this.grantedUserAuthorities.push(user);
  }

  private addGroupToSelection(authority: PermissionEntry) {

  }

  private _filter(value: any): (UserAccount | ServiceAccount)[] {
    const filterValue = value.toLowerCase();
    return this.allUsers.filter(u => u.username.toLowerCase().startsWith(filterValue));
  }
}
