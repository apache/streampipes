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
export class PolygonLabelingService {
    private points: number[];
    private tmpPoints: number[];

    private isLabeling: boolean;

    constructor(
        private colorService: ColorService,
        private labelingMode: LabelingModeService,
    ) {
        this.isLabeling = false;
    }

    static buildAnchors(layer, annotation, imageView, poly) {
        this.removeAnchors(layer, annotation.id);
        const anchorGroup = new Konva.Group({
            name: 'anchor_group_' + annotation.id,
        });

        for (let i = 0; i < annotation.segmentation[0].length; i += 2) {
            PolygonLabelingService.buildAnchor(
                layer,
                annotation,
                i,
                imageView,
                poly,
                anchorGroup,
            );
        }
        layer.add(anchorGroup);
    }

    static buildAnchor(layer, annotation, index, imageView, poly, anchorGroup) {
        const shift: ICoordinates = imageView.getShift();

        const xx = annotation.segmentation[0][index] + shift.x;
        const yy = annotation.segmentation[0][index + 1] + shift.y;
        const anchor = new Konva.Circle({
            name: 'anchor_' + annotation.id,
            x: xx,
            y: yy,
            radius: 10,
            fill: 'white',
            stroke: 'green',
            strokeWidth: 4,
            draggable: true,
        });
        anchorGroup.add(anchor);

        anchor.on('mouseover', function () {
            annotation.isHovered = true;
        });

        anchor.on('mouseout', function () {
            annotation.isHovered = false;
        });

        anchor.on('dragmove', function () {
            const position = imageView.getImagePointerPosition();
            const _shift: ICoordinates = imageView.getShift();

            annotation.segmentation[0][index] = position.x;
            annotation.segmentation[0][index + 1] = position.y;

            const tmp = [];
            for (let i = 0; i < annotation.segmentation[0].length; i += 2) {
                tmp.push(annotation.segmentation[0][i] + _shift.x);
                tmp.push(annotation.segmentation[0][i + 1] + _shift.y);
            }
            poly.points(tmp);
            poly.x(0);
            poly.y(0);
            poly.draw();
        });
    }

    static removeAnchors(layer, annotationId) {
        const tmp = [];
        for (const child of layer.children) {
            if (child.name() === 'anchor_group_' + annotationId) {
                child.destroy();
            }
        }
        layer.batchDraw();
    }

    startLabeling(position: ICoordinates) {
        if (this.isLabeling) {
            this.points.push(position.x);
            this.points.push(position.y);
        } else {
            this.isLabeling = true;
            this.points = [];
            this.tmpPoints = [];
            this.points.push(position.x);
            this.points.push(position.y);
        }
    }

    executeLabeling(position: ICoordinates) {
        if (this.isLabeling) {
            this.tmpPoints = Object.assign([], this.points);
            this.tmpPoints.push(position.x);
            this.tmpPoints.push(position.y);
        }
    }

    endLabeling(position: ICoordinates) {
        this.isLabeling = false;
        return this.points.slice(0, this.points.length - 2);
    }

    tempDraw(layer: Konva.Layer, shift: ICoordinates, label: Label) {
        if (this.isLabeling) {
            const tmp = [];
            for (let i = 0; i < this.tmpPoints.length; i += 2) {
                tmp.push(this.tmpPoints[i] + shift.x);
                tmp.push(this.tmpPoints[i + 1] + shift.y);
            }

            const poly = new Konva.Line({
                points: tmp,
                fill: label.color,
                stroke: 'black',
                opacity: 0.8,
                strokeWidth: 4,
                closed: true,
            });
            layer.add(poly);
        }
    }

    draw(
        layer: Konva.Layer,
        shift: ICoordinates,
        annotation: Annotation,
        imageView,
    ) {
        const tmp = [];
        for (let i = 0; i < annotation.segmentation[0].length; i += 2) {
            tmp.push(annotation.segmentation[0][i] + shift.x);
            tmp.push(annotation.segmentation[0][i + 1] + shift.y);
        }

        const poly = new Konva.Line({
            points: tmp,
            fill: annotation.color,
            stroke: 'black',
            opacity: 0.5,
            strokeWidth: 4,
            closed: true,
            draggable: true,
        });

        const transformer = new Konva.Transformer({
            anchorFill: '#FFFFFF',
            anchorSize: 10,
            rotateEnabled: false,
            enabledAnchors: [],
            borderStroke: 'green',
        });

        if (annotation.isSelected) {
            transformer.attachTo(poly);
            PolygonLabelingService.buildAnchors(
                layer,
                annotation,
                imageView,
                poly,
            );
        }

        this.addDragHandler(
            poly,
            annotation,
            layer,
            imageView,
            this.labelingMode,
        );
        this.addMouseHandler(
            poly,
            annotation,
            layer,
            transformer,
            this.labelingMode,
        );
        this.addClickHandler(
            poly,
            annotation,
            layer,
            transformer,
            imageView,
            this.labelingMode,
        );

        layer.add(poly);
        layer.add(transformer);
    }

    private addClickHandler(
        poly,
        annotation,
        layer,
        transformer,
        imageView,
        labelingMode,
    ) {
        poly.on('click', function () {
            if (labelingMode.isNoneMode()) {
                annotation.isSelected = !annotation.isSelected;

                if (annotation.isSelected) {
                    annotation.isSelected = true;
                    transformer.attachTo(this);
                    PolygonLabelingService.buildAnchors(
                        layer,
                        annotation,
                        imageView,
                        poly,
                    );
                } else {
                    PolygonLabelingService.removeAnchors(layer, annotation.id);
                    transformer.detach();
                }
                layer.batchDraw();
            }
        });
    }

    private addMouseHandler(
        poly,
        annotation,
        layer,
        transformer,
        labelingMode,
    ) {
        poly.on('mouseover', function () {
            if (labelingMode.isNoneMode()) {
                annotation.isHovered = true;
                poly.opacity(0.8);
                layer.batchDraw();
            }
        });

        poly.on('mouseout', function () {
            annotation.isHovered = false;
            this.opacity(0.5);
            layer.batchDraw();
        });
    }

    private addDragHandler(poly, annotation, layer, imageView, labelingMode) {
        let offset: number[];

        poly.on('dragstart', function () {
            if (!labelingMode.isNoneMode()) {
                poly.stopDrag();
            } else {
                const position = imageView.getImagePointerPosition();
                offset = [];
                for (let i = 0; i < annotation.segmentation[0].length; i += 2) {
                    offset.push(annotation.segmentation[0][i] - position.x);
                    offset.push(annotation.segmentation[0][i + 1] - position.y);
                }
            }
        });

        poly.on('dragmove', function () {
            if (!labelingMode.isNoneMode()) {
                poly.stopDrag();
            } else {
                const position = imageView.getImagePointerPosition();
                const tmp = [];
                for (let i = 0; i < annotation.segmentation[0].length; i += 2) {
                    tmp.push(position.x + offset[i]);
                    tmp.push(position.y + offset[i + 1]);
                }
                annotation.segmentation[0] = tmp;
                if (annotation.isSelected) {
                    PolygonLabelingService.buildAnchors(
                        layer,
                        annotation,
                        imageView,
                        poly,
                    );
                }
            }
        });
    }
}
