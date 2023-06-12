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
import { SpConfigurationTabs } from '../configuration-tabs';
import { LabelsService, SpLabel } from '@streampipes/platform-services';

@Component({
    selector: 'sp-label-configuration',
    templateUrl: './label-configuration.component.html',
    styleUrls: ['./label-configuration.component.scss'],
})
export class SpLabelConfigurationComponent implements OnInit {
    tabs = SpConfigurationTabs.getTabs();

    allLabels: SpLabel[] = [];
    createLabelMode = false;

    editedLabels: string[] = [];

    constructor(private labelsService: LabelsService) {}

    ngOnInit(): void {
        this.reloadLabels();
    }

    reloadLabels(): void {
        this.labelsService.getAllLabels().subscribe(res => {
            this.allLabels = res;
        });
    }

    saveLabel(label: SpLabel): void {
        this.labelsService.addLabel(label).subscribe(() => this.reloadLabels());
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
        this.editedLabels.splice(this.editedLabels.indexOf(labelId), 1);
    }

    isEditMode(labelId: string): boolean {
        return this.editedLabels.find(l => l === labelId) !== undefined;
    }
}
