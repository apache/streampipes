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

import { Component, OnInit, ViewChild } from '@angular/core';
import {
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { FileUploadDialogComponent } from './dialog/file-upload/file-upload-dialog.component';
import { SpFilesRoutes } from './files.routes';

@Component({
    templateUrl: './files.component.html',
    styleUrls: ['./files.component.scss'],
})
export class FilesComponent implements OnInit {
    @ViewChild('fileOverviewComponent') fileOverviewComponent;

    constructor(
        private dialogService: DialogService,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpFilesRoutes.BASE),
        );
    }

    openFileUploadDialog() {
        const dialogRef = this.dialogService.open(FileUploadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Upload file',
            width: '40vw',
        });

        dialogRef
            .afterClosed()
            .subscribe(() => this.fileOverviewComponent.refreshFiles());
    }
}
