import {Component, HostListener} from "@angular/core";
import Konva from "konva";
import {AddPipelineDialogComponent} from "../../dialog/add-pipeline/add-pipeline-dialog.component";
import {MatDialog} from "@angular/material";
import {ShapeService} from "../../services/shape.service";
import {SelectedVisualizationData} from "../../model/selected-visualization-data.model";
import {SaveDashboardDialogComponent} from "../../dialog/save-dashboard/save-dashboard-dialog.component";

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

    currentlySelectedShape: any;

    IMAGE_ID: string = "main-image";
    selectedVisualizationData: SelectedVisualizationData;

    backgroundImagePresent: boolean = false;
    measurementPresent: boolean = false;

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

        this.initLayers();
    }

    initLayers() {
        this.mainLayer = new Konva.Layer();
        this.backgroundImageLayer = new Konva.Layer();

        this.backgroundImageLayer.on('click', evt => {
            this.currentlySelectedShape = evt.target;
        });
        this.mainLayer.on('click', evt => {
            let parentElement = evt.target.getParent();
            while(!parentElement.id()) {
                parentElement = parentElement.getParent();
            }
            this.currentlySelectedShape = parentElement;
        });
        this.mainCanvasStage.add(this.backgroundImageLayer);
        this.mainCanvasStage.add(this.mainLayer);
    }

    handleFileInput(event: any) {
        let files: any = event.target.files;
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
                id: this.IMAGE_ID
            });

            this.backgroundImageLayer.add(imageCanvas);
            this.backgroundImageLayer.draw();

            var tr = this.getNewTransformer(this.IMAGE_ID);
            this.backgroundImageLayer.add(tr);
            tr.attachTo(imageCanvas);
            this.backgroundImageLayer.draw();
            this.currentlySelectedShape = imageCanvas;
        };

        const reader = new FileReader();
        reader.onload = e => image.src = reader.result;

        reader.readAsDataURL(this.selectedUploadFile);
        event.target.value = null;
        this.backgroundImagePresent = true;

    }

    prepareDashboard() {
        const dialogRef = this.dialog.open(SaveDashboardDialogComponent, {
            width: '70%',
            height: '500px',
            panelClass: 'custom-dialog-container',
            data: {dashboardCanvas: this.mainCanvasStage as any, file: this.selectedUploadFile}
        });
    }

    clearCanvas() {
        this.backgroundImageLayer.destroy();
        this.mainLayer.destroy();
        this.initLayers();
        this.backgroundImagePresent = false;
        this.measurementPresent = false;
    }

    openAddPipelineDialog(): void {
        const dialogRef = this.dialog.open(AddPipelineDialogComponent, {
            width: '70%',
            height: '500px',
            panelClass: 'custom-dialog-container'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.addNewVisulizationItem(result);
                this.measurementPresent = true;
                this.mainLayer.draw();
            }
        });
    }

    @HostListener('document:keydown', ['$event'])
    handleKeyboardEvent(event: KeyboardEvent) {
        const delta = 4;
        if (event.code == "Delete") {
            let id = this.currentlySelectedShape.id();
            this.mainCanvasStage.findOne("#" +id + "-transformer").destroy();
            this.currentlySelectedShape.destroy();
            if (id === this.IMAGE_ID) {
                this.backgroundImagePresent = false;
            } else {
                let remainingElementIds = this.mainLayer.find("Group");
                if (remainingElementIds.length === 0) {
                    this.measurementPresent = false;
                }
            }
        } else if (event.code == "ArrowLeft") {
            this.currentlySelectedShape.x(this.currentlySelectedShape.x() - delta);
        } else if (event.code == "ArrowRight") {
            this.currentlySelectedShape.x(this.currentlySelectedShape.x() + delta);
        } else if (event.code == "ArrowDown") {
            this.currentlySelectedShape.y(this.currentlySelectedShape.y() + delta);
        } else if (event.code == "ArrowUp") {
            this.currentlySelectedShape.y(this.currentlySelectedShape.y() - delta);
        }
        this.backgroundImageLayer.draw();
        this.mainLayer.draw();
    }

    addNewVisulizationItem(visualizationConfig) {
        let visGroup = this.shapeService.makeNewMeasurementShape(visualizationConfig);
        let id = this.makeId();
        visGroup.id(id);
        this.mainLayer.add(visGroup);
        let tr = this.getNewTransformer(id);
        this.mainLayer.add(tr);
        tr.attachTo(visGroup);
        this.mainLayer.draw();
        this.currentlySelectedShape = visGroup;
    }

    getNewTransformer(id: string): Konva.Transformer {
        return new Konva.Transformer({
            anchorStroke: 'white',
            anchorFill: '#39B54A',
            anchorSize: 10,
            borderStroke: 'green',
            borderDash: [3, 3],
            keepRatio: true,
            id: id + "-transformer"
        });
    }

    makeId() {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < 6; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

}