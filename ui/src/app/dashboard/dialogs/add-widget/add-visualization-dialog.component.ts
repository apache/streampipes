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
    AfterViewInit,
    ChangeDetectorRef,
    Component,
    Input,
    OnInit,
} from '@angular/core';
import { ElementIconText } from '../../../services/get-element-icon-text.service';
import { WidgetConfigBuilder } from '../../registry/widget-config-builder';
import { WidgetRegistry } from '../../registry/widget-registry';
import { MappingPropertyGenerator } from '../../sdk/matching/mapping-property-generator';
import {
    Dashboard,
    DashboardService,
    DashboardWidgetModel,
    DashboardWidgetSettings,
    DataLakeMeasure,
    DatalakeRestService,
    DataViewDataExplorerService,
    EventPropertyUnion,
    EventSchema,
    FreeTextStaticProperty,
    MappingPropertyNary,
    MappingPropertyUnary,
    PipelineService,
} from '@streampipes/platform-services';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { DialogRef } from '@streampipes/shared-ui';
import { zip } from 'rxjs';

@Component({
    selector: 'sp-add-visualization-dialog-component',
    templateUrl: './add-visualization-dialog.component.html',
    styleUrls: ['./add-visualization-dialog.component.scss'],
})
export class AddVisualizationDialogComponent implements OnInit, AfterViewInit {
    pages = [
        {
            type: 'select-pipeline',
            title: 'Select Pipeline',
            description: "Select a pipeline you'd like to visualize",
        },
        {
            type: 'select-widget',
            title: 'Select Widget',
            description: 'Select widget',
        },
        {
            type: 'configure-widget',
            title: 'Configure Widget',
            description: 'Configure widget',
        },
    ];

    visualizablePipelines: DataLakeMeasure[] = [];
    availableWidgets: DashboardWidgetSettings[];

    selectedPipeline: DataLakeMeasure;
    selectedWidget: DashboardWidgetSettings;

    dashboard: Dashboard;

    selectedType: any;
    page = 'select-pipeline';
    dialogTitle: string;

    parentForm: UntypedFormGroup;

    formValid = false;
    viewInitialized = false;

    @Input()
    pipeline: DataLakeMeasure;

    @Input()
    widget: DashboardWidgetModel;

    @Input()
    editMode: boolean;

    @Input()
    startPage: string;

    constructor(
        private dialogRef: DialogRef<AddVisualizationDialogComponent>,
        private dashboardService: DashboardService,
        private dataLakeRestService: DatalakeRestService,
        private dataExplorerService: DataViewDataExplorerService,
        private pipelineService: PipelineService,
        public elementIconText: ElementIconText,
        private fb: UntypedFormBuilder,
        private changeDetectorRef: ChangeDetectorRef,
    ) {}

    ngOnInit() {
        this.parentForm = this.fb.group({});
        this.parentForm.statusChanges.subscribe(status => {
            this.formValid = this.viewInitialized && this.parentForm.valid;
        });
        if (!this.editMode) {
            this.dialogTitle = 'Add widget';
            this.loadVisualizablePipelines();
        } else {
            this.loadVisualizablePipelines();
            this.dialogTitle = 'Edit widget';
            this.selectedPipeline = this.pipeline;
            this.selectedWidget = this.widget.dashboardWidgetSettings;
            this.page = this.startPage;
        }
    }

    ngAfterViewInit() {
        this.viewInitialized = true;
        this.formValid = this.viewInitialized && this.parentForm.valid;
        this.changeDetectorRef.detectChanges();
    }

    loadVisualizablePipelines() {
        zip(
            this.dataExplorerService.getAllPersistedDataStreams(),
            this.dataLakeRestService.getAllMeasurementSeries(),
        ).subscribe(res => {
            const availableVisualizablePipelines = [];
            const visualizablePipelines = res[0];
            visualizablePipelines.forEach(p => {
                const measurement = res[1].find(m => {
                    return m.measureName === p.measureName;
                });
                if (measurement) {
                    p.eventSchema = measurement.eventSchema;
                    availableVisualizablePipelines.push(p);
                }
            });
            this.visualizablePipelines = this.sortPipeline(
                availableVisualizablePipelines,
            );
        });
    }

    sortPipeline(visualizations: DataLakeMeasure[]): DataLakeMeasure[] {
        return visualizations.sort((a, b) => {
            if (a.pipelineName === b.pipelineName) {
                return a.measureName.toLowerCase() < b.measureName.toLowerCase()
                    ? -1
                    : 1;
            } else {
                return a.pipelineName.toLowerCase() <
                    b.pipelineName.toLowerCase()
                    ? -1
                    : 1;
            }
        });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    getSelectedPipelineCss(vis) {
        return this.getSelectedCss(this.selectedPipeline, vis);
    }

    getSelectedVisTypeCss(type) {
        return this.getSelectedCss(this.selectedType, type);
    }

    getSelectedCss(selected, current) {
        if (selected === current) {
            return 'wizard-preview wizard-preview-selected';
        } else {
            return 'wizard-preview';
        }
    }

    getTabCss(page) {
        if (page === this.page) {
            return 'md-fab md-accent';
        } else {
            return 'md-fab md-accent wizard-inactive';
        }
    }

    selectPipeline(vis) {
        this.selectedPipeline = vis;
        this.next();
    }

    selectWidget(widget) {
        this.selectedWidget = widget;
        this.selectedWidget.config.forEach(sp => {
            if (
                sp instanceof MappingPropertyUnary ||
                sp instanceof MappingPropertyNary
            ) {
                const requirement: EventPropertyUnion = this.findRequirement(
                    this.selectedWidget.requiredSchema,
                    sp.internalName,
                );
                sp.mapsFromOptions = new MappingPropertyGenerator(
                    requirement,
                    this.selectedPipeline.eventSchema.eventProperties,
                ).computeMatchingProperties();
            }
            if (
                sp instanceof FreeTextStaticProperty &&
                sp.internalName === WidgetConfigBuilder.TITLE_KEY
            ) {
                sp.value = this.selectedPipeline.measureName;
            }
        });
        this.next();
    }

    findRequirement(requiredSchema: EventSchema, internalName: string) {
        return requiredSchema.eventProperties.find(
            ep => ep.runtimeName === internalName,
        );
    }

    loadAvailableWidgets() {
        this.availableWidgets = WidgetRegistry.getCompatibleWidgetTemplates(
            this.selectedPipeline,
        );
        this.availableWidgets.sort((a, b) => {
            return a.widgetLabel < b.widgetLabel ? -1 : 1;
        });
    }

    next() {
        if (this.page === 'select-pipeline') {
            this.loadAvailableWidgets();
            this.page = 'select-widget';
        } else if (this.page === 'select-widget') {
            this.page = 'configure-widget';
        } else {
            const configuredWidget: DashboardWidgetModel =
                new DashboardWidgetModel();
            configuredWidget['@class'] =
                'org.apache.streampipes.model.dashboard.DashboardWidgetModel';
            configuredWidget.dashboardWidgetSettings = this.selectedWidget;
            configuredWidget.dashboardWidgetSettings['@class'] =
                'org.apache.streampipes.model.dashboard.DashboardWidgetSettings';
            configuredWidget.visualizationName =
                this.selectedPipeline.measureName;
            configuredWidget.pipelineId = this.selectedPipeline.pipelineId;
            configuredWidget.widgetType =
                configuredWidget.dashboardWidgetSettings.widgetName;
            if (!this.editMode) {
                this.dashboardService
                    .saveWidget(configuredWidget)
                    .subscribe(response => {
                        this.dialogRef.close(response);
                    });
            } else {
                configuredWidget._id = this.widget._id;
                configuredWidget._rev = this.widget._rev;
                configuredWidget.widgetId = this.widget.widgetId;
                this.dialogRef.close(configuredWidget);
            }
        }
    }

    back() {
        if (this.page === 'select-widget') {
            this.page = 'select-pipeline';
        } else if (this.page === 'configure-widget') {
            this.loadAvailableWidgets();
            this.page = 'select-widget';
        }
    }

    iconText(s) {
        return this.elementIconText.getElementIconText(s);
    }
}
