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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

import Konva from 'konva';
import { WebsocketService } from '../../services/websocket.service';
import { DashboardConfiguration } from '../../model/dashboard-configuration.model';
import { RestService } from '../../services/rest.service';
import {DashboardService} from "../../../dashboard/services/dashboard.service";
import {
    DashboardWidgetModel,
    Pipeline,
    VisualizablePipeline
} from "../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model";
import {DashboardItem} from "../../../dashboard/models/dashboard.model";

interface Window {
    Image: any;
}

declare const window: Window;

@Component({
    selector: 'view-asset',
    templateUrl: './view-asset.component.html',
    styleUrls: ['./view-asset.component.css']
})
export class ViewAssetComponent implements OnInit {

    @Input() dashboardConfig: DashboardConfiguration;
    @Output() dashboardClosed = new EventEmitter<boolean>();
    @Output() editDashboardEmitter = new EventEmitter<DashboardConfiguration>();

    mainCanvasStage: any;
    mainLayer: any;
    backgroundImageLayer: any;

    dashboardItem: DashboardItem;
    widgetLoaded = false;

    constructor(private websocketService: WebsocketService,
                private restService: RestService,
                private dashboardService: DashboardService) {

    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        this.mainCanvasStage = Konva.Node.create(this.dashboardConfig, 'container');
        this.mainCanvasStage.draw();

        this.backgroundImageLayer = new Konva.Layer();
        this.showImage();
        this.mainCanvasStage.add(this.backgroundImageLayer);
        const labels = this.mainCanvasStage.find('Label');
        labels.each(label => {
           label.on('mouseenter', () => this.onMouseEnter(label));
            label.on('mouseleave', () => this.onMouseLeave(label));
           label.on('click', () => this.onLinkClicked(label));
        });

        this.backgroundImageLayer.moveToBottom();
        this.mainCanvasStage.draw();
        this.updateMeasurements();
    }

    onMouseEnter(label) {
        label.children[0].attrs.fontStyle = 'bold';
        this.mainCanvasStage.draw();
    }

    onMouseLeave(label) {
        label.children[0].attrs.fontStyle = 'normal';
        this.mainCanvasStage.draw();
    }

    onLinkClicked(label) {
        const href = label.children[0].attrs.hyperlink;
        const newWindow = label.children[0].attrs.newWindow;
        newWindow ? (window as any).open(href) : (window as any).location.href = href;
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

    editDashboard() {
        this.editDashboardEmitter.emit(this.dashboardConfig);
    }

}
