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

import {
    Component,
    Input,
    OnInit,
    ViewEncapsulation,
    inject,
} from '@angular/core';
import { Group, Role, UserGroupService } from '@streampipes/platform-services';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    DialogRef,
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { AvailableRolesService } from '../../../services/available-roles.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { AlternateIdConfigurationComponent } from '../alternate-id-configuration/alternate-id-configuration.component';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-group-dialog',
    templateUrl: './edit-group-dialog.component.html',
    styleUrls: ['./edit-group-dialog.component.scss'],
    encapsulation: ViewEncapsulation.None,
    imports: [
        FlexDirective,
        LayoutDirective,
        FormsModule,
        ReactiveFormsModule,
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        MatCheckbox,
        AlternateIdConfigurationComponent,
        MatDivider,
        MatButton,
        AsyncPipe,
        TranslatePipe,
    ],
})
export class EditGroupDialogComponent implements OnInit {
    private fb = inject(UntypedFormBuilder);
    private availableRolesService = inject(AvailableRolesService);
    private dialogRef = inject<DialogRef<EditGroupDialogComponent>>(DialogRef);
    private userGroupService = inject(UserGroupService);

    @Input()
    group: Group;

    @Input()
    editMode: boolean;

    parentForm: UntypedFormGroup;
    availableRoles$: Observable<Role[]>;
    clonedGroup: Group;

    ngOnInit(): void {
        this.availableRoles$ = this.availableRolesService
            .getAvailableRoles()
            .pipe(
                map(roles =>
                    roles.sort((a, b) => a.label.localeCompare(b.label)),
                ),
            );
        this.clonedGroup = Group.fromData(this.group, new Group());
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'groupName',
            new UntypedFormControl(
                this.clonedGroup.groupName,
                Validators.required,
            ),
        );

        this.parentForm.valueChanges.subscribe(
            v => (this.clonedGroup.groupName = v.groupName),
        );
    }

    close(refresh: boolean) {
        this.dialogRef.close(refresh);
    }

    save() {
        if (this.editMode) {
            this.userGroupService
                .updateGroup(this.clonedGroup)
                .subscribe(() => this.close(true));
        } else {
            this.userGroupService
                .createGroup(this.clonedGroup)
                .subscribe(() => this.close(true));
        }
    }

    changeRoleAssignment(event: MatCheckboxChange) {
        if (this.clonedGroup.roles.indexOf(event.source.value) > -1) {
            this.removeRole(event.source.value);
        } else {
            this.addRole(event.source.value);
        }
    }

    removeRole(role: string) {
        this.clonedGroup.roles.splice(this.clonedGroup.roles.indexOf(role), 1);
    }

    addRole(role: string) {
        this.clonedGroup.roles.push(role);
    }
}
