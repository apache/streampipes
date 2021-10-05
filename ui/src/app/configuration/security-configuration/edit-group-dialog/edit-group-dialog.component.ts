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
import { Group, Role } from '../../../core-model/gen/streampipes-model-client';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { UserRole } from '../../../_enums/user-role.enum';
import { DialogRef } from '../../../core-ui/dialog/base-dialog/dialog-ref';
import { UserGroupService } from '../../../platform-services/apis/user-group.service';
import { MatCheckboxChange } from '@angular/material/checkbox';

@Component({
  selector: 'sp-edit-group-dialog',
  templateUrl: './edit-group-dialog.component.html',
  styleUrls: ['./edit-group-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EditGroupDialogComponent implements OnInit {

  @Input()
  group: Group;

  @Input()
  editMode: boolean;

  parentForm: FormGroup;
  availableRoles: string[];
  clonedGroup: Group;

  constructor(private fb: FormBuilder,
              private dialogRef: DialogRef<EditGroupDialogComponent>,
              private userGroupService: UserGroupService) {}

  ngOnInit(): void {
    this.availableRoles = Object.values(UserRole).filter(value => typeof value === 'string') as string[];
    this.clonedGroup = Group.fromData(this.group, new Group());
    this.parentForm = this.fb.group({});
    this.parentForm.addControl('groupName', new FormControl(this.clonedGroup.groupName, Validators.required));

    this.parentForm.valueChanges.subscribe(v => this.clonedGroup.groupName = v.groupName);
  }

  close(refresh: boolean) {
    this.dialogRef.close(refresh);
  }

  save() {
    if (this.editMode) {
      this.userGroupService.updateGroup(this.clonedGroup).subscribe(() => this.close(true));
    } else {
      this.userGroupService.createGroup(this.clonedGroup).subscribe(() => this.close(true));
    }
  }

  changeRoleAssignment(event: MatCheckboxChange) {
    if (this.clonedGroup.roles.indexOf(event.source.value as Role) > -1) {
      this.removeRole(event.source.value);
    } else {
      this.addRole(event.source.value);
    }
  }

  removeRole(role: string) {
    this.clonedGroup.roles.splice(this.clonedGroup.roles.indexOf(role as Role), 1);
  }

  addRole(role: string) {
    this.clonedGroup.roles.push(role as Role);
  }


}
