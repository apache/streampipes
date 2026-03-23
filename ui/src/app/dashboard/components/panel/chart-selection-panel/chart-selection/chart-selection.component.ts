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

import { Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import {
    ChartService,
    DataExplorerWidgetModel,
} from '@streampipes/platform-services';
import { Router } from '@angular/router';
import { AuthService } from '../../../../../services/auth.service';
import { UserPrivilege } from '../../../../../core/auth/user-privilege.enum';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { ChartPreviewComponent } from './chart-preview/chart-preview.component';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-chart-selection',
    templateUrl: './chart-selection.component.html',
    styleUrls: ['./chart-selection.component.scss'],
    imports: [
        FlexDirective,
        LayoutGapDirective,
        LayoutDirective,
        ChartPreviewComponent,
        LayoutAlignDirective,
        MatButton,
        MatIcon,
        TranslatePipe,
    ],
})
export class ChartSelectionComponent implements OnInit {
    private authService = inject(AuthService);

    @Output()
    addChartEmitter: EventEmitter<string> = new EventEmitter();

    charts: DataExplorerWidgetModel[] = [];

    hasChartWritePrivileges: boolean = false;

    constructor(
        private dataViewService: ChartService,
        private router: Router,
    ) {}

    ngOnInit() {
        this.dataViewService.getAllCharts().subscribe(charts => {
            this.charts = charts.sort((a, b) =>
                a.baseAppearanceConfig.widgetTitle.localeCompare(
                    b.baseAppearanceConfig.widgetTitle,
                ),
            );
        });

        this.hasChartWritePrivileges = this.authService.hasRole(
            UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
        );
    }

    navigateToDataViewCreation(): void {
        this.router.navigate(['chart', 'create'], {
            queryParams: { editMode: true },
            state: { omitConfirm: true },
        });
    }
}
