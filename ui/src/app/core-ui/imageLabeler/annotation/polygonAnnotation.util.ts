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

import { ColorUtil } from "../util/color.util";
import * as pointInPolygon from 'point-in-polygon';

export class PolygonAnnotationUtil {

  private static backgroundHoverAlpha = 0.6;
  private static backgroundAlpha = 0.2;

  public static isLabeling;
  private static points;

  //mouse position
  private static lastImageCordX = 0;
  private static lastImageCordY = 0;

  private static selectedPoint = undefined;

  static mouseDownCreate(imageCord) {
    if (this.isLabeling) {
      this.points.push(imageCord)
    } else {
      this.isLabeling = true;
      this.points = [];
      this.points.push(imageCord)
    }
  }

  static mouseDownTransform(imageCord, annotation, scale) {
    this.lastImageCordX = imageCord[0];
    this.lastImageCordY = imageCord[1];

    this.selectedPoint = this.getPointId(imageCord, annotation, scale);
  }

  static mouseMoveCreate(imageCord, imageXShift, imageYShift, context, label) {
    context.strokeStyle = ColorUtil.getColor(label);
    context.fillStyle = context.strokeStyle;
    context.globalAlpha = this.backgroundHoverAlpha;

    context.beginPath();
    context.moveTo(this.points[0][0] + imageXShift, this.points[0][1] + imageYShift);
    for (var i = 1; i < this.points.length; i++) {
      context.lineTo(this.points[i][0] + imageXShift, this.points[i][1] + imageYShift);
    }

    context.lineTo(imageCord[0] + imageXShift, imageCord[1] + imageYShift);
    context.closePath();
    context.stroke();
    context.fill();
    context.globalAlpha = 1;
  }

  static mouseMoveTransform(imageCord, annotation) {
    if (this.selectedPoint !== undefined) {
      console.log(2 * this.selectedPoint)
      annotation.segmentation[0][2 * this.selectedPoint] = imageCord[0];
      annotation.segmentation[0][2 * this.selectedPoint + 1]  = imageCord[1];
    }
  }

  static finishCreate(imageCords, coco, labelId) {
    this.isLabeling = false;
    //this.points.push(imageCords);

    let points = [];
    for (let i = 0; i < this.points.length-1 ; i+=1) {
      points.push(this.points[i][0]);
      points.push(this.points[i][1]);
    }
    console.log(points);
    coco.addPolygonAnnotation(points, labelId)
  }

  static draw(annotation, label, context, imageXShift, imageYShift, scale) {
    if (annotation.isHovered) {
      context.globalAlpha = this.backgroundHoverAlpha
    } else if (annotation.isSelected) {
      context.globalAlpha = this.backgroundHoverAlpha;
    } else {
      context.globalAlpha = this.backgroundAlpha;
    }
    context.strokeStyle = ColorUtil.getColor(label);
    context.fillStyle = context.strokeStyle;

    context.beginPath();
    let points = annotation.segmentation[0];

    context.moveTo(points[0] + imageXShift, points[1] + imageYShift);
    for (var i = 2; i < points.length; i+=2) {
      context.lineTo(points[i] + imageXShift, points[i+1] + imageYShift);
    }
    context.closePath();
    context.fill();
    context.globalAlpha = 1;
    context.stroke();

    if (annotation.isSelected) {
      let size = this.getResizeCirlceSize(scale);

      for (var i = 0; i < points.length; i+=2) {
        context.beginPath();
        context.arc(points[i] + imageXShift, points[i+1] + imageYShift, size, 0, 2 * Math.PI);
        context.fill();
      }
    }
  }

  private static getResizeCirlceSize(scale) {
    return Math.floor(10 / scale);
  }

  private static getPointId(imageCords, annotation, scale) {
    let points =  annotation.segmentation[0];
    for (var i = 0; i < points.length; i+=2) {
      if (Math.abs(imageCords[0] - points[i]) < this.getResizeCirlceSize(scale) &&
        Math.abs(imageCords[1] - points[i+1]) < this.getResizeCirlceSize(scale)) {
        return i / 2;
      }
    }
    return undefined;
  }

  public static checkIfClicked(imageCords, annotation, scale) {
    let points = [];
    for (var i = 0; i < annotation.segmentation[0].length; i+=2) {
      points.push([annotation.segmentation[0][i], annotation.segmentation[0][i+1]]);
    }
    let result = pointInPolygon(imageCords, points);
    if (result) {
      return result;
    }
    if (annotation.isSelected && this.getPointId(imageCords, annotation, scale) !== undefined) {
      return true;
    }
    return false;
  }

}
