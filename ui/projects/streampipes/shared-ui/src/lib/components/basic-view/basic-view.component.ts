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
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../dialog/confirm-dialog/confirm-dialog.component';

@Component({
    selector: 'sp-basic-view',
    templateUrl: './basic-view.component.html',
    styleUrls: ['./basic-view.component.scss'],
    standalone: false,
})
export class SpBasicViewComponent {
    @Input()
    padding = false;

    @Input()
    showBackLink = false;

    @Input()
    confirmClose = false;

    @Input()
    backLinkTarget: string[];

    @Input()
    hideNavbar = false;

    constructor(
        private router: Router,
        private dialogService: MatDialog,
        private translateService: TranslateService,
    ) {}

    navigateBack() {
        console.log('Confirm close', this.confirmClose);
        if (this.confirmClose) {
            this.openConfirmationDialog();
            console.log('finsihed');
        } else {
            this.router.navigate(this.backLinkTarget);
        }
    }

    openConfirmationDialog() {
        console.log('Start Dialog');

        const dialogRef = this.dialogService.open(ConfirmDialogComponent, {
            width: '600px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to leave this page?',
                ),
                subtitle: '',

                cancelTitle: this.translateService.instant('No'),
                okTitle: this.translateService.instant('Yes'),
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            console.log('result', result);
            if (result) {
                this.router.navigate(this.backLinkTarget);
            }
        });
    }
}
