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
import { CocoFormat } from "../../core-model/coco/Coco.format";
import { InteractionMode } from "./interactionMode";
import { ReactLabelingHelper } from "./helper/reactLabeling.helper";
import { ImageTranslationHelper } from "./helper/imageTranslation.helper";
import { DatalakeRestService } from "../../core-services/datalake/datalake-rest.service";

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
  private isHoverCanvas;

  //image
  private image;
  private imageTranslationX = 0;
  private imageTranslationY = 0;

  public coco;
  public labels;
  public labelCategories;
  public labelCategory;
  private selectedLabel;

  //actual interaction mode
  private interactionMode: InteractionMode = InteractionMode.ReactLabeling;

  //scale
  private scale: number = 1;

  private selectedAnnotation;

  constructor(private restService: DatalakeRestService) {

  }

  ngOnInit(): void {
    //1. get Image Paths
    //2. get Images

    //3. get Labels
    this.labels = this.restService.getLabels();
    this.labelCategories = Object.keys(this.restService.getLabels());
    this.labelCategory = this.labelCategories[1];
    this.selectedLabel = this.labels[this.labelCategory][0]

    this.isHoverCanvas = false;
  }

  ngAfterViewInit() {
    this.canvas = this.canvasRef.nativeElement;
    this.context = this.canvas.getContext('2d');
    this.canvasWidth = this.canvas.width;
    this.canvasHeight= this.canvas.height;

    this.canvas.addEventListener('contextmenu', event => event.preventDefault());
    this.canvas.addEventListener('DOMMouseScroll',event => this.scroll(event),false);
    this.canvas.addEventListener('mousewheel',event => this.scroll(event),false);
    this.canvas.addEventListener('keydown',event => console.log(event),false);

    this.image = new Image();

    this.image.onload = () => {
      this.coco = new CocoFormat("Test.png", this.image.width, this.image.height);
      console.log('Image width: ' + this.image.width);
      console.log('Image height: ' + this.image.height);
      this.scale = Math.min(1, this.canvasWidth / this.image.width, this.canvasHeight / this.image.height);
      console.log('Set Scale to: ' + this.scale);
      this.draw();
    };
    this.image.src = 'https://cdn.pixabay.com/photo/2017/10/29/21/05/bridge-2900839_1280.jpg';
    this.context.lineWidth = 2;
  }

  imageMouseDown(e) {
    if (e.which == 1) {
      //left click

      //check if Annotation is click or not and set interaction Mode
      this.selectedAnnotation = undefined;
      for(let annotation of this.coco.annotations) {
        let clicked = annotation.checkIfInArea(this.getImageCords(e.clientX, e.clientY));
        if (clicked) {
          annotation.isHovered = false;
          this.selectedAnnotation = annotation;
        }
        annotation.isSelected = clicked;
      }
      if (this.selectedAnnotation !== undefined) {
        this.interactionMode = InteractionMode.ReactResize;
      } else {
        this.interactionMode = InteractionMode.ReactLabeling;
      }

      if (this.interactionMode == InteractionMode.ReactLabeling) {
        ReactLabelingHelper.mouseDownCreate(this.getImageCords(e.clientX, e.clientY));
      } else if (this.interactionMode == InteractionMode.ReactResize){
        this.draw();
        ReactLabelingHelper.mouseDownTransform(this.getImageCords(e.clientX, e.clientY),
          this.selectedAnnotation, this.scale)
      }
      this.isLeftMouseDown = true;

    } else if (e.which == 2) {
      //middle click
    } else {
      //right click
      this.isRightMouseDown = true;
      ImageTranslationHelper.mouseDown(this.getCanvasCords(e.clientX, e.clientY), this.imageTranslationX, this.imageTranslationY);
    }
  }

  imageMouseMove(e) {
    if (this.isLeftMouseDown) {

      if (this.interactionMode == InteractionMode.ReactLabeling) {
        this.startDraw();
        this.annotationDraw();
        let imageXShift = (this.canvasWidth - this.image.width) / 2;
        let imageYShift =(this.canvasHeight - this.image.height) / 2;
        ReactLabelingHelper.mouseMoveCreate(this.getImageCords(e.clientX, e.clientY), imageXShift, imageYShift,
          this.context, this.selectedLabel, this.getColor(this.selectedLabel));
        this.endDraw();
      } else if (this.interactionMode == InteractionMode.ReactResize){
        this.startDraw();
        this.annotationDraw();
        ReactLabelingHelper.mouseMoveTansform(this.getImageCords(e.clientX, e.clientY), this.selectedAnnotation);
        this.endDraw();
      }
    } else if (this.isRightMouseDown) {
      let translation = ImageTranslationHelper.mouseMove(this.getCanvasCords(e.clientX, e.clientY));
      this.imageTranslationX = translation[0];
      this.imageTranslationY = translation[1];
      this.draw();
    } else {
      for(let annotation of this.coco.annotations) {
        annotation.isHovered = false;
        if (!annotation.isSelected) {
          annotation.isHovered = annotation.checkIfInArea(this.getImageCords(e.clientX, e.clientY))
        }
      }
      this.draw();
    }

  }

  imageMouseUp(e) {
    if (this.isLeftMouseDown) {
      this.isLeftMouseDown = false;

      let labelId = this.coco.getLabelId(this.selectedLabel, this.labelCategory);
      if (this.interactionMode == InteractionMode.ReactLabeling) {
        ReactLabelingHelper.mouseUpCreate(this.getImageCords(e.clientX, e.clientY), this.coco, labelId);
      }
      this.draw()
    }
    if (this.isRightMouseDown) {
      this.isRightMouseDown = false;
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

  annotationDraw() {
    for(let annotation of this.coco.annotations) {
      let label = this.coco.getLabelById(annotation.category_id);
      //TODO if not BBox
      let imageXShift = (this.canvasWidth - this.image.width) / 2;
      let imageYShift= (this.canvasHeight - this.image.height) / 2;
      ReactLabelingHelper.draw(annotation, label, this.context, this.getColor(label), imageXShift, imageYShift, this.scale)
    }
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
    this.annotationDraw();
    this.endDraw();
  }

  @HostListener('document:keydown', ['$event'])
  handleShortCuts(event: KeyboardEvent) {
    console.log(event.key);
    if (this.isHoverCanvas) {
      if (event.code.toLowerCase().includes('digit')) {
        // Number
        let value = Number(event.key);
        if (value != 0 && value <= this.labels[this.labelCategory].length) {
          this.selectedLabel = this.labels[this.labelCategory][value - 1]
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
          case 'delete': this.deleteAnnotation(this.selectedAnnotation);
        }
      }
    }
  }

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
    this.interactionMode = InteractionMode.ReactLabeling;
  }

  getColor(label) {
    var hash = 0;
    for (var i = 0; i < label.length; i++) {
      hash = label.charCodeAt(i) + ((hash << 5) - hash);
    }
    var colour = '#';
    for (var i = 0; i < 3; i++) {
      var value = (hash >> (i * 8)) & 0xFF;
      colour += ('00' + value.toString(16)).substr(-2);
    }
    return colour;
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

  enterAnnotation(annotation) {
    annotation.isHovered = true;
    this.draw()
  }

  leaveAnnotation(annotation) {
    annotation.isHovered = false;
    this.draw()
  }

  enterCanvas() {
    this.isHoverCanvas = true;
  }

  leaveCanvas() {
    this.isHoverCanvas = false;
  }

  deleteAnnotation(annotation) {
    if (annotation !== undefined) {
      this.coco.removeAnnotation(annotation.id);
      this.draw();
    }
  }

  changeLabel(annonation, label, category) {
    console.log(label, category)
    let labelId = this.coco.getLabelId(label, category);
    annonation.category_id = labelId;
    this.draw();
  }




}