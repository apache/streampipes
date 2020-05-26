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
 */

import { AfterViewInit, Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import Konva from 'konva';
import { ICoordinates } from '../../model/coordinates';

@Component({
  selector: 'sp-image-container',
  templateUrl: './image-container.component.html',
  styleUrls: ['./image-container.component.css']
})
export class ImageContainerComponent implements OnInit, AfterViewInit {

  @Input()
  set imageSrc(src) {
    this.loadImage(src);
  }

  @Output()
  childRedraw: EventEmitter<[Konva.Layer, ICoordinates]> = new EventEmitter<[Konva.Layer, ICoordinates]>();
  @Output()
  mouseDownLeft: EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]> = new EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]>();
  @Output()
  mouseMove: EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]> = new EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]>();
  @Output()
  mouseMoveLeft: EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]> = new EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]>();
  @Output()
  mouseUpLeft: EventEmitter<[Konva.Layer, Konva.Layer, ICoordinates, ICoordinates]> = new EventEmitter<[Konva.Layer, Konva.Layer, ICoordinates, ICoordinates]>();
  @Output()
  shortCut: EventEmitter<string> = new EventEmitter<string>();
  @Output()
  dbclick: EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]> = new EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]>();
  @Output()
  mouseDownRight: EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]> = new EventEmitter<[Konva.Layer, ICoordinates, ICoordinates]>();


  private image;

  private mainCanvasStage: Konva.Stage;
  private imageLayer: Konva.Layer;
  private annotationLayer: Konva.Layer;
  private drawLayer: Konva.Layer;

  private scale: number;

  private imageShift: ICoordinates;
  private lastImageTranslation: ICoordinates;
  private lastImagePointerPosition: ICoordinates;

  private isLeftMouseDown: boolean;
  private isMiddleMouseDown: boolean;
  private isRightMouseDown: boolean;

  private isHoverComponent: boolean;

  constructor() { }

  ngOnInit(): void {
    this.scale = 1;
    this.imageShift = {x: 0, y: 0};
    this.isLeftMouseDown = false;
    this.isMiddleMouseDown = false;
    this.isRightMouseDown = false;
    this.isHoverComponent = false;
  }

  ngAfterViewInit(): void {
    this.reset();
  }

  reset() {
    this.scale = 1;
    this.imageShift = {x: 0, y: 0};
    // TODO fit to parent
    this.mainCanvasStage = new Konva.Stage({
      container: 'canvas-container',
      width: 800,
      height: 500
    });
    this.registerEventHandler();

  }

  loadImage(src) {
    this.reset();
    this.image = new window.Image();

    this.image.onload = () => {
      this.scale = Math.min(1, this.mainCanvasStage.width() / this.image.width, this.mainCanvasStage.height() / this.image.height);
      this.initLayers();
      this.redrawAll();
    };
    this.image.src = src;
  }

  getShift() {
    if (this.imageLayer !== undefined) {
      const position = this.imageLayer.getChildren().toArray()[0].getPosition();
      return {x: position.x, y: position.y};
    }
  }
  /* mouse handler */

  imageMouseDown(e) {
    const button = e.evt.which;
    if (button === 1) {
      // left click
      this.isLeftMouseDown = true;
      this.mouseDownLeft.emit([this.drawLayer, this.getShift(), this.getImagePointerPosition()]);
      this.drawLayer.batchDraw();
    } else if (button === 2) {
      // middle click
      this.isMiddleMouseDown = true;
      this.mainCanvasStage.container().style.cursor = 'move';
      this.lastImagePointerPosition = this.getImagePointerPosition();
      this.lastImageTranslation = this.imageShift;
    } else if (button === 3) {
      // right click
      this.isRightMouseDown = true;
      this.mouseDownRight.emit([this.drawLayer, this.getShift(), this.getImagePointerPosition()]);
    }
  }

  imageMouseMove(e) {
    if (this.isLeftMouseDown) {
      this.drawLayer.destroyChildren();
      this.mouseMoveLeft.emit([this.drawLayer, this.getShift(), this.getImagePointerPosition()]);
      this.drawLayer.batchDraw();
    } else if (this.isMiddleMouseDown) {
      const imagePointerPosition = this.getImagePointerPosition();
      this.imageShift.x = this.lastImageTranslation.x + (imagePointerPosition.x - this.lastImagePointerPosition.x);
      this.imageShift.y = this.lastImageTranslation.y + (imagePointerPosition.y - this.lastImagePointerPosition.y);
      this.lastImagePointerPosition = this.getImagePointerPosition();
      this.lastImageTranslation = this.imageShift;
      this.shiftViewContent();
    } else {
      if (this.drawLayer !== undefined) { this.drawLayer.destroyChildren(); }
      this.mouseMove.emit([this.drawLayer, this.getShift(), this.getImagePointerPosition()]);
      if (this.drawLayer !== undefined) { this.drawLayer.destroyChildren(); }
    }
  }

  imageMouseUp(e) {
    if (this.isLeftMouseDown) {
      this.isLeftMouseDown = false;
      this.drawLayer.destroyChildren();
      this.mouseUpLeft.emit([this.annotationLayer, this.drawLayer, this.getShift(), this.getImagePointerPosition()]);
      this.drawLayer.batchDraw();
      this.annotationLayer.batchDraw();
    }
    if (this.isMiddleMouseDown) {
      this.isMiddleMouseDown = false;
      this.mainCanvasStage.container().style.cursor = 'default';
    }
  }

  dblclick (e) {
    this.drawLayer.destroyChildren();
    this.drawLayer.batchDraw();
    this.dbclick.emit([this.annotationLayer, this.getShift(), this.getImagePointerPosition()]);
    this.annotationLayer.batchDraw();
  }

  /* Draw */

  redrawAll() {
    if (this.drawLayer !== undefined) {
      this.drawLayer.destroyChildren();
    }
    if (this.annotationLayer !== undefined) {
      this.annotationLayer.destroyChildren();
    }
    this.childRedraw.emit([this.annotationLayer, this.getShift()]);
    this.shiftViewContent();
  }

  shiftViewContent() {
    const newWidth = this.mainCanvasStage.width() * this.scale;
    const newHeight = this.mainCanvasStage.height() * this.scale;

    this.mainCanvasStage.position({
      x: -((newWidth - this.mainCanvasStage.width()) / 2) + this.imageShift.x,
      y: -((newHeight - this.mainCanvasStage.height()) / 2) + this.imageShift.y
    });
    this.mainCanvasStage.scale({ x: this.scale, y: this.scale });

    this.mainCanvasStage.batchDraw();
  }

  initLayers() {
    this.imageLayer = new Konva.Layer();
    const konvaImage = new Konva.Image({
      image: this.image,
      x: this.mainCanvasStage.width() / 2 - this.image.width / 2,
      y: this.mainCanvasStage.height() / 2 - this.image.height / 2,
    });
    this.imageLayer.add(konvaImage);
    this.imageLayer.clearBeforeDraw();

    this.annotationLayer = new Konva.Layer();
    this.drawLayer = new Konva.Layer();

    this.mainCanvasStage.add(this.imageLayer);
    this.mainCanvasStage.add(this.annotationLayer);
    this.mainCanvasStage.add(this.drawLayer);
  }

  @HostListener('document:keydown', ['$event'])
  handleShortCuts(e) {
    const key = e.key;
    this.shortCut.emit(key.toLowerCase());
    if (this.isHoverComponent) {
      switch (key.toLowerCase()) {
          case 'w': this.imageShift.y -= 5; this.redrawAll();
            break;
          case 'a': this.imageShift.x -= 5; this.redrawAll();
            break;
          case 's': this.imageShift.y += 5; this.redrawAll();
            break;
          case 'd': this.imageShift.x += 5; this.redrawAll();
            break;
        }
    }
  }

  getPointerPosition(): ICoordinates {
    return this.mainCanvasStage.getPointerPosition();
  }

  getImagePointerPosition(): ICoordinates {
    const x = Math.floor((this.getPointerPosition().x / this.scale) -
      ((this.mainCanvasStage.width() / this.scale - this.image.width) / 2) - (this.imageShift.x / this.scale));
    const y = Math.floor((this.getPointerPosition().y / this.scale) -
      ((this.mainCanvasStage.height() / this.scale - this.image.height) / 2) - (this.imageShift.y / this.scale));
    return {x, y};
  }

  getImagePositionFromPosition(posistion: ICoordinates): ICoordinates {
    const x = Math.floor((posistion.x / this.scale) -
      ((this.mainCanvasStage.width() / this.scale - this.image.width) / 2) - (this.imageShift.x / this.scale));
    const y = Math.floor((posistion.y / this.scale) -
      ((this.mainCanvasStage.height() / this.scale - this.image.height) / 2) - (this.imageShift.y / this.scale));
    return {x, y};
  }

  registerEventHandler() {
    this.mainCanvasStage.on('wheel', e => this.scroll(e));
    this.mainCanvasStage.on('contextmenu', e => e.evt.preventDefault());
    this.mainCanvasStage.on('mousedown', e => this.imageMouseDown(e));
    this.mainCanvasStage.on('mousemove', e => this.imageMouseMove(e));
    this.mainCanvasStage.on('mouseup', e => this.imageMouseUp(e));
    this.mainCanvasStage.on('mouseover', e => this.isHoverComponent = true);
    this.mainCanvasStage.on('mouseout', e => this.isHoverComponent = false);
    this.mainCanvasStage.on('dblclick', e => this.dblclick(e));
    this.mainCanvasStage.on('dbclick', e => this.dblclick(e));
  }

  scroll(e) {
    e.evt.preventDefault();
    this.scale += e.evt.wheelDeltaY * (1 / 6000);
    this.redrawAll();
  }
}
