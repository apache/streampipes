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
import { MatStepLabel } from '@angular/material/stepper';
import { SplitSectionComponent } from '../../../../components/split-section/split-section.component';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-select-format',
    templateUrl: './select-format.component.html',
    styleUrls: [
        './select-format.component.scss',
        '../../data-download-dialog.component.scss',
    ],
    imports: [
        MatStepLabel,
        SplitSectionComponent,
        MatRadioGroup,
        FormsModule,
        MatRadioButton,
        LayoutDirective,
        MatCheckbox,
        MatFormField,
        MatSelect,
        MatOption,
        MatLabel,
        MatInput,
        TranslatePipe,
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
