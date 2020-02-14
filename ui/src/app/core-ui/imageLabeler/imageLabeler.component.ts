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

import { Component, OnInit, ViewChild } from "@angular/core";
import { CocoFormat } from "../../core-model/coco/Coco.format";

@Component({
  selector: 'sp-image-labeler',
  templateUrl: './imageLabeler.component.html',
  styleUrls: ['./imageLabeler.component.css']
})
export class ImageLabelerComponent implements OnInit {

  @ViewChild('test') canvasRef;
  private canvas;
  private context;

  //mouse position
  private last_mousex = 0;
  private last_mousey = 0;
  private last_mousexTransformed = 0;
  private last_mouseyTransformed = 0;

  private mousedown = false;

  private reactHeight = 0;
  private reactWidth = 0;

  private canvasWidth;
  private canvasHeight;

  private source;

  private coco;
  private label = "Car";

  //scale
  private startScale = 1;
  private scale: number = this.startScale;

  ngOnInit(): void {

    this.canvas = this.canvasRef.nativeElement;
    this.context = this.canvas.getContext('2d');
    this.canvasWidth = this.canvas.widget;
    this.canvasHeight= this.canvas.height;


    this.source = new Image();

    this.source.onload = () => {
      this.context.drawImage(this.source, 0, 0,);
      //  context.reactWidth = source.reactWidth;
      //  context.height = source.height;
      this.coco = new CocoFormat("Test.png", this.source.width, this.source.height);
      //this.context.strokeRect(0, 0, 20, 30);

    };
    this.source.src = 'https://cdn.pixabay.com/photo/2017/10/29/21/05/bridge-2900839_1280.jpg';
    this.context.lineWidth = 2;
  }

  imageMouseDown(e) {
    let mousePos = this.getMousePosScreen(e.clientX, e.clientY);
    this.last_mousex = mousePos[0];
    this.last_mousey = mousePos[1];
    let mousePosTransformed = this.getMousePosTransformed(e.clientX, e.clientY);
    this.last_mousexTransformed = mousePosTransformed[0];
    this.last_mouseyTransformed = mousePosTransformed[1];
    this.mousedown = true;
  }

  imageMouseMove(e) {
    let mousePos = this.getMousePosScreen(e.clientX, e.clientY);
    let mousex = mousePos[0];
    let mousey = mousePos[1];
    if(this.mousedown) {
      this.draw();

      this.reactWidth = mousex - this.last_mousex;
      this.reactHeight = mousey - this.last_mousey;
      this.context.strokeStyle = this.getColor(this.label);
      this.context.beginPath();
      this.context.rect(this.last_mousex, this.last_mousey, this.reactWidth, this.reactHeight);
      this.context.fillText(this.label, this.last_mousex, this.last_mousey + this.reactHeight);
      this.context.stroke();
    }
  }

  imageMouseUp(e) {
    this.mousedown = false;

    let categoryId = this.coco.getCategoryId(this.label, "");

    let mousePosTransformed = this.getMousePosTransformed(e.clientX, e.clientY);
    let mousexTransformed = mousePosTransformed[0];
    let last_mouseyTransformed = mousePosTransformed[1];

    let reactWidth = mousexTransformed - this.last_mousexTransformed;
    let reactHeight = last_mouseyTransformed - this.last_mouseyTransformed;

    this.coco.addReactAnnotation(this.last_mousexTransformed, this.last_mouseyTransformed, reactWidth, reactHeight, categoryId);
    console.log(this.last_mousexTransformed, this.last_mouseyTransformed, reactWidth, reactHeight, categoryId);

  }

  draw() {
    let newWidth = this.canvasWidth * this.scale;
    let newHeight = this.canvasHeight * this.scale;

    this.context.save();
    this.context.translate(-((newWidth - this.canvasWidth) / 2), -((newHeight - this.canvasHeight) / 2));
    this.context.scale(this.scale, this.scale);

    this.context.drawImage(this.source, 0, 0,);

    for(let annotation of this.coco.annotations) {
      console.log(this.coco.annotations[0].bbox);
      let label = this.coco.getCategoryName(annotation.category_id);
      this.context.strokeStyle = this.getColor(label);
      this.context.beginPath();
      //TODO if not BBox
      let bbox = annotation.bbox;
      this.context.rect(bbox[0], bbox[1], bbox[2], bbox[3]);
      this.context.fillText(label, bbox[0], bbox[1] + bbox[3]);
      this.context.stroke();
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
      parseInt(clientX - this.canvas.getBoundingClientRect().left),
      parseInt(clientY - this.canvas.getBoundingClientRect().top ),
    ]
  }

  getMousePosTransformed(clientX, clientY): [any, any] {
    return [
      parseInt((clientX - this.canvas.getBoundingClientRect().left) / this.scale),
      parseInt((clientY - this.canvas.getBoundingClientRect().top) / this.scale),
    ]
  }



  }