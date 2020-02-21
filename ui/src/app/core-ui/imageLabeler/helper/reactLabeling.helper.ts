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

export class ReactLabelingHelper {

  private static backgroundHoverAlpha = 0.6;
  private static backgroundAlpha = 0.2;

  //mouse position
  private static lastImageCordX = 0;
  private static lastImageCordY = 0;

  private static isMouseDown = false;

  private static reactHeight = 0;
  private static reactWidth = 0;

  //resize
  private static pressedResizeTopLeft = false;
  private static pressedResizeTopRight = false;
  private static pressedResizeButtonLeft = false;
  private static pressedResizeButtonRight = false;

  static mouseDownCreate(imageCord) {
    this.lastImageCordX = imageCord[0];
    this.lastImageCordY = imageCord[1];
    this.isMouseDown = true;
  }

  static mouseDownResize(imageCord, annotation, scale) {
    //check if pressing movement button
    this.lastImageCordX = imageCord[0];
    this.lastImageCordY = imageCord[1];

    this.pressedResizeTopLeft = false;
    this.pressedResizeTopRight = false;
    this.pressedResizeButtonLeft = false;
    this.pressedResizeButtonRight = false;

    this.setSelectedResizeBox(annotation, scale);
  }

  static mouseMoveCreate(imageCord, imageXShift, imageYShift, context, label, color) {
      if(this.isMouseDown) {
        this.reactWidth = imageCord[0] - this.lastImageCordX;
        this.reactHeight = imageCord[1] - this.lastImageCordY;

        context.strokeStyle = color;
        context.fillStyle = color;
        context.beginPath();
        context.globalAlpha = this.backgroundHoverAlpha;
        context.fillRect(this.lastImageCordX + imageXShift, this.lastImageCordY + imageYShift,
          this.reactWidth, this.reactHeight);
        context.globalAlpha = 1;
        context.strokeRect(this.lastImageCordX + imageXShift, this.lastImageCordY + imageYShift,
          this.reactWidth, this.reactHeight);
        context.fillText(label, this.lastImageCordX + imageXShift, this.lastImageCordY,- 5 );
      }
  }

  static mouseMoveResize(imageCord, annotation) {
    if (this.pressedResizeTopLeft) {
      annotation.bbox[2] += annotation.bbox[0]- imageCord[0];
      annotation.bbox[3] += annotation.bbox[1]- imageCord[1];
      annotation.bbox[0] = imageCord[0];
      annotation.bbox[1] = imageCord[1];
    } else if (this.pressedResizeTopRight) {
      annotation.bbox[2] = Math.abs(annotation.bbox[0]- imageCord[0]);
      annotation.bbox[3] += annotation.bbox[1] - imageCord[1];
      annotation.bbox[1] = imageCord[1];
    } else if (this.pressedResizeButtonLeft) {
      annotation.bbox[2] += annotation.bbox[0]- imageCord[0];
      annotation.bbox[3] = Math.abs(annotation.bbox[1] - imageCord[1]);
      annotation.bbox[0] = imageCord[0];
    } else if (this.pressedResizeButtonRight) {
      annotation.bbox[2] = Math.abs(annotation.bbox[0]- imageCord[0]);
      annotation.bbox[3] = Math.abs(annotation.bbox[1] - imageCord[1]);
    }

  }

  static mouseUpCreate(mousePosTransformed, coco, labelId) {
    this.isMouseDown = false;

    let reactWidth = mousePosTransformed[0] - this.lastImageCordX;
    let reactHeight = mousePosTransformed[1] - this.lastImageCordY;

    if (reactWidth > 0 && reactHeight > 0) {
      coco.addReactAnnotation(this.lastImageCordX, this.lastImageCordY , reactWidth, reactHeight, labelId);
      console.log('Add react Label:', this.lastImageCordX, this.lastImageCordY, reactWidth, reactHeight, labelId)
    }
   }

  static draw(annotation, label, context, color, imageXShift, imageYShift, scale) {
    let bbox = annotation.bbox;
   // this.drawBox(bbox[0] + imageXShift, bbox[1] + imageYShift, bbox[2], bbox[3], label, color, context, annotation.isHovered);

    if (annotation.isHovered) {
      context.globalAlpha = this.backgroundHoverAlpha
    } else if (annotation.isSelected) {
      context.globalAlpha = this.backgroundHoverAlpha;
    } else {
      context.globalAlpha = this.backgroundAlpha;
    }
    context.strokeStyle = color;
    context.fillStyle = color;
    context.beginPath();
    context.fillRect(bbox[0] + imageXShift, bbox[1] + imageYShift, bbox[2], bbox[3]);
    context.globalAlpha = 1;
    context.strokeRect(bbox[0] + imageXShift, bbox[1] + imageYShift, bbox[2], bbox[3]);
    context.font = '12px Arial';
    context.fillText(label, bbox[0] + imageXShift, bbox[1] + imageYShift - 5 );
    //context.stroke();

    if (annotation.isSelected) {
      let size = this.getResizeBoxSize(scale);
      this.drawCircle(bbox[0] + imageXShift, bbox[1] + imageYShift, size, context);
      this.drawCircle(bbox[0] + bbox[2] + imageXShift - size, bbox[1] + imageYShift, size, context);
      this.drawCircle(bbox[0] + imageXShift, bbox[1] + bbox[3] + imageYShift  - size, size, context);
      this.drawCircle( bbox[0] + bbox[2] + imageXShift  - size, bbox[1] + bbox[3] + imageYShift  - size, size, context);
    }

  }

  private static getResizeBoxSize(scale) {
    return Math.floor(15 / scale);
  }

  private static drawCircle(x, y, size, context) {
    context.fillStyle = '#fafafa';

    context.beginPath();
    context.fillRect(x, y, size, size);
    context.strokeStyle = 'black';
    context.strokeRect(x, y, size, size);
    context.stroke();
  }

  private static setSelectedResizeBox(annotation, scale) {
    let size = this.getResizeBoxSize(scale);

    if (annotation.bbox[0] <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + size  &&
      annotation.bbox[1] <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + size) {
      this.pressedResizeTopLeft = true;
      return true;
    } else if (annotation.bbox[0] + annotation.bbox[2] - size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + annotation.bbox[2] &&
      annotation.bbox[1] <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + size) {
      this.pressedResizeTopRight = true;
      return true;
    } else if (annotation.bbox[0] <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + size &&
      annotation.bbox[1] + annotation.bbox[3] >= this.lastImageCordY && this.lastImageCordY >= annotation.bbox[1] + annotation.bbox[3] - size) {
      this.pressedResizeButtonLeft = true;
      return true;
    } else if (annotation.bbox[0] + annotation.bbox[2] - size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + annotation.bbox[2] &&
      annotation.bbox[1] + annotation.bbox[3] >= this.lastImageCordY && this.lastImageCordY >= annotation.bbox[1] + annotation.bbox[3] - size) {
      this.pressedResizeButtonRight = true;
      return true;
    }
    return false;
  }


}