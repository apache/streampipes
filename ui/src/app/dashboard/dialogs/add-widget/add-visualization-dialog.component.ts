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

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FreeTextStaticProperty } from '../../../connect/model/FreeTextStaticProperty';
import { MappingPropertyNary } from '../../../connect/model/MappingPropertyNary';
import { MappingPropertyUnary } from '../../../connect/model/MappingPropertyUnary';
import { EventProperty } from '../../../connect/schema-editor/model/EventProperty';
import { EventSchema } from '../../../connect/schema-editor/model/EventSchema';
import { DashboardWidget } from '../../../core-model/dashboard/DashboardWidget';
import { DashboardWidgetSettings } from '../../../core-model/dashboard/DashboardWidgetSettings';
import { VisualizablePipeline } from '../../../core-model/dashboard/VisualizablePipeline';
import { ElementIconText } from '../../../services/get-element-icon-text.service';
import { Dashboard } from '../../models/dashboard.model';
import { WidgetConfigBuilder } from '../../registry/widget-config-builder';
import { WidgetRegistry } from '../../registry/widget-registry';
import { MappingPropertyGenerator } from '../../sdk/matching/mapping-property-generator';
import { DashboardService } from '../../services/dashboard.service';

@Component({
    selector: 'add-visualization-dialog-component',
    templateUrl: './add-visualization-dialog.component.html',
    styleUrls: ['./add-visualization-dialog.component.scss']
})
export class AddVisualizationDialogComponent {

    pages = [{
        type: 'select-pipeline',
        title: 'Select Pipeline',
        description: 'Select a pipeline you\'d like to visualize'
    }, {
        type: 'select-widget',
        title: 'Select Widget',
        description: 'Select widget'
    }, {
        type: 'configure-widget',
        title: 'Configure Widget',
        description: 'Configure widget'
    }];

    visualizablePipelines: VisualizablePipeline[] = [];
    availableWidgets: DashboardWidgetSettings[];

    selectedPipeline: VisualizablePipeline;
    selectedWidget: DashboardWidgetSettings;

    dashboard: Dashboard;

    selectedType: any;
    page: any = 'select-pipeline';
    dialogTitle: string;

    configValid: boolean;


    constructor(
        public dialogRef: MatDialogRef<AddVisualizationDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private dashboardService: DashboardService,
        public elementIconText: ElementIconText) {
    }

    ngOnInit() {
        if (!this.data) {
            this.dialogTitle = 'Add widget';
            this.dashboardService.getVisualizablePipelines().subscribe(visualizations => {
                this.visualizablePipelines = [];
                visualizations.forEach(vis => {
                    this.dashboardService.getPipelineById(vis.pipelineId).subscribe(pipeline => {
                        vis.pipelineName = pipeline.name;
                        this.visualizablePipelines.push(vis);
                        this.sortPipeline();
                    });
                });
            });
        } else {
            this.dialogTitle = 'Edit widget';
            this.selectedPipeline = this.data.pipeline;
            this.selectedWidget = this.data.widget.dashboardWidgetSettings;
            this.page = 'configure-widget';
        }
    }

    sortPipeline() {
        this.visualizablePipelines.sort((a, b) => {
            return a.pipelineName < b.pipelineName ? -1 : 1;
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
        if (selected == current) {
            return 'wizard-preview wizard-preview-selected';
        } else {
            return 'wizard-preview';
        }
    }

    getTabCss(page) {
        if (page == this.page) { return 'md-fab md-accent'; } else { return 'md-fab md-accent wizard-inactive'; }
    }

    selectPipeline(vis) {
        this.selectedPipeline = vis;
        this.next();
    }

    selectWidget(widget) {
        this.selectedWidget = widget;
        this.selectedWidget.config.forEach(sp => {
            if (sp instanceof MappingPropertyUnary || sp instanceof MappingPropertyNary) {
                const requirement: EventProperty = this.findRequirement(this.selectedWidget.requiredSchema, sp.internalName);
                sp.mapsFromOptions = new MappingPropertyGenerator(requirement, this.selectedPipeline.schema.eventProperties).computeMatchingProperties();
            }
            if (sp instanceof FreeTextStaticProperty && sp.internalName === WidgetConfigBuilder.TITLE_KEY) {
                sp.value = this.selectedPipeline.visualizationName;
            }
        });
        this.next();
    }

    findRequirement(requiredSchema: EventSchema, internalName: string) {
        return requiredSchema.eventProperties.find(ep => ep.runtimeName === internalName);
    }

    next() {
        if (this.page == 'select-pipeline') {
            this.availableWidgets = WidgetRegistry.getCompatibleWidgetTemplates(this.selectedPipeline);
            this.availableWidgets.sort((a, b) => {
                return a.widgetLabel < b.widgetLabel ? -1 : 1;
            });
            this.page = 'select-widget';
        } else if (this.page == 'select-widget') {
            this.page = 'configure-widget';
        } else {
            const configuredWidget: DashboardWidget = new DashboardWidget();
            configuredWidget.dashboardWidgetSettings = this.selectedWidget;
            configuredWidget.visualizablePipelineId = this.selectedPipeline._id;
            configuredWidget.visualizablePipelineTopic = this.selectedPipeline.topic;
            if (!this.data) {
                this.dashboardService.saveWidget(configuredWidget).subscribe(response => {
                    this.dialogRef.close(response);
                });
            } else {
                configuredWidget._id = this.data.widget._id;
                configuredWidget._ref = this.data.widget._ref;
                configuredWidget.widgetId = this.data.widget.widgetId;
                this.dialogRef.close(configuredWidget);
            }
        }
    }

    back() {
        if (this.page == 'select-widget') {
            this.page = 'select-pipeline';
        } else if (this.page == 'configure-widget') {
            this.page = 'select-widget';
        }
    }

    iconText(s) {
        return this.elementIconText.getElementIconText(s);
    }

    validConfiguration(valid: boolean) {
        setTimeout(() => {
            this.configValid = this.selectedWidget.config.every(sp => sp.isValid);
        });
    }

}
