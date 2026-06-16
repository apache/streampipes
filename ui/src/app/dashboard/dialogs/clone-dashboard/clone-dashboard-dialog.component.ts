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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    ChartService,
    ClientDashboardItem,
    CompositeDashboard,
    Dashboard,
    DashboardService,
    DataExplorerWidgetModel,
} from '@streampipes/platform-services';
import { DialogRef, FormFieldComponent } from '@streampipes/shared-ui';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';
import { Observable, zip } from 'rxjs';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatDivider } from '@angular/material/divider';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

export interface WidgetClone {
    current: DataExplorerWidgetModel;
    cloned: DataExplorerWidgetModel;
}

@Component({
    selector: 'sp-clone-dashboard-dialog-component',
    templateUrl: './clone-dashboard-dialog.component.html',
    styleUrls: ['./clone-dashboard-dialog.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        MatFormField,
        MatInput,
        FormsModule,
        MatError,
        MatCheckbox,
        MatAccordion,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle,
        MatDivider,
        LayoutGapDirective,
        MatButton,
        LayoutAlignDirective,
        MatProgressSpinner,
        TranslatePipe,
        FormFieldComponent,
    ],
})
export class CloneDashboardDialogComponent implements OnInit {
    private dialogRef = inject(DialogRef<CloneDashboardDialogComponent>);
    private dashboardService = inject(DashboardService);
    private chartService = inject(ChartService);
    private idGeneratorService = inject(IdGeneratorService);
    private translate = inject(TranslateService);

    static readonly DashboardPrefix = 'sp:dashboardmodel:';
    static readonly ChartPrefix = 'sp:dataexplorerwidgetmodel:';

    @Input()
    dashboard: Dashboard;

    compositeDashboard: CompositeDashboard;
    widgetConfigs: WidgetClone[] = [];

    form;

    ngOnInit() {
        this.dashboardService
            .getCompositeDashboard(this.dashboard.elementId)
            .subscribe(res => {
                this.compositeDashboard = res.body as CompositeDashboard;
                this.widgetConfigs = this.compositeDashboard.widgets.map(w => {
                    return {
                        current: w,
                        cloned: JSON.parse(JSON.stringify(w)),
                    };
                });
            });
        this.form = {
            name: `${this.dashboard.name} (${this.translate.instant('Copy')})`,
            description: this.dashboard.description,
            deepClone: false,
            allowWidgetEdits: false,
        };
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    onSave(): void {
        let widget$: Observable<any>[] = [];
        const clonedDashboard: Dashboard = JSON.parse(
            JSON.stringify(this.dashboard),
        );
        const clonedWidgets = this.widgetConfigs.map(wc =>
            this.form.allowWidgetEdits ? wc.cloned : wc.current,
        );
        clonedDashboard.elementId = this.idGeneratorService.generateWithPrefix(
            CloneDashboardDialogComponent.DashboardPrefix,
            6,
        );
        clonedDashboard.rev = undefined;
        clonedDashboard.metadata.createdAtEpochMs = Date.now();
        clonedDashboard.metadata.lastModifiedEpochMs = Date.now();
        clonedDashboard.name = this.form.name;
        clonedDashboard.description = this.form.description;
        if (this.form.deepClone) {
            const widgetCloneMap = new Map<string, DataExplorerWidgetModel>();

            clonedDashboard.widgets.forEach(widget => {
                this.remapWidgetReference(
                    widget,
                    clonedWidgets,
                    widgetCloneMap,
                    this.form.allowWidgetEdits,
                );
            });

            widget$ = Array.from(widgetCloneMap.values()).map(w =>
                this.chartService.saveChart(w),
            );
        }
        zip([
            ...widget$,
            this.dashboardService.saveDashboard(clonedDashboard),
        ]).subscribe(() => {
            this.dialogRef.close(true);
        });
    }

    private remapWidgetReference(
        dashboardWidget: ClientDashboardItem,
        clonedWidgets: DataExplorerWidgetModel[],
        widgetCloneMap: Map<string, DataExplorerWidgetModel>,
        keepWidgetTitle: boolean,
    ): void {
        const sourceWidgetId = dashboardWidget.dataViewElementId;
        if (widgetCloneMap.has(sourceWidgetId)) {
            dashboardWidget.dataViewElementId =
                widgetCloneMap.get(sourceWidgetId).elementId;
            return;
        }

        const clonedWidget = clonedWidgets.find(
            widget => widget.elementId === sourceWidgetId,
        );
        if (clonedWidget === undefined) {
            return;
        }

        clonedWidget.elementId = this.idGeneratorService.generateWithPrefix(
            CloneDashboardDialogComponent.ChartPrefix,
            6,
        );
        clonedWidget.metadata.createdAtEpochMs = Date.now();
        clonedWidget.metadata.lastModifiedEpochMs = Date.now();
        if (!keepWidgetTitle) {
            clonedWidget.baseAppearanceConfig.widgetTitle = `${clonedWidget.baseAppearanceConfig.widgetTitle} (${this.translate.instant('Copy')})`;
        }
        clonedWidget.rev = undefined;

        widgetCloneMap.set(sourceWidgetId, clonedWidget);
        dashboardWidget.dataViewElementId = clonedWidget.elementId;
    }
}
