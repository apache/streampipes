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

import { Component, Input, OnInit } from '@angular/core';
import { RestService } from '../../services/rest.service';
import { DashboardConfiguration } from '../../model/dashboard-configuration.model';
import { ImageInfo } from '../../model/image-info.model';
import { DialogRef } from '@streampipes/shared-ui';
import Konva from 'konva';
import Stage = Konva.Stage;

@Component({
    selector: 'sp-save-dashboard-dialog-component',
    templateUrl: 'save-dashboard-dialog.component.html',
    styleUrls: ['./save-dashboard-dialog.component.scss'],
})
export class SaveDashboardDialogComponent implements OnInit {
    dashboardName: string;
    dashboardDescription: string;

    @Input()
    dashboardCanvas: Stage;

    @Input()
    file: File;

    @Input()
    editMode: boolean;

    @Input()
    dashboardConfig: DashboardConfiguration;

    constructor(
        private dialogRef: DialogRef<SaveDashboardDialogComponent>,
        private restService: RestService,
    ) {}

    ngOnInit() {
        if (this.editMode) {
            this.dashboardName = this.dashboardConfig.dashboardName;
            this.dashboardDescription =
                this.dashboardConfig.dashboardDescription;
        }
    }

    cancel() {
        this.dialogRef.close();
    }

    save() {
        // save image
        if (this.file) {
            this.restService.storeImage(this.file).subscribe(response => {});
        }

        // save dashboard
        const imageInfo = this.makeImageInfo();
        const dashboardConfig = this.makeDashboardConfig();
        dashboardConfig.dashboardName = this.dashboardName;
        dashboardConfig.dashboardDescription = this.dashboardDescription;
        dashboardConfig.imageInfo = imageInfo;
        dashboardConfig.imageInfo.draggable = false;
        if (this.file) {
            dashboardConfig.imageInfo.imageName = this.file.name;
        }

        if (this.editMode) {
            dashboardConfig.dashboardId = this.dashboardConfig.dashboardId;
        }

        const observable = this.editMode
            ? this.restService.updateDashboard(dashboardConfig)
            : this.restService.storeDashboard(dashboardConfig);
        observable.subscribe(response => {
            this.dialogRef.close();
        });
    }

    makeImageInfo(): ImageInfo {
        const imageShape = this.dashboardCanvas.findOne('#main-image');
        return imageShape.attrs as ImageInfo;
    }

    makeDashboardConfig(): DashboardConfiguration {
        const canvas = this.dashboardCanvas;
        const transformerShapes = Array.from(canvas.find('Transformer'));
        transformerShapes.forEach(shape => {
            shape.destroy();
        });

        const mainShape = canvas.findOne('#main-image');
        mainShape.destroy();

        return JSON.parse(canvas.toJSON());
    }
}
