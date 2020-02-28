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

import { AfterViewInit, Component, HostListener, OnInit, ViewChild } from "@angular/core";
import { ImageTranslationUtil } from "./util/imageTranslation.util";
import { DatalakeRestService } from "../../core-services/datalake/datalake-rest.service";
import { ColorUtil } from "./util/color.util";
import { ImageAnnotation } from "./annotation/imageAnnotation";
import { InteractionMode } from "./interactionMode";
import { ImageClassification } from "./classification/imageClassification";

@Component({
  selector: 'sp-image-labeler',
  templateUrl: './imageLabeler.component.html',
  styleUrls: ['./imageLabeler.component.css']
})
export class ImageLabelerComponent implements OnInit, AfterViewInit {

  @ViewChild('canvas') canvasRef;
  private canvas;
  private context;

  private isLeftMouseDown = false;
  private isRightMouseDown = false;

  //canvas properties
  private canvasWidth;
  private canvasHeight;
  private isHoverComponent;

  //image
  private image;
  private imageTranslationX = 0;
  private imageTranslationY = 0;

  public labels;
  public labelCategories;
  public labelCategory;
  private selectedLabel;

  public interactionMode: InteractionMode = InteractionMode.imageAnnotate;

  //scale
  private scale: number = 1;

  constructor(private restService: DatalakeRestService, public imageAnnotation: ImageAnnotation, public imageClassification: ImageClassification) {

  }

  ngOnInit(): void {
    //1. get Image Paths
    //2. get Images

    //3. get Labels
    this.labels = this.restService.getLabels();
    this.labelCategories = Object.keys(this.restService.getLabels());
    this.labelCategory = this.labelCategories[1];
    this.selectedLabel = this.labels[this.labelCategory][0];

    this.isHoverComponent = false;
  }

  ngAfterViewInit() {
    this.canvas = this.canvasRef.nativeElement;
    this.context = this.canvas.getContext('2d');
    this.canvasWidth = this.canvas.width;
    this.canvasHeight= this.canvas.height;

    this.canvas.addEventListener('contextmenu', event => event.preventDefault());
    this.canvas.addEventListener('DOMMouseScroll',event => this.scroll(event),false);
    this.canvas.addEventListener('mousewheel',event => this.scroll(event),false);

    this.changeImage('https://cdn.pixabay.com/photo/2017/10/29/21/05/bridge-2900839_1280.jpg');
    this.context.lineWidth = 2;
  }

  changeImage(url) {
    this.image = new Image();

    this.image.onload = () => {
      this.imageAnnotation.newImage("Test.png", this.image.width, this.image.height);
      this.imageClassification.newImage();
      console.log('Image width: ' + this.image.width);
      console.log('Image height: ' + this.image.height);
      this.scale = Math.min(1, this.canvasWidth / this.image.width, this.canvasHeight / this.image.height);
      console.log('Set Scale to: ' + this.scale);
      this.draw();
    };
    this.image.src = url;
  }

  imageMouseDown(e) {
    if (e.which == 1) {
      //left click
      this.isLeftMouseDown = true;

      switch (this.interactionMode) {
        case InteractionMode.imageAnnotate: this.imageAnnotation.mouseDown(this.getImageCords(e.clientX, e.clientY), this.scale);
          break;
      }

    } else if (e.which == 2) {
      //middle click
    } else {
      //right click
      this.isRightMouseDown = true;
      ImageTranslationUtil.mouseDown(this.getCanvasCords(e.clientX, e.clientY), this.imageTranslationX, this.imageTranslationY);
    }
  }

  imageMouseMove(e) {
    //TODO solve duplicated code
    if (this.imageAnnotation.isPolygonLabeling()) {
      this.startDraw();
      let imageXShift = (this.canvasWidth - this.image.width) / 2;
      let imageYShift =(this.canvasHeight - this.image.height) / 2;

      this.imageAnnotation.annotationDraw(imageXShift, imageYShift, this.scale, this.context);
      this.imageAnnotation.mouseMover(this.getImageCords(e.clientX, e.clientY), imageXShift, imageYShift,
        this.context, this.selectedLabel);
      this.endDraw();
    } else if (this.isLeftMouseDown) {

      switch (this.interactionMode) {
        case InteractionMode.imageAnnotate: {
            this.startDraw();
            let imageXShift = (this.canvasWidth - this.image.width) / 2;
            let imageYShift =(this.canvasHeight - this.image.height) / 2;
            this.imageAnnotation.annotationDraw(imageXShift, imageYShift, this.scale, this.context);
            this.imageAnnotation.mouseMover(this.getImageCords(e.clientX, e.clientY), imageXShift, imageYShift,
              this.context, this.selectedLabel);
            this.endDraw();
          }
          break;
      }

    } else if (this.isRightMouseDown) {
      let translation = ImageTranslationUtil.mouseMove(this.getCanvasCords(e.clientX, e.clientY));
      this.imageTranslationX = translation[0];
      this.imageTranslationY = translation[1];
      this.draw();
    } else {
      this.imageAnnotation.annotationHovering(this.getImageCords(e.clientX, e.clientY), this.scale);
      this.draw();
    }

  }

  imageMouseUp(e) {
    if (this.isLeftMouseDown) {
      this.isLeftMouseDown = false;
      switch (this.interactionMode) {
        case InteractionMode.imageAnnotate: {
            this.imageAnnotation.mouseUp(this.getImageCords(e.clientX, e.clientY), this.selectedLabel, this.labelCategory);
            if (!this.imageAnnotation.isPolygonLabeling()) {
              this.draw()
            }
        }
          break;
      }
    }
    if (this.isRightMouseDown) {
      this.isRightMouseDown = false;
    }
  }

  dblclick (e) {
    if (this.interactionMode = InteractionMode.imageAnnotate) {
      this.imageAnnotation.dblclick(this.getImageCords(e.clientX, e.clientY), this.selectedLabel, this.labelCategory);
    }
  }

  startDraw() {
    this.context.clearRect(0, 0, this.canvasWidth, this.canvasHeight);

    let newWidth = this.canvasWidth * this.scale;
    let newHeight = this.canvasHeight * this.scale;

    this.context.save();

    this.context.translate(-((newWidth - this.canvasWidth) / 2) + this.imageTranslationX,
      -((newHeight - this.canvasHeight) / 2) + this.imageTranslationY);
    this.context.scale(this.scale, this.scale);

    this.context.drawImage(this.image, this.canvasWidth / 2 - this.image.width / 2, this.canvasHeight / 2 - this.image.height / 2);
  }


  endDraw() {
    this.context.restore();

    this.context.beginPath();
    this.context.globalAlpha = 0.8;
    this.context.fillStyle = 'lightgrey';
    this.context.fillRect(0, 0, 50, 20);
    this.context.globalAlpha = 1;
    this.context.font = '12px Arial';
    this.context.fillStyle = 'black';
    this.context.fillText((Math.round(this.scale  * 100) / 100).toFixed(2) + " x", 5,15);
    this.context.stroke();
  }

  draw() {
    this.startDraw();
    let imageXShift = (this.canvasWidth - this.image.width) / 2;
    let imageYShift =(this.canvasHeight - this.image.height) / 2;
    this.imageAnnotation.annotationDraw(imageXShift, imageYShift, this.scale, this.context);
    this.endDraw();
  }

  @HostListener('document:keydown', ['$event'])
  handleShortCuts(event: KeyboardEvent) {
    if (this.isHoverComponent) {
      if (event.code.toLowerCase().includes('digit')) {
        // Number
        let value = Number(event.key);
        if (value != 0 && value <= this.labels[this.labelCategory].length) {
          this.selectLabel(this.labels[this.labelCategory][value - 1]);
        }
      } else {
        let key = event.key;
        switch (key.toLowerCase()) {
          case 'q': alert('Previous image'); //TODO
            break;
          case 'e': alert('Next image'); //TODOd
            break;
          case 'w': this.imageTranslationY += 5; this.draw();
            break;
          case 'a': this.imageTranslationX += 5; this.draw();
            break;
          case 's': this.imageTranslationY -= 5; this.draw();
            break;
          case 'd': this.imageTranslationX -= 5; this.draw();
            break;
          case 'delete': this.imageAnnotation.deleteSelectedAnnotation();
            this.draw();
        }
        if (this.interactionMode == InteractionMode.imageAnnotate) {
          switch (key.toLowerCase()) {
            case 'r': this.imageAnnotation.setReactMode();
              break;
            case 'f': this.imageAnnotation.setPolygonMode();
              this.draw();
          }
        }
      }
    }
  }

  getColor(label) {
    return ColorUtil.getColor(label);
  }

  getCanvasCords(clientX, clientY): [any, any] {
    return [
      Math.floor(clientX - this.canvas.getBoundingClientRect().left),
      Math.floor(clientY - this.canvas.getBoundingClientRect().top),
    ]
  }

  getImageCords(clientX, clientY): [any, any] {
    return [
      Math.floor(((clientX - this.canvas.getBoundingClientRect().left) / this.scale) - ((this.canvasWidth / this.scale - this.image.width) / 2) - (this.imageTranslationX / this.scale)),
      Math.floor(((clientY - this.canvas.getBoundingClientRect().top) / this.scale) - ((this.canvasHeight / this.scale - this.image.height) / 2) - (this.imageTranslationY / this.scale)),
    ]
  }

  //UI Callbacks

  scroll(e) {
    this.scale += e.wheelDeltaY * (1/6000);
    this.draw();
  }

  zoomin()
  {
    this.scale += 0.05;
    this.draw();
  }
  zoomout()
  {
    this.scale -= 0.05;
    this.draw();
  }

  selectLabel(label) {
    this.selectedLabel = label;
    switch (this.interactionMode) {
      case InteractionMode.imageClassify: this.imageClassification.addClass(label);
        break;
    }
  }

  enterCanvas() {
    this.isHoverComponent = true;
  }

  leaveCanvas() {
    this.isHoverComponent = false;
  }

  getLabelById(id) {
    return this.imageAnnotation.getLabelById(id);
  }

  setImageViewInteractionMode() {
    this.interactionMode = InteractionMode.imageViewing;
  }

  setImageAnnotateInteractionMode() {
    this.interactionMode = InteractionMode.imageAnnotate;
  }

  setImageClassifyInteractionMode() {
    this.interactionMode = InteractionMode.imageClassify;
  }

  isImageViewingMode() {
    return this.interactionMode == InteractionMode.imageViewing;
  }

  isImageAnnotateMode() {
    return this.interactionMode == InteractionMode.imageAnnotate;

  }

  isImageClassifyMode() {
    return this.interactionMode == InteractionMode.imageClassify;
  }
  
  //annotations ui callbacks
  
  enterAnnotation(annotation) {
    annotation.isHovered = true;
    this.draw()
  }

  leaveAnnotation(annotation) {
    annotation.isHovered = false;
    this.draw()
  }
  
  deleteAnnotation(annotation) {
   this.imageAnnotation.deleteAnnotation(annotation);
   this.draw();
  }

  changeLabel(annonation, label, category) {
    this.imageAnnotation.changeLabel(annonation, label, category);
    this.draw();
  }

  //classification ui callbacks
  
  removeClass(clazz) {
    this.imageClassification.removeClass(clazz);
  }

}
