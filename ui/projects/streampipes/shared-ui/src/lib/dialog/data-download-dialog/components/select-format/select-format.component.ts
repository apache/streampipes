/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Component, inject, Input, OnInit } from '@angular/core';
import { FormatExportConfig } from '../../model/format-export-config.model';
import { FileMetadata, FilesService } from '@streampipes/platform-services';
import { CurrentUserService } from '../../../../services/current-user.service';

@Component({
    selector: 'sp-select-format',
    templateUrl: './select-format.component.html',
    styleUrls: [
        './select-format.component.scss',
        '../../data-download-dialog.component.scss',
    ],
})
export class SelectFormatComponent implements OnInit {
    @Input() formatExportConfig: FormatExportConfig;

    hasReadFilePrivilege = false;
    excelTemplates: FileMetadata[] = [];

    private fileService = inject(FilesService);
    private currentUserService = inject(CurrentUserService);

    constructor() {}

    ngOnInit() {
        this.hasReadFilePrivilege = this.currentUserService.hasRole(
            'PRIVILEGE_READ_FILES',
        );
        if (this.hasReadFilePrivilege) {
            this.fileService
                .getFileMetadata(['xlsx'])
                .subscribe(excelTemplates => {
                    this.excelTemplates = excelTemplates;
                });
        }
    }
}
