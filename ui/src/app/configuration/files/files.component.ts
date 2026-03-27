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

import { Component, OnInit, ViewChild, inject } from '@angular/core';
import {
    DialogService,
    PanelType,
    SpBasicNavTabsComponent,
    SpBreadcrumbService,
    SplitSectionComponent,
    SpNavigationItem,
} from '@streampipes/shared-ui';
import { FileUploadDialogComponent } from '../dialog/file-upload/file-upload-dialog.component';
import { SpConfigurationTabsService } from '../configuration-tabs.service';
import { SpConfigurationRoutes } from '../configuration.breadcrumb';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { FileOverviewComponent } from './file-overview/file-overview.component';

@Component({
    templateUrl: './files.component.html',
    styleUrls: ['./files.component.scss'],
    imports: [
        SpBasicNavTabsComponent,
        SplitSectionComponent,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatButton,
        FlexDirective,
        FileOverviewComponent,
        TranslatePipe,
    ],
})
export class FilesComponent implements OnInit {
    private dialogService = inject(DialogService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private tabService = inject(SpConfigurationTabsService);
    private translateService = inject(TranslateService);

    tabs: SpNavigationItem[] = [];

    @ViewChild('fileOverviewComponent') fileOverviewComponent;

    ngOnInit() {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('files') },
        ]);
    }

    openFileUploadDialog() {
        const dialogRef = this.dialogService.open(FileUploadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Upload file'),
            width: '40vw',
        });

        dialogRef
            .afterClosed()
            .subscribe(() => this.fileOverviewComponent.refreshFiles());
    }
}
