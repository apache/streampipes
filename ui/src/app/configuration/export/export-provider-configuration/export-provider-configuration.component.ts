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
    ChangeDetectionStrategy,
    Component,
    inject,
    OnInit,
} from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
    MatTableDataSource,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    ExportProviderService,
    ExportProviderSettings,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    DialogRef,
    DialogService,
    PanelType,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { UserRole } from '../../../core/auth/user-role.enum';
import { DeleteExportProviderComponent } from '../../../dataset/dialog/delete-export-provider/delete-export-provider-dialog.component';
import { ExportProviderConnectionTestComponent } from '../../../dataset/dialog/export-provider-connection-test/export-provider-connection-test.component';
import { ExportProviderComponent } from '../../../dataset/dialog/export-provider-dialog/export-provider-dialog.component';

@Component({
    selector: 'sp-export-provider-configuration',
    templateUrl: './export-provider-configuration.component.html',
    styleUrls: ['./export-provider-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FlexDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatButton,
        MatCell,
        MatCellDef,
        MatColumnDef,
        MatHeaderCell,
        MatHeaderCellDef,
        MatHeaderRow,
        MatHeaderRowDef,
        MatIcon,
        MatIconButton,
        MatRow,
        MatRowDef,
        MatSort,
        MatSortHeader,
        MatTable,
        MatTooltip,
        SplitSectionComponent,
        TranslatePipe,
    ],
})
export class ExportProviderConfigurationComponent implements OnInit {
    private currentUserService = inject(CurrentUserService);
    private dialogService = inject(DialogService);
    private exportProviderService = inject(ExportProviderService);
    private translateService = inject(TranslateService);

    availableExportProviders: ExportProviderSettings[] = [];
    dataSource = new MatTableDataSource<ExportProviderSettings>([]);
    displayedColumns: string[] = [
        'providertype',
        'endpoint',
        'bucket',
        'editExportProvider',
        'delete',
        'test',
    ];
    isAdmin = false;

    ngOnInit(): void {
        this.isAdmin = this.currentUserService
            .getCurrentUser()
            .roles.includes(UserRole.ROLE_ADMIN);

        if (this.isAdmin) {
            this.loadExportProviders();
        }
    }

    loadExportProviders(): void {
        this.availableExportProviders = [];
        this.exportProviderService
            .getAllExportProviders()
            .subscribe(exportProviders => {
                this.availableExportProviders = exportProviders;
                this.dataSource.data = exportProviders;
            });
    }

    createExportProvider(provider: ExportProviderSettings | null): void {
        const dialogRef: DialogRef<ExportProviderComponent> =
            this.dialogService.open(ExportProviderComponent, {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('New Export Provider'),
                width: '50vw',
                data: { provider },
            });

        dialogRef.afterClosed().subscribe(() => this.loadExportProviders());
    }

    deleteExportProvider(providerId: string): void {
        const dialogRef: DialogRef<DeleteExportProviderComponent> =
            this.dialogService.open(DeleteExportProviderComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Delete Export Provider'),
                width: '70vw',
                data: { providerId },
            });

        dialogRef.afterClosed().subscribe(changed => {
            if (changed) {
                this.loadExportProviders();
            }
        });
    }

    testExportProvider(providerId: string): void {
        const dialogRef: DialogRef<ExportProviderConnectionTestComponent> =
            this.dialogService.open(ExportProviderConnectionTestComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant(
                    'Test Export Provider Connection',
                ),
                width: '70vw',
                data: { providerId },
            });

        dialogRef.afterClosed().subscribe(changed => {
            if (changed) {
                this.loadExportProviders();
            }
        });
    }
}
