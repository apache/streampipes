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
import { CocoFormat } from "../../core-model/coco/Coco.format";
import { InteractionMode } from "./interactionMode";
import { ReactLabelingHelper } from "./helper/reactLabeling.helper";
import { ImageTranslationHelper } from "./helper/imageTranslation.helper";

@Component({
  selector: 'sp-image-labeler',
  templateUrl: './imageLabeler.component.html',
  styleUrls: ['./imageLabeler.component.css']
})
export class ImageLabelerComponent implements OnInit, AfterViewInit {

  @ViewChild('canvas') canvasRef;
  private canvas;
  private context;

  private isMouseDown = false;

  //canvas properties
  private canvasWidth;
  private canvasHeight;

  //image
  private image;
  private imageTranslationX = 0;
  private imageTranslationY = 0;

  private coco;
  private label = "Car";

  //actual interaction mode
  private interactionMode: InteractionMode = InteractionMode.ReactLabeling;

  //scale
  private scale: number = 1;

  ngOnInit(): void {

  }

  ngAfterViewInit() {
    this.canvas = this.canvasRef.nativeElement;
    this.context = this.canvas.getContext('2d');
    this.canvasWidth = this.canvas.width;
    this.canvasHeight= this.canvas.height;

    this.image = new Image();

    this.image.onload = () => {
      this.coco = new CocoFormat("Test.png", this.image.width, this.image.height);
      console.log('Image width: ' + this.image.width);
      console.log('Image height: ' + this.image.height);
      this.scale = Math.min(1, this.canvasWidth / this.image.width, this.canvasHeight / this.image.height);
      console.log('Set Scale to: ' + this.scale);
      this.draw();
    };
    //this.image.src = 'https://cdn.pixabay.com/photo/2017/10/29/21/05/bridge-2900839_1280.jpg';
    this.image.src = 'https://www.hamburg.de/contentblob/1740056/7308ff64cbb71631d0463f5b6a34471c/data/bild-kohoevedstrasse3.jpg';
    // this.source.src = 'https://previews.123rf.com/images/sahua/sahua1503/sahua150300006/38262839-autobahn-stra%C3%9Fe-schilder-autos-und-konstruktionen.jpg';
    this.context.lineWidth = 2;
  }

  imageMouseDown(e) {
       if (this.interactionMode == InteractionMode.ReactLabeling) {
      ReactLabelingHelper.mouseDown(this.getMousePosScreen(e.clientX, e.clientY),
        this.getMousePosTransformed(e.clientX, e.clientY));
    } else if (this.interactionMode == InteractionMode.Translate) {
      ImageTranslationHelper.mouseDown(this.getMousePosScreen(e.clientX, e.clientY), this.imageTranslationX, this.imageTranslationY)
    }
    this.isMouseDown = true;
  }

  imageMouseMove(e) {
    if(this.isMouseDown) {

      if (this.interactionMode == InteractionMode.ReactLabeling) {
        this.draw();
        ReactLabelingHelper.mouseMove(this.getMousePosScreen(e.clientX, e.clientY),
          this.getMousePosTransformed(e.clientX, e.clientY), this.context, this.label, this.getColor(this.label));
      } else if (this.interactionMode == InteractionMode.Translate) {
        let translation = ImageTranslationHelper.mouseMove(this.getMousePosScreen(e.clientX, e.clientY));
        this.imageTranslationX = translation[0];
        this.imageTranslationY = translation[1];
        this.draw();
      }
    }

  }

  imageMouseUp(e) {
    this.isMouseDown = false;

    let labelId = this.coco.getLabelId(this.label, "");

    if (this.interactionMode == InteractionMode.ReactLabeling) {
      ReactLabelingHelper.mouseUp(this.getMousePosScreen(e.clientX, e.clientY),
        this.getMousePosTransformed(e.clientX, e.clientY), this.coco, labelId);
    }
  }

  draw() {
    this.context.clearRect(0, 0, this.canvasWidth, this.canvasHeight);

    let newWidth = this.canvasWidth * this.scale;
    let newHeight = this.canvasHeight * this.scale;

    this.context.save();


    this.context.translate(-((newWidth - this.canvasWidth) / 2) + this.imageTranslationX,
      -((newHeight - this.canvasHeight) / 2) + this.imageTranslationY);
    this.context.scale(this.scale, this.scale);

    this.context.drawImage(this.image, this.canvasWidth / 2 - this.image.width / 2, this.canvasHeight / 2 - this.image.height / 2);

    for(let annotation of this.coco.annotations) {
      //console.log(this.coco.annotations[0].bbox);
      let label = this.coco.getLabelById(annotation.category_id);
      //TODO if not BBox


      ReactLabelingHelper.draw(annotation, label, this.context, this.getColor(label),
        ((this.canvasWidth - this.image.width) / 2),
        ((this.canvasHeight - this.image.height) / 2))
    }
    this.context.restore();
  }

  imageZoom(e) {
    console.log(e);
    let theEvent = e.originalEvent.wheelDelta || e.originalEvent.detail*-1;
    if(theEvent / 120 > 0) {
      this.zoomin();
    } else {
      this.zoomout();
    }
    if (e.preventDefault)
      e.preventDefault();
  }

  zoomin()
  {
    this.scale += 0.01;
    this.draw();
  }
  zoomout()
  {
    this.scale -= 0.01;
    this.draw();
  }

  selectLabel(label) {
    this.label = label;
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

  getMousePosScreen(clientX, clientY): [any, any] {
    return [
      Math.floor(clientX - this.canvas.getBoundingClientRect().left),
      Math.floor(clientY - this.canvas.getBoundingClientRect().top),
    ]
  }

  getMousePosTransformed(clientX, clientY): [any, any] {
    return [
      Math.floor(((clientX - this.canvas.getBoundingClientRect().left) / this.scale) - ((this.canvasWidth / this.scale - this.image.width) / 2) - (this.imageTranslationX / this.scale)),
      Math.floor(((clientY - this.canvas.getBoundingClientRect().top) / this.scale) - ((this.canvasHeight / this.scale - this.image.height) / 2) - (this.imageTranslationY / this.scale)),
    ]
  }

  setInteractionModeTranslate() {
    this.interactionMode = InteractionMode.Translate;
  }



  }