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

import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { SpConfigurationTabsService } from '../configuration-tabs.service';
import { LabelsService, SpLabel } from '@streampipes/platform-services';
import { SpConfigurationRoutes } from '../configuration.breadcrumb';
import {
    SpBasicNavTabsComponent,
    SpBreadcrumbService,
    SpLabelComponent,
    SplitSectionComponent,
    SpNavigationItem,
    SpTableComponent,
} from '@streampipes/shared-ui';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { SpEditLabelComponent } from './edit-label/edit-label.component';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-label-configuration',
    templateUrl: './label-configuration.component.html',
    styleUrls: ['./label-configuration.component.scss'],
    imports: [
        SpBasicNavTabsComponent,
        LayoutDirective,
        SplitSectionComponent,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatButton,
        SpEditLabelComponent,
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        SpLabelComponent,
        MatIconButton,
        MatTooltip,
        MatIcon,
        TranslatePipe,
    ],
})
export class SpLabelConfigurationComponent implements OnInit {
    private breadcrumbService = inject(SpBreadcrumbService);
    private labelsService = inject(LabelsService);
    private tabService = inject(SpConfigurationTabsService);

    tabs: SpNavigationItem[] = [];

    allLabels: SpLabel[] = [];
    readonly createLabelMode = signal(false);

    dataSource: MatTableDataSource<SpLabel> = new MatTableDataSource<SpLabel>();

    @ViewChild(MatSort)
    sort: MatSort;

    displayedColumns = ['name', 'description', 'actions'];
    labelsinUse: string[] = [];

    readonly editedLabels = signal<string[]>([]);

    ngOnInit(): void {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('labels') },
        ]);
        this.reloadLabels();
    }

    reloadLabels(): void {
        this.labelsService.getAllLabels().subscribe(res => {
            this.allLabels = res;
            this.dataSource.data = this.allLabels;
            setTimeout(() => {
                this.dataSource.sort = this.sort;
            });
        });
        this.labelsService.getLabelsInUse().subscribe(labelsInUse => {
            this.labelsinUse = labelsInUse;
        });
    }

    saveLabel(label: SpLabel): void {
        this.labelsService.addLabel(label).subscribe(() => {
            this.createLabelMode.set(false);
            this.reloadLabels();
        });
    }

    updateLabel(label: SpLabel): void {
        this.labelsService.updateLabel(label).subscribe(() => {
            this.removeEditedLabel(label._id);
            this.reloadLabels();
        });
    }

    deleteLabel(label: SpLabel): void {
        this.labelsService.deleteLabel(label._id, label._rev).subscribe(() => {
            this.reloadLabels();
        });
    }

    removeEditedLabel(labelId: string): void {
        this.editedLabels.update(labels => labels.filter(id => id !== labelId));
    }

    isEditMode(labelId: string): boolean {
        return this.editedLabels().includes(labelId);
    }

    addEditedLabel(labelId: string): void {
        this.editedLabels.update(labels =>
            labels.includes(labelId) ? labels : [...labels, labelId],
        );
    }
}
