import {Component, ElementRef, EventEmitter, Input, Output, QueryList, ViewChild, ViewChildren} from "@angular/core";
import {Observable, of} from "rxjs";

import Konva from "konva";
import {WebsocketService} from "../../services/websocket.service";
import {DashboardConfiguration} from "../../model/dashboard-configuration.model";
import {RestService} from "../../services/rest.service";

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
        let dynamicShapes = this.mainCanvasStage.find(".dynamic-text");
        dynamicShapes.forEach(ds => {
            let monitoredField = ds.text();
           this.websocketService.connect(ds.attrs.brokerUrl, ds.attrs.topic).subscribe(msg => {
               ds.text(msg[monitoredField]);
               this.mainCanvasStage.draw();
           })
        });
    }

    showImage() {
        const image = new window.Image();
        image.src = this.restService.getImageUrl(this.dashboardConfig.imageInfo.imageName);
        this.dashboardConfig.imageInfo.image = image;
        image.onload = () => {
            let imageCanvas = new Konva.Image(this.dashboardConfig.imageInfo);
            this.backgroundImageLayer.add(imageCanvas);
            this.backgroundImageLayer.draw();
        }
    }

    closeDashboard() {
        this.dashboardClosed.emit(true);
    }

}