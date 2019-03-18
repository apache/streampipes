import {Component, Inject} from "@angular/core";
import {RestService} from "../../services/rest.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {CanvasConfiguration} from "../../model/canvas-configuration.model";
import {DashboardConfiguration} from "../../model/dashboard-configuration.model";
import {ImageInfo} from "../../model/image-info.model";

@Component({
    selector: 'save-dashboard-dialog-component',
    templateUrl: 'save-dashboard-dialog.component.html',
    styleUrls: ['../add-pipeline/add-pipeline-dialog.component.css'],
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
           console.log(response);
        });

        // save dashboard
        let imageInfo = this.makeImageInfo();
        let dashboardConfig = this.makeDashboardConfig();
        dashboardConfig.dashboardName = this.dashboardName;
        dashboardConfig.dashboardDescription = this.dashboardDescription;
        dashboardConfig.imageInfo = imageInfo;
        dashboardConfig.imageInfo.imageName = this.data.file.name;

        console.log(dashboardConfig);

        this.restService.storeDashboard(dashboardConfig).subscribe(response => {
            this.dialogRef.close();
        });
    }

    makeImageInfo(): ImageInfo {
        let imageShape = this.data.dashboardCanvas.findOne('#main-image');
        console.log(imageShape);
        return imageShape.attrs as ImageInfo;
    }

    makeDashboardConfig(): DashboardConfiguration {
        let canvas = this.data.dashboardCanvas;
        let transformerShapes = canvas.find('Transformer');
        transformerShapes.forEach(shape => {
            shape.destroy();
        });

        let mainShape = canvas.find("#main-image");
        mainShape.destroy();

        return JSON.parse(canvas.toJSON());
    }
}