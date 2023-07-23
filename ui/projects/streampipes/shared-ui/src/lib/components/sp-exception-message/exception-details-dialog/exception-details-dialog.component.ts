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
import { SpLogMessage } from '@streampipes/platform-services';
import { DialogRef } from '../../../dialog/base-dialog/dialog-ref';

@Component({
    selector: 'sp-exception-details-dialog',
    templateUrl: './exception-details-dialog.component.html',
    styleUrls: [
        './exception-details-dialog.component.scss',
        '../../../../../../../../src/scss/sp/sp-dialog.scss',
    ],
})
export class SpExceptionDetailsDialogComponent implements OnInit {
    @Input()
    message: SpLogMessage;

    @Input()
    title: string;

    showDetails = false;

    constructor(
        private dialogRef: DialogRef<SpExceptionDetailsDialogComponent>,
    ) {}

    close() {
        this.dialogRef.close();
    }

    ngOnInit(): void {}
}
