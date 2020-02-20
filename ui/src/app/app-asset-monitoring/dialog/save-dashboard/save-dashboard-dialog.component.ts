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

import {Component, Inject} from "@angular/core";
import {RestService} from "../../services/rest.service";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import {CanvasConfiguration} from "../../model/canvas-configuration.model";
import {DashboardConfiguration} from "../../model/dashboard-configuration.model";
import {ImageInfo} from "../../model/image-info.model";

@Component({
    selector: 'save-dashboard-dialog-component',
    templateUrl: 'save-dashboard-dialog.component.html',
    styleUrls: ['./save-dashboard-dialog.component.css'],
})
export class SaveDashboardDialogComponent {

    dashboardName: string;
    dashboardDescription: string;

    constructor(
        public dialogRef: MatDialogRef<SaveDashboardDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: CanvasConfiguration,
        private restService: RestService) {
    }

    cancel() {
        this.dialogRef.close();
    }

    save() {
        // save image
        this.restService.storeImage(this.data.file).subscribe(response => {
        });

        // save dashboard
        let imageInfo = this.makeImageInfo();
        let dashboardConfig = this.makeDashboardConfig();
        dashboardConfig.dashboardName = this.dashboardName;
        dashboardConfig.dashboardDescription = this.dashboardDescription;
        dashboardConfig.imageInfo = imageInfo;
        dashboardConfig.imageInfo.imageName = this.data.file.name;

        this.restService.storeDashboard(dashboardConfig).subscribe(response => {
            this.dialogRef.close();
        });
    }

    makeImageInfo(): ImageInfo {
        let imageShape = this.data.dashboardCanvas.findOne('#main-image');
        return imageShape.attrs as ImageInfo;
    }

    makeDashboardConfig(): DashboardConfiguration {
        let canvas = this.data.dashboardCanvas;
        let transformerShapes = Array.from(canvas.find('Transformer'));
        transformerShapes.forEach(shape => {
            shape.destroy();
        });

        let mainShape = canvas.findOne("#main-image");
        mainShape.destroy();

        return JSON.parse(canvas.toJSON());
    }
}