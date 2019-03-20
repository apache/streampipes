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

    public testCanvasObjects: any = {
        image: '/assets/img/test-asset-image.jpg',
        measurements: [{
            label: {
                text: "Temperature",
                x: 0,
                y: 100,
                fill: "blue",
                id: "my-text"
            },
            measurement: {
                //measurementTopic: "org.streampipes.test",
                //measurementUrl: "",
                x: 0,
                y: 20,
                fill: "red",
                width: 100,
                height: 100,
                id: "hi-id"
            }
        }
        ]
    };

    constructor(private websocketService: WebsocketService,
                private restService: RestService) {

    }

    updateMeasurement() {
        let brokerUrl = "ws://localhost:8082/streampipes/ws/topic/";
        let topic = "/topic/5f43f98a-0f47-486b-93f7-ab3d9c596668";
        this.websocketService.connect(brokerUrl, topic).subscribe(msg => {
            let measurementComponent = this.mainCanvasStage.findOne("#my-text");
            measurementComponent.text(msg.randomValue);
            this.mainLayer.draw();
        });
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

    showItems() {
        this.testCanvasObjects.measurements.forEach(m => {
            this.mainLayer.add(this.makeMeasurement(m.measurement));
            this.mainLayer.add(this.makeLabel(m.label));
        });
        this.mainLayer.draw();
    }

    makeLabel(label): any {
        return new Konva.Text(label);
    }

    makeMeasurement(measurement): any {
        return new Konva.Rect(measurement);
    }


    ngOnInit() {
        //this.showImage("/assets/img/test-asset-image.jpg");
        //this.showItems();
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


    public configStage: Observable<any> = of({
        width: 800,
        height: 800
    });


    public handleClick(component) {
        console.log('Hello Circle', component);
    }

    closeDashboard() {
        this.dashboardClosed.emit(true);
    }

}