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

import { Component, EventEmitter, Input, Output } from '@angular/core';

import Konva from 'konva';
import { WebsocketService } from '../../services/websocket.service';
import { DashboardConfiguration } from '../../model/dashboard-configuration.model';
import { RestService } from '../../services/rest.service';

interface Window {
    Image: any;
}

declare const window: Window;

@Component({
    selector: 'view-asset',
    templateUrl: './view-asset.component.html',
    styleUrls: ['./view-asset.component.css']
})
export class ViewAssetComponent {

    @Input() dashboardConfig: DashboardConfiguration;
    @Output() dashboardClosed = new EventEmitter<boolean>();

    mainCanvasStage: any;
    mainLayer: any;
    backgroundImageLayer: any;

    constructor(private websocketService: WebsocketService,
                private restService: RestService) {

    }

    ngAfterViewInit() {
        this.mainCanvasStage = Konva.Node.create(this.dashboardConfig, 'container');
        this.mainCanvasStage.draw();

        this.backgroundImageLayer = new Konva.Layer();
        this.showImage();
        this.mainCanvasStage.add(this.backgroundImageLayer);
        this.backgroundImageLayer.moveToBottom();
        this.mainCanvasStage.draw();
        this.updateMeasurements();
    }

    updateMeasurements() {
        const dynamicShapes = this.mainCanvasStage.find('.dynamic-text');
        dynamicShapes.forEach(ds => {
            const monitoredField = ds.text();
           this.websocketService.connect(ds.attrs.brokerUrl, ds.attrs.topic).subscribe(msg => {
               ds.text(msg[monitoredField]);
               this.mainCanvasStage.draw();
           });
        });
    }

    showImage() {
        const image = new window.Image();
        image.src = this.restService.getImageUrl(this.dashboardConfig.imageInfo.imageName);
        this.dashboardConfig.imageInfo.image = image;
        image.onload = () => {
            const imageCanvas = new Konva.Image(this.dashboardConfig.imageInfo);
            this.backgroundImageLayer.add(imageCanvas);
            this.backgroundImageLayer.draw();
        };
    }

    closeDashboard() {
        this.dashboardClosed.emit(true);
    }

}
