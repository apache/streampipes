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

import { Injectable } from '@angular/core';
import Konva from 'konva';
import { Annotation } from '../../../core-model/coco/Annotation';
import { ICoordinates } from '../model/coordinates';
import { ColorService } from './color.service';
import { LabelingModeService } from './LabelingMode.service';
import { Label } from '@streampipes/platform-services';

@Injectable()
export class ReactLabelingService {
    private lastPosition: ICoordinates;
    private reactSize: ICoordinates;

    private isLabeling: boolean;

    constructor(
        private colorService: ColorService,
        private labelingMode: LabelingModeService,
    ) {}

    startLabeling(position: ICoordinates) {
        this.isLabeling = true;
        this.lastPosition = position;
        this.reactSize = { x: 0, y: 0 };
    }

    executeLabeling(position: ICoordinates) {
        this.reactSize.x = position.x - this.lastPosition.x;
        this.reactSize.y = position.y - this.lastPosition.y;
    }

    endLabeling(position: ICoordinates) {
        this.isLabeling = false;
        if (this.reactSize.x > 0 || this.reactSize.y > 0) {
            return [this.lastPosition, this.reactSize];
        }
    }

    tempDraw(layer: Konva.Layer, shift: ICoordinates, label: Label) {
        if (this.isLabeling && (this.reactSize.x > 0 || this.reactSize.y > 0)) {
            const box = new Konva.Rect({
                x: this.lastPosition.x + shift.x,
                y: this.lastPosition.y + shift.y,
                width: this.reactSize.x,
                height: this.reactSize.y,
                fill: label.color,
                opacity: 0.5,
                stroke: 'black',
                strokeWidth: 4,
                draggable: false,
            });
            layer.add(box);
        }
    }

    draw(
        layer: Konva.Layer,
        shift: ICoordinates,
        annotation: Annotation,
        imageView,
        color: string,
    ) {
        const rect = new Konva.Rect({
            x: annotation.bbox[0] + shift.x,
            y: annotation.bbox[1] + shift.y,
            width: annotation.bbox[2],
            height: annotation.bbox[3],
            fill: color,
            opacity: 0.5,
            stroke: 'black',
            strokeWidth: 4,
            draggable: true,
        });

        const transformer = new Konva.Transformer({
            anchorFill: '#FFFFFF',
            anchorSize: 10,
            rotateEnabled: false,
            keepRatio: false,
            borderStroke: 'green',
        });

        if (annotation.isSelected) {
            transformer.attachTo(rect);
        }

        this.addDragHandler(rect, annotation, imageView, this.labelingMode);
        this.addTransformHandler(rect, annotation, imageView);
        this.addMouseHandler(
            rect,
            annotation,
            layer,
            transformer,
            this.labelingMode,
        );
        this.addClickHandler(
            rect,
            annotation,
            layer,
            transformer,
            this.labelingMode,
        );

        layer.add(rect);
        layer.add(transformer);
    }

    private addClickHandler(
        rect,
        annotation,
        layer,
        transformer,
        labelingMode,
    ) {
        rect.on('click', function () {
            if (labelingMode.isNoneMode()) {
                annotation.isSelected = !annotation.isSelected;

                if (annotation.isSelected) {
                    transformer.attachTo(this);
                } else {
                    transformer.detach();
                }

                layer.batchDraw();
            }
        });
    }

    private addMouseHandler(
        rect,
        annotation,
        layer,
        transformer,
        labelingMode,
    ) {
        rect.on('mouseover', function () {
            if (labelingMode.isNoneMode()) {
                annotation.isHovered = true;
                rect.opacity(0.8);
                layer.batchDraw();
            }
        });

        rect.on('mouseout', function () {
            annotation.isHovered = false;
            rect.opacity(0.5);
            layer.batchDraw();
        });

        transformer.on('mouseover', function () {
            if (labelingMode.isNoneMode()) {
                annotation.isHovered = true;
            }
        });

        transformer.on('mouseout', function () {
            annotation.isHovered = false;
        });
    }

    private addTransformHandler(rect, annotation, imageView) {
        let resizer: string;

        rect.on('transformstart', function (e) {
            resizer = e.evt.currentTarget.parent.movingResizer;
        });

        rect.on('transform', function (e) {
            const position = imageView.getImagePointerPosition();
            if (resizer === 'top-left') {
                annotation.bbox[2] += annotation.bbox[0] - position.x;
                annotation.bbox[3] += annotation.bbox[1] - position.y;
                annotation.bbox[0] = position.x;
                annotation.bbox[1] = position.y;
            } else if (resizer === 'top-right') {
                annotation.bbox[2] = Math.abs(annotation.bbox[0] - position.x);
                annotation.bbox[3] += annotation.bbox[1] - position.y;
                annotation.bbox[1] = position.y;
            } else if (resizer === 'bottom-left') {
                annotation.bbox[2] += annotation.bbox[0] - position.x;
                annotation.bbox[3] = Math.abs(annotation.bbox[1] - position.y);
                annotation.bbox[0] = position.x;
            } else if (resizer === 'bottom-right') {
                annotation.bbox[2] = Math.abs(annotation.bbox[0] - position.x);
                annotation.bbox[3] = Math.abs(annotation.bbox[1] - position.y);
            } else if (resizer === 'top-center') {
                annotation.bbox[3] += annotation.bbox[1] - position.y;
                annotation.bbox[1] = position.y;
            } else if (resizer === 'middle-right') {
                annotation.bbox[2] = Math.abs(annotation.bbox[0] - position.x);
            } else if (resizer === 'middle-left') {
                annotation.bbox[2] += annotation.bbox[0] - position.x;
                annotation.bbox[0] = position.x;
            } else if (resizer === 'bottom-center') {
                annotation.bbox[3] = Math.abs(annotation.bbox[1] - position.y);
            }
        });
    }

    private addDragHandler(rect, annotation, imageView, labelingMode) {
        let offset: ICoordinates;

        rect.on('dragstart', function () {
            if (!labelingMode.isNoneMode()) {
                rect.stopDrag();
            } else {
                const position = imageView.getImagePointerPosition();
                offset = {
                    x: annotation.bbox[0] - position.x,
                    y: annotation.bbox[1] - position.y,
                };
            }
        });

        rect.on('dragmove', function () {
            if (!labelingMode.isNoneMode()) {
                rect.stopDrag();
            } else {
                const position = imageView.getImagePointerPosition();
                annotation.bbox[0] =
                    imageView.getImagePointerPosition().x + offset.x;
                annotation.bbox[1] =
                    imageView.getImagePointerPosition().y + offset.y;
            }
        });
    }
}
