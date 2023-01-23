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

import { Component, OnInit } from '@angular/core';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { LoginService } from '../../services/login.service';

@Component({
    selector: 'sp-restore-password',
    templateUrl: './restore-password.component.html',
    styleUrls: ['../login/login.component.scss'],
})
export class RestorePasswordComponent implements OnInit {
    parentForm: UntypedFormGroup;
    restoreSuccess = false;
    restoreCompleted = false;

    username: string;

    constructor(
        private fb: UntypedFormBuilder,
        private loginService: LoginService,
    ) {}

    ngOnInit(): void {
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'username',
            new UntypedFormControl('', Validators.required),
        );

        this.parentForm.valueChanges.subscribe(result => {
            this.username = result.username;
        });
    }

    sendRestorePasswordLink() {
        this.restoreCompleted = false;
        this.loginService.sendRestorePasswordLink(this.username).subscribe(
            response => {
                this.restoreSuccess = true;
                this.restoreCompleted = true;
            },
            error => {
                this.restoreSuccess = false;
                this.restoreCompleted = true;
            },
        );
    }
}
