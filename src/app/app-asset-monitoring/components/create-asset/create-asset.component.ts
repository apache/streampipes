import {Component, HostListener} from "@angular/core";
import Konva from "konva";
import {AddPipelineDialogComponent} from "../../dialog/add-pipeline/add-pipeline-dialog.component";
import {MatDialog} from "@angular/material";
import {ShapeService} from "../../services/shape.service";
import {SelectedVisualizationData} from "../../model/selected-visualization-data.model";
import {SaveDashboardDialogComponent} from "../../dialog/save-dashboard/save-dashboard-dialog.component";
import {DashboardConfiguration} from "../../model/dashboard-configuration.model";

interface Window {
    Image: any;
}

declare const window: Window;

@Component({
    selector: 'create-asset',
    templateUrl: './create-asset.component.html',
    styleUrls: ['./create-asset.component.css']
})
export class CreateAssetComponent {

    fileName: any;
    selectedUploadFile: File;

    mainCanvasStage: Konva.Stage;
    backgroundImageLayer: any;
    mainLayer: any;

    selectedVisualizationData: SelectedVisualizationData;

    constructor(public dialog: MatDialog, public shapeService: ShapeService) {

    }

    ngAfterViewInit() {
        var width = 1200;
        var height = 900;
        this.mainCanvasStage = new Konva.Stage({
            container: 'asset-configuration-board-canvas',
            width: width,
            height: height
        });

        let container = this.mainCanvasStage.container();
        container.focus();

        this.mainLayer = new Konva.Layer();
        this.backgroundImageLayer = new Konva.Layer();

        this.backgroundImageLayer.on('click', function(evt) {
            // get the shape that was clicked on
            var shape = evt.target;
            console.log(evt);
        });

        this.mainCanvasStage.add(this.backgroundImageLayer);
        this.mainCanvasStage.add(this.mainLayer);
    }

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;

        const image = new window.Image();
        image.onload = () => {
            let imageCanvas = new Konva.Image({
                image: image,
                width: image.width,
                height: image.height,
                x: 0,
                y: 0,
                draggable: true,
                id: 'main-image'
            });

            this.backgroundImageLayer.add(imageCanvas);
            this.backgroundImageLayer.draw();

            var tr = new Konva.Transformer({
                anchorStroke: 'white',
                anchorFill: '#39B54A',
                anchorSize: 20,
                borderStroke: 'green',
                borderDash: [3, 3],
                keepRatio: true,
            });
            this.backgroundImageLayer.add(tr);
            tr.attachTo(imageCanvas);
            this.backgroundImageLayer.draw();
        };

        const reader = new FileReader();
        reader.onload = e => image.src = reader.result;

        reader.readAsDataURL(this.selectedUploadFile);


    }

    prepareDashboard() {
        const dialogRef = this.dialog.open(SaveDashboardDialogComponent, {
            width: '70%',
            height: '500px',
            panelClass: 'custom-dialog-container',
            data: {dashboardCanvas: this.mainCanvasStage as DashboardConfiguration, file: this.selectedUploadFile}
        });
    }

    clearCanvas() {
        this.backgroundImageLayer.clear();
        this.mainLayer.clear();
    }

    openAddPipelineDialog(): void {
        const dialogRef = this.dialog.open(AddPipelineDialogComponent, {
            width: '70%',
            height: '500px',
            panelClass: 'custom-dialog-container'
        });

        dialogRef.afterClosed().subscribe(result => {
            this.addNewVisulizationItem(result);
            console.log(result);
            this.mainLayer.draw();
        });
    }

    @HostListener('document:keydown', ['$event'])
    handleKeyboardEvent(event: KeyboardEvent) {
        console.log(event);
        //this.key = event.key;
    }

    addNewVisulizationItem(visualizationConfig) {
        let visGroup = this.shapeService.makeNewMeasurementShape(visualizationConfig);
        this.mainLayer.add(visGroup);
        let tr = this.getNewTransformer();
        this.mainLayer.add(tr);
        tr.attachTo(visGroup);
        this.mainLayer.draw();

    }

    getNewTransformer(): Konva.Transformer {
        return new Konva.Transformer({
            anchorStroke: 'white',
            anchorFill: '#39B54A',
            anchorSize: 10,
            borderStroke: 'green',
            borderDash: [3, 3],
            keepRatio: true
        });
    }

}