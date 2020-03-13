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


import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { MatSnackBar } from '@angular/material/snack-bar';
import Konva from 'konva';
import { Annotation } from '../../../core-model/coco/Annotation';
import { CocoFormat } from '../../../core-model/coco/Coco.format';
import { DatalakeRestService } from '../../../core-services/datalake/datalake-rest.service';
import { ImageContainerComponent } from '../components/image-container/image-container.component';
import { ICoordinates } from '../model/coordinates';
import { BrushLabelingService } from '../services/BrushLabeling.service';
import { PolygonLabelingService } from '../services/PolygonLabeling.service';
import { ReactLabelingService } from '../services/ReactLabeling.service';
import { LabelingMode } from '../model/labeling-mode';

@Component({
  selector: 'sp-image-labeling',
  templateUrl: './image-labeling.component.html',
  styleUrls: ['./image-labeling.component.css']
})
export class ImageLabelingComponent implements OnInit, AfterViewInit {

  // label
  public labels;
  public selectedLabel: {category, label};

  // images
  public imagesSrcs;
  public imagesIndex: number;

  public cocoFiles: CocoFormat[];

  public isHoverComponent;
  public brushSize: number;

  @ViewChild(ImageContainerComponent) imageView: ImageContainerComponent;


  public labelingMode: LabelingMode = LabelingMode.ReactLabeling;

  constructor(private restService: DatalakeRestService, private reactLabelingService: ReactLabelingService,
              private polygonLabelingService: PolygonLabelingService, private brushLabelingService: BrushLabelingService,
              private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.isHoverComponent = false;
    this.brushSize = 5;

    // 1. get labels
    this.labels = this.restService.getLabels();

    // 2. get Images
    this.imagesSrcs = this.restService.getImageSrcs();
    this.imagesIndex = 0;

    // 3. get Coco files
    this.cocoFiles = [];
    for (const src of this.imagesSrcs) {
      const coco = new CocoFormat();
      coco.addImage(src);
      this.cocoFiles.push(coco);
    }
  }

  ngAfterViewInit(): void {
    this.imagesIndex = 0;
  }


  /* sp-image-view handler */
  handleMouseDownLeft(layer: Konva.Layer, shift: ICoordinates, position: ICoordinates) {
    if (this.labelingEnabled()) {
      switch (this.labelingMode) {
        case LabelingMode.ReactLabeling: this.reactLabelingService.startLabeling(position);
          break;
        case LabelingMode.PolygonLabeling: this.polygonLabelingService.startLabeling(position);
          break;
        case LabelingMode.BrushLabeling: this.brushLabelingService.startLabeling(position, this.brushSize);
      }
    }
  }

  handleMouseMove(layer: Konva.Layer, shift: ICoordinates, position: ICoordinates) {
    switch (this.labelingMode) {
      case LabelingMode.PolygonLabeling: {
        this.polygonLabelingService.executeLabeling(position);
        this.polygonLabelingService.tempDraw(layer, shift, this.selectedLabel.label);
      }
    }
  }

  handleMouseMoveLeft(layer: Konva.Layer, shift: ICoordinates, position: ICoordinates) {
    if (this.labelingEnabled()) {
      switch (this.labelingMode) {
        case LabelingMode.ReactLabeling: {
          this.reactLabelingService.executeLabeling(position);
          this.reactLabelingService.tempDraw(layer, shift, this.selectedLabel.label);
        }
          break;
        case  LabelingMode.BrushLabeling: {
          this.brushLabelingService.executeLabeling(position);
          this.brushLabelingService.tempDraw(layer, shift, this.selectedLabel.label);
        }
      }
    }
  }

  handleMouseUpLeft(annotationLayer: Konva.Layer, drawLayer: Konva.Layer, shift: ICoordinates, position: ICoordinates) {
    if (this.labelingEnabled()) {
      switch (this.labelingMode) {
        case LabelingMode.ReactLabeling: {
          const result = this.reactLabelingService.endLabeling(position);
          const coco = this.cocoFiles[this.imagesIndex];
          const annotation = coco.addReactAnnotationToFirstImage(result[0], result[1],
            this.selectedLabel.category, this.selectedLabel.label);
          this.reactLabelingService.draw(annotationLayer, shift, annotation, this.imageView);
        }
          break;
        case LabelingMode.PolygonLabeling: {
           this.polygonLabelingService.tempDraw(drawLayer, shift, this.selectedLabel.label);
        }
          break;
        case LabelingMode.BrushLabeling: {
          const result = this.brushLabelingService.endLabeling(position);
          const coco = this.cocoFiles[this.imagesIndex];
          const annotation = coco.addBrushAnnotationFirstImage(result[0], result[1],
            this.selectedLabel.category, this.selectedLabel.label);
          this.brushLabelingService.draw(annotationLayer, shift, annotation, this.imageView);
        }
      }
    }
  }


  handleImageViewDBClick(layer: Konva.Layer, shift: ICoordinates, position: ICoordinates) {
    if (this.labelingEnabled()) {
      switch (this.labelingMode) {
        case LabelingMode.PolygonLabeling:
          const points = this.polygonLabelingService.endLabeling(position);
          const coco = this.cocoFiles[this.imagesIndex];
          const annotation = coco.addPolygonAnnotationFirstImage(points,
            this.selectedLabel.category, this.selectedLabel.label);
          this.polygonLabelingService.draw(layer, shift, annotation, this.imageView);
      }
    }
  }

  handleChildRedraw(layer: Konva.Layer, shift: ICoordinates) {
    const coco = this.cocoFiles[this.imagesIndex];
    for (const annotation of coco.annotations) {
      annotation.isHovered = false;
      annotation.isSelected = false;
      if (annotation.isBox()) {
        this.reactLabelingService.draw(layer, shift, annotation, this.imageView);
      } else if (annotation.isPolygon() && !annotation.isBrush()) {
        this.polygonLabelingService.draw(layer, shift, annotation, this.imageView);
      } else if (annotation.isBrush()) {
        this.brushLabelingService.draw(layer, shift, annotation, this.imageView);
      }
    }
  }

  handleImageViewShortCuts(key) {
    if (key === 'delete') {
      const coco = this.cocoFiles[this.imagesIndex];
      const toDelete = coco.annotations.filter(anno => anno.isSelected);
      for (const anno of toDelete) {
        this.handleDeleteAnnotation(anno);
      }
    }
  }

  /* sp-image-labels handler */
  handleLabelChange(label: {category, label}) {
    this.selectedLabel = label;
  }

  /* sp-image-bar */
  handleImageIndexChange(index) {
    this.save();
    this.imagesIndex = index;
  }
  handleImagePageUp(e) {
    this.save();
    alert('Page Up - Load new data');
  }

  handleImagePageDown(e) {
    this.save();
    alert('Page Down - Load new data');
  }

  /* sp-image-annotations handlers */
  handleChangeAnnotationLabel(change: [Annotation, string, string]) {
    const coco = this.cocoFiles[this.imagesIndex];
    const categoryId = coco.getLabelId(change[1], change[2]);
    change[0].category_id = categoryId;
    change[0].category_name = change[2];
    this.imageView.redrawAll();
  }

  handleDeleteAnnotation(annotation) {
    if (annotation !== undefined) {
      const coco = this.cocoFiles[this.imagesIndex];
      coco.removeAnnotation(annotation.id);
      this.imageView.redrawAll();
    }
  }

  /* utils */

  private labelingEnabled() {
    const coco = this.cocoFiles[this.imagesIndex];
    const annotation = coco.annotations.find(anno => anno.isHovered);
    if (annotation !== undefined) {
      return false;
    } else {
      return true;
    }
  }

  private save() {
    // TODO
    const coco = this.cocoFiles[this.imagesIndex];
    console.log(coco);
    this.openSnackBar('TODO: Save coco file');
  }

  private openSnackBar(message: string) {
    this.snackBar.open(message, '', {
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'right'
    });
  }

  /* UI */
  isReactMode() {
    return this.labelingMode === LabelingMode.ReactLabeling;
  }

  setReactMode() {
    this.labelingMode = LabelingMode.ReactLabeling;
  }

  isPolygonMode() {
    return this.labelingMode === LabelingMode.PolygonLabeling;
  }

  setPolygonMode() {
    this.labelingMode = LabelingMode.PolygonLabeling;
  }

  isBrushMode() {
    return this.labelingMode === LabelingMode.BrushLabeling;
  }

  setBrushMode() {
    this.labelingMode = LabelingMode.BrushLabeling;
  }
}
