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

import { Component, Input } from '@angular/core';
import { SpLogMessage } from '@streampipes/platform-services';
import { DialogService } from '../../dialog/base-dialog/base-dialog.service';
import { PanelType } from '../../dialog/base-dialog/base-dialog.model';
import { SpExceptionDetailsDialogComponent } from './exception-details-dialog/exception-details-dialog.component';

@Component({
    selector: 'sp-exception-message',
    templateUrl: './sp-exception-message.component.html',
    styleUrls: ['./sp-exception-message.component.scss'],
})
export class SpExceptionMessageComponent {
    @Input()
    level = 'error';

    @Input()
    showDetails = true;

    @Input()
    message: SpLogMessage;

    @Input()
    messageTimestamp: number;

    constructor(private dialogService: DialogService) {}

    openDetailsDialog() {
        this.dialogService.open(SpExceptionDetailsDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            width: '80vw',
            title: 'Error Details',
            data: {
                message: this.message,
            },
        });
    }
}
