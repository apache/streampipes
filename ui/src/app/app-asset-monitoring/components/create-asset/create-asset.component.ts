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

import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import Konva from 'konva';
import { AddPipelineDialogComponent } from '../../dialog/add-pipeline/add-pipeline-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ShapeService } from '../../services/shape.service';
import { SelectedVisualizationData } from '../../model/selected-visualization-data.model';
import { SaveDashboardDialogComponent } from '../../dialog/save-dashboard/save-dashboard-dialog.component';
import { PanelType } from '../../../core-ui/dialog/base-dialog/base-dialog.model';
import { DialogService } from '../../../core-ui/dialog/base-dialog/base-dialog.service';

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

    IMAGE_ID = 'main-image';
    selectedVisualizationData: SelectedVisualizationData;

    backgroundImagePresent = false;
    measurementPresent = false;

    @Output() dashboardClosed = new EventEmitter<boolean>();

    constructor(public dialog: MatDialog,
                public shapeService: ShapeService,
                private dialogService: DialogService) {
    }

    ngAfterViewInit() {
        const width = 1400;
        const height = 900;
        this.mainCanvasStage = new Konva.Stage({
            container: 'asset-configuration-board-canvas',
            width,
            height
        });

        const container = this.mainCanvasStage.container();
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
            while (!parentElement.id()) {
                parentElement = parentElement.getParent();
            }
            this.currentlySelectedShape = parentElement;
        });
        this.mainCanvasStage.add(this.backgroundImageLayer);
        this.mainCanvasStage.add(this.mainLayer);
    }

    handleFileInput(event: any) {
        const files: any = event.target.files;
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;

        const image = new window.Image();
        image.onload = () => {
            const desiredWidth = Math.min(this.mainCanvasStage.width(), image.width);
            const aspectRatio = image.width / image.height;
            const desiredHeight = desiredWidth * aspectRatio;
            const imageCanvas = new Konva.Image({
                image,
                width: desiredWidth,
                height: desiredHeight,
                x: 0,
                y: 0,
                draggable: true,
                id: this.IMAGE_ID
            });

            this.backgroundImageLayer.add(imageCanvas);
            this.backgroundImageLayer.draw();

            const tr = this.getNewTransformer(this.IMAGE_ID);
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
        const dialogRef = this.dialogService.open(SaveDashboardDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Save asset dashboard',
            width: '50vw',
            data: {
                dashboardCanvas: this.mainCanvasStage as any,
                file: this.selectedUploadFile
            }
        });
        dialogRef.afterClosed().subscribe(closed => {
            console.log('close');
            this.dashboardClosed.emit(true);
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
        const dialogRef = this.dialogService.open(AddPipelineDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Add visualization',
            width: '50vw',
            data: {
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            console.log(result);
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
        if (event.code === 'Delete') {
            const id = this.currentlySelectedShape.id();
            this.mainCanvasStage.findOne('#' + id + '-transformer').destroy();
            this.currentlySelectedShape.destroy();
            if (id === this.IMAGE_ID) {
                this.backgroundImagePresent = false;
            } else {
                const remainingElementIds = this.mainLayer.find('Group');
                if (remainingElementIds.length === 0) {
                    this.measurementPresent = false;
                }
            }
        } else if (event.code === 'ArrowLeft') {
            this.currentlySelectedShape.x(this.currentlySelectedShape.x() - delta);
        } else if (event.code === 'ArrowRight') {
            this.currentlySelectedShape.x(this.currentlySelectedShape.x() + delta);
        } else if (event.code === 'ArrowDown') {
            this.currentlySelectedShape.y(this.currentlySelectedShape.y() + delta);
        } else if (event.code === 'ArrowUp') {
            this.currentlySelectedShape.y(this.currentlySelectedShape.y() - delta);
        }
        this.backgroundImageLayer.draw();
        this.mainLayer.draw();
    }

    addNewVisulizationItem(visualizationConfig) {
        const visGroup = this.shapeService.makeNewMeasurementShape(visualizationConfig);
        const id = this.makeId();
        visGroup.id(id);
        this.mainLayer.add(visGroup);
        const tr = this.getNewTransformer(id);
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
            id: id + '-transformer'
        });
    }

    makeId() {
        let text = '';
        const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

        for (let i = 0; i < 6; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }

        return text;
    }

}
