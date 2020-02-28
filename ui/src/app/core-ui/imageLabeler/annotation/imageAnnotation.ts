/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { AnnotationMode } from "./annotationMode";
import { ReactAnnotationUtil } from "./reactAnnotation.util";
import { Injectable } from "@angular/core";
import { CocoFormat } from "../../../core-model/coco/Coco.format";
import { PolygonAnnotationUtil } from "./polygonAnnotation.util";

@Injectable()
export class ImageAnnotation {

  private interactionMode: AnnotationMode = AnnotationMode.ReactLabeling;
  private lastLabelMode: AnnotationMode = AnnotationMode.ReactLabeling;
  private selectedAnnotation;

  private coco: CocoFormat;

  newImage(imageName, width, height) {
    this.coco = new CocoFormat(imageName, width, height);
  }

  mouseDown(imageCord, scale) {
    this.selectedAnnotation = this.checkForSelectedAnnotation(imageCord, this.coco.annotations, scale);

    if (this.interactionMode == AnnotationMode.PolygonTransform){
      PolygonAnnotationUtil.mouseDownTransform(imageCord,
        this.selectedAnnotation, scale)
    } else if (this.interactionMode == AnnotationMode.ReactLabeling) {
      ReactAnnotationUtil.mouseDownCreate(imageCord);
    } else if (this.interactionMode == AnnotationMode.ReactResize){
      ReactAnnotationUtil.mouseDownTransform(imageCord,
        this.selectedAnnotation, scale)
    } else if (this.interactionMode == AnnotationMode.PolygonLabeling){
      PolygonAnnotationUtil.mouseDownCreate(imageCord)
    }
  }

  mouseMover(imageCord, imageXShift, imageYShift, context, label) {
    if (this.interactionMode == AnnotationMode.PolygonTransform){
      PolygonAnnotationUtil.mouseMoveTransform(imageCord, this.selectedAnnotation)
    } else if (this.interactionMode == AnnotationMode.ReactLabeling) {
      ReactAnnotationUtil.mouseMoveCreate(imageCord, imageXShift, imageYShift, context, label);
    } else if (this.interactionMode == AnnotationMode.ReactResize){
      ReactAnnotationUtil.mouseMoveTansform(imageCord, this.selectedAnnotation);
    } else if (this.interactionMode == AnnotationMode.PolygonLabeling){
      PolygonAnnotationUtil.mouseMoveCreate(imageCord, imageXShift, imageYShift, context, label)
    }
  }

  mouseUp(imageCords, label, labelCategory) {
    if (this.interactionMode == AnnotationMode.ReactLabeling) {
      let labelId = this.coco.getLabelId(label, labelCategory);
      ReactAnnotationUtil.mouseUpCreate(imageCords, this.coco, labelId);
    }
  }

  dblclick (imageCords, label, labelCategory) {
    let labelId = this.coco.getLabelId(label, labelCategory);
    PolygonAnnotationUtil.finishCreate(imageCords, this.coco, labelId)
  }

  getSelectedAnnotation() {
    return this.selectedAnnotation;
  }

  deleteAnnotation(annotation) {
    if (annotation !== undefined) {
      this.coco.removeAnnotation(annotation.id);
    }
  }

  deleteSelectedAnnotation() {
    if (this.selectedAnnotation !== undefined) {
      this.coco.removeAnnotation(this.selectedAnnotation.id);
    }
  }

  annotationHovering(imageCords, scale) {
    for(let annotation of this.coco.annotations) {
      annotation.isHovered = false;
      if (!annotation.isSelected) {
        if (annotation.isBox()) {
          annotation.isHovered = ReactAnnotationUtil.checkIfClicked(imageCords, annotation)
        } else {
          annotation.isHovered = PolygonAnnotationUtil.checkIfClicked(imageCords, annotation, scale)
        }
      }
    }
  }

  annotationDraw(imageXShift, imageYShift, scale, context) {
    for(let annotation of this.coco.annotations) {
      let label = this.coco.getLabelById(annotation.category_id);
      if (annotation.isBox()) {
        ReactAnnotationUtil.draw(annotation, label, context, imageXShift, imageYShift, scale)
      } else {
        PolygonAnnotationUtil.draw(annotation, label, context, imageXShift, imageYShift, scale)
      }
    }
  }

  private checkForSelectedAnnotation(imageCord, annotations, scale) {
    let selectedAnnotation = undefined;
    for(let annotation of annotations) {
      let clicked = false;
      if (annotation.isBox()) {
        clicked = ReactAnnotationUtil.checkIfClicked(imageCord, annotation)
      } else {
        clicked = PolygonAnnotationUtil.checkIfClicked(imageCord, annotation, scale)
      }
      if (clicked) {
        annotation.isHovered = false;
        selectedAnnotation = annotation;
      }
      annotation.isSelected = clicked;
    }

    if (selectedAnnotation !== undefined) {
      if (selectedAnnotation.isBox()) {
        this.interactionMode = AnnotationMode.ReactResize;
      } else {
        this.interactionMode = AnnotationMode.PolygonTransform;
      }
    } else {
      this.interactionMode = this.lastLabelMode;
    }

    return selectedAnnotation
  }

  changeLabel(annonation, label, category) {
    let labelId = this.coco.getLabelId(label, category);
    annonation.category_id = labelId;
  }

  getAnnotations() {
    return this.coco?.annotations;
  }

  getLabelById(id) {
    return this.coco.getLabelById(id);
  }

  isReactMode() {
    return this.interactionMode == AnnotationMode.ReactLabeling || this.interactionMode == AnnotationMode.ReactResize;
  }

  isPolygonMode() {
    return this.interactionMode == AnnotationMode.PolygonLabeling || this.interactionMode == AnnotationMode.PolygonTransform;
  }

  setReactMode() {
    this.lastLabelMode = AnnotationMode.ReactLabeling;
    this.interactionMode = AnnotationMode.ReactLabeling;
  }

  setPolygonMode() {
    this.lastLabelMode = AnnotationMode.PolygonLabeling;
    this.interactionMode = AnnotationMode.PolygonLabeling;
  }

  isPolygonLabeling() {
    return PolygonAnnotationUtil.isLabeling;
  }


}
