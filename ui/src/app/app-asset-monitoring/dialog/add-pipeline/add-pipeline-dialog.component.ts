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

import { Component, OnInit } from '@angular/core';
import { RestApi } from '../../../services/rest-api.service';
import { RestService } from '../../services/rest.service';
import { ElementIconText } from '../../../services/get-element-icon-text.service';
import { SelectedVisualizationData } from '../../model/selected-visualization-data.model';
import { DialogRef } from '@streampipes/shared-ui';
import {
    DataLakeMeasure,
    DatalakeRestService,
    DataViewDataExplorerService,
} from '@streampipes/platform-services';
import { zip } from 'rxjs';

@Component({
    selector: 'sp-add-pipeline-dialog-component',
    templateUrl: './add-pipeline-dialog.component.html',
    styleUrls: ['./add-pipeline-dialog.component.scss'],
})
export class AddPipelineDialogComponent implements OnInit {
    pages = [
        {
            type: 'select-pipeline',
            title: 'Select Pipeline',
            description: "Select a pipeline you'd like to visualize",
        },
        {
            type: 'select-measurement',
            title: 'Measurement Value',
            description: 'Select measurement',
        },
        {
            type: 'select-label',
            title: 'Label',
            description: 'Choose label',
        },
    ];

    visualizablePipelines: DataLakeMeasure[] = [];

    selectedVisualization: DataLakeMeasure;
    selectedType: any;
    selectedMeasurement: any;
    page: any = 'select-pipeline';

    selectedLabelBackgroundColor = '#FFFFFF';
    selectedLabelTextColor = '#1B1464';
    selectedMeasurementBackgroundColor = '#39B54A';
    selectedMeasurementTextColor = '#FFFFFF';
    selectedLabel: string;

    constructor(
        private dialogRef: DialogRef<AddPipelineDialogComponent>,
        private restApi: RestApi,
        private restService: RestService,
        private dataLakeRestService: DatalakeRestService,
        private dataExplorerService: DataViewDataExplorerService,
        public elementIconText: ElementIconText,
    ) {}

    ngOnInit() {
        this.loadVisualizablePipelines();
    }

    loadVisualizablePipelines() {
        zip(
            this.dataExplorerService.getAllPersistedDataStreams(),
            this.dataLakeRestService.getAllMeasurementSeries(),
        ).subscribe(res => {
            const visualizablePipelines = res[0];
            visualizablePipelines.forEach(p => {
                const measurement = res[1].find(m => {
                    return m.measureName === p.measureName;
                });
                p.eventSchema = measurement.eventSchema;
            });
            this.visualizablePipelines = visualizablePipelines;
        });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    getSelectedPipelineCss(vis) {
        return this.getSelectedCss(this.selectedVisualization, vis);
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
        this.selectedVisualization = vis;
        this.next();
    }

    next() {
        if (this.page === 'select-pipeline') {
            this.page = 'select-measurement';
        } else if (this.page === 'select-measurement') {
            this.page = 'select-label';
        } else {
            const selectedConfig: SelectedVisualizationData =
                {} as SelectedVisualizationData;
            selectedConfig.labelBackgroundColor =
                this.selectedLabelBackgroundColor;
            selectedConfig.labelTextColor = this.selectedLabelTextColor;
            selectedConfig.measurementBackgroundColor =
                this.selectedMeasurementBackgroundColor;
            selectedConfig.measurementTextColor =
                this.selectedMeasurementTextColor;
            selectedConfig.measurement = this.selectedMeasurement;
            selectedConfig.visualizationId =
                this.selectedVisualization.pipelineId;
            selectedConfig.label = this.selectedLabel;
            selectedConfig.dataLakeMeasure =
                this.selectedVisualization.measureName;

            this.dialogRef.close(selectedConfig);
        }
    }

    back() {
        if (this.page === 'select-measurement') {
            this.page = 'select-pipeline';
        } else if (this.page === 'select-label') {
            this.page = 'select-measurement';
        }
    }

    iconText(s) {
        return this.elementIconText.getElementIconText(s);
    }
}
