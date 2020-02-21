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
  private static pressedTopLeft = false;
  private static pressedTopRight = false;
  private static pressedButtomLeft = false;
  private static pressedButtomRight = false;
  private static pressedTop = false;
  private static pressedRight = false;
  private static pressedLeft = false;
  private static pressedButtom = false;

  private static offSetX;
  private static offSetY;

  static mouseDownCreate(imageCord) {
    this.lastImageCordX = imageCord[0];
    this.lastImageCordY = imageCord[1];
    this.isMouseDown = true;
  }

  static mouseDownTransform(imageCord, annotation, scale) {
    //check if pressing movement button
    this.lastImageCordX = imageCord[0];
    this.lastImageCordY = imageCord[1];

    this.setSelectedResizeBox(annotation, scale);

    this.offSetX = annotation.bbox[0] - imageCord[0];
    this.offSetY = annotation.bbox[1] - imageCord[1];
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

  static mouseMoveTansform(imageCord, annotation) {

    if (this.pressedTopLeft) {
      annotation.bbox[2] += annotation.bbox[0]- imageCord[0];
      annotation.bbox[3] += annotation.bbox[1]- imageCord[1];
      annotation.bbox[0] = imageCord[0];
      annotation.bbox[1] = imageCord[1];
    } else if (this.pressedTopRight) {
      annotation.bbox[2] = Math.abs(annotation.bbox[0] - imageCord[0]);
      annotation.bbox[3] += annotation.bbox[1] - imageCord[1];
      annotation.bbox[1] = imageCord[1];
    } else if (this.pressedButtomLeft) {
      annotation.bbox[2] += annotation.bbox[0]- imageCord[0];
      annotation.bbox[3] = Math.abs(annotation.bbox[1] - imageCord[1]);
      annotation.bbox[0] = imageCord[0];
    } else if (this.pressedButtomRight) {
      annotation.bbox[2] = Math.abs(annotation.bbox[0] - imageCord[0]);
      annotation.bbox[3] = Math.abs(annotation.bbox[1] - imageCord[1]);
    } else if (this.pressedTop) {
      annotation.bbox[3] += annotation.bbox[1] - imageCord[1];
      annotation.bbox[1] = imageCord[1];
    } else if (this.pressedRight) {
      annotation.bbox[2] = Math.abs(annotation.bbox[0] - imageCord[0]);
    } else if (this.pressedLeft) {
      annotation.bbox[2] += annotation.bbox[0] - imageCord[0];
      annotation.bbox[0] = imageCord[0];
    } else if (this.pressedButtom) {
      annotation.bbox[3] = Math.abs(annotation.bbox[1] - imageCord[1]);
    } else {
      annotation.bbox[0] = imageCord[0] + this.offSetX ;
      annotation.bbox[1] = imageCord[1] + this.offSetY;
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

    //Control boxes for transformation
    if (annotation.isSelected) {
      let size = this.getResizeBoxSize(scale);
      this.drawTransformBox(bbox[0] + imageXShift, bbox[1] + imageYShift, size, size, context);
      this.drawTransformBox(bbox[0] + bbox[2] + imageXShift - size, bbox[1] + imageYShift, size, size, context);
      this.drawTransformBox(bbox[0] + imageXShift, bbox[1] + bbox[3] + imageYShift  - size, size, size, context);
      this.drawTransformBox( bbox[0] + bbox[2] + imageXShift  - size, bbox[1] + bbox[3] + imageYShift  - size, size, size, context);

      this.drawTransformBox(bbox[0] + imageXShift + size, bbox[1] + imageYShift, annotation.bbox[2] - 2* size, size, context);
      this.drawTransformBox(bbox[0] + imageXShift, bbox[1] + imageYShift + size, size, annotation.bbox[3] - 2* size, context);
      this.drawTransformBox(bbox[0] + bbox[2] + imageXShift - size, bbox[1] + imageYShift + size, size, annotation.bbox[3] - 2* size, context);
      this.drawTransformBox(bbox[0] + imageXShift + size, bbox[1] + bbox[3] + imageYShift - size, annotation.bbox[2] - 2* size, size, context);
    }

  }

  private static getResizeBoxSize(scale) {
    return Math.floor(15 / scale);
  }

  private static drawTransformBox(x, y, widght, heigt, context) {
    //context.fillStyle = '#fafafa';

    context.beginPath();
   // context.fillRect(x, y, widght, heigt);
    //context.strokeStyle = 'black';
    context.strokeRect(x, y, widght, heigt);
    context.stroke();
  }

  private static setSelectedResizeBox(annotation, scale) {
    this.pressedTopLeft = false;
    this.pressedTopRight = false;
    this.pressedButtomLeft = false;
    this.pressedButtomRight = false;
    this.pressedTop = false;
    this.pressedRight = false;
    this.pressedLeft = false;
    this.pressedButtom = false;

    let size = this.getResizeBoxSize(scale);

    if (annotation.bbox[0] <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + size  &&
      annotation.bbox[1] <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + size) {
      this.pressedTopLeft = true;
    } else if (annotation.bbox[0] + annotation.bbox[2] - size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + annotation.bbox[2] &&
      annotation.bbox[1] <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + size) {
      this.pressedTopRight = true;
    } else if (annotation.bbox[0] <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + size &&
      annotation.bbox[1] + annotation.bbox[3] >= this.lastImageCordY && this.lastImageCordY >= annotation.bbox[1] + annotation.bbox[3] - size) {
      this.pressedButtomLeft = true;
      return true;
    } else if (annotation.bbox[0] + annotation.bbox[2] - size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + annotation.bbox[2] &&
      annotation.bbox[1] + annotation.bbox[3] >= this.lastImageCordY && this.lastImageCordY >= annotation.bbox[1] + annotation.bbox[3] - size) {
      this.pressedButtomRight = true;
    } else if(annotation.bbox[0] + size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + annotation.bbox[2] - size  &&
      annotation.bbox[1] <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + size) {
      this.pressedTop = true;
      console.log('pressed top')
    } else if (annotation.bbox[0] + annotation.bbox[2] - size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + annotation.bbox[2] &&
      annotation.bbox[1] + size <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + annotation.bbox[3]  - size) {
      this.pressedRight = true;
      console.log('pressedRight')
    } else if (annotation.bbox[0] - size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + size &&
      annotation.bbox[1] + size <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + annotation.bbox[3]  - size) {
      this.pressedLeft = true;
      console.log('pressedLeft')
    } else if (annotation.bbox[0] + size <= this.lastImageCordX && this.lastImageCordX <= annotation.bbox[0] + annotation.bbox[2] - size  &&
      annotation.bbox[1] + annotation.bbox[3] - size <= this.lastImageCordY && this.lastImageCordY <= annotation.bbox[1] + annotation.bbox[3]) {
      this.pressedButtom = true;
      console.log('pressedButtom')
    }
  }


}