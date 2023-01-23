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
import { Annotation } from '../../../core-model/coco/Annotation';
import { Category } from '../../../core-model/coco/Category';
import { CocoFormat } from '../../../core-model/coco/Coco.format';
import { Image } from '../../../core-model/coco/Image';

@Injectable()
export class CocoFormatService {
    static getLabelId(
        coco: CocoFormat,
        supercategory,
        name,
        labelName,
    ): number {
        // TODO: Find  better solution instead of copy same code
        let category = coco.categories.find(
            elem => elem.name === name && elem.supercategory === supercategory,
        );
        if (category === undefined) {
            category = new Category(
                coco.categories.length + 1,
                name,
                supercategory,
                labelName,
            );
            coco.categories.push(category);
        }
        return category.id;
    }

    addImage(coco: CocoFormat, fileName) {
        const image = new Image();
        image.file_name = fileName;
        image.id = coco.images.length + 1;
        coco.images.push(image);
    }

    getLabelById(coco: CocoFormat, id) {
        return coco.categories.find(elem => elem.id === id).name;
    }

    getLabelId(coco: CocoFormat, supercategory, name, labelName): number {
        let category = coco.categories.find(
            elem => elem.name === name && elem.supercategory === supercategory,
        );
        if (category === undefined) {
            category = new Category(
                coco.categories.length + 1,
                name,
                supercategory,
                labelName,
            );
            coco.categories.push(category);
        }
        return category.id;
    }

    addReactAnnotationToFirstImage(
        coco: CocoFormat,
        cords,
        size,
        supercategory,
        category,
        color,
        labelName,
    ): Annotation {
        const annotation = new Annotation();
        annotation.id = coco.annotations.length + 1;
        annotation.iscrowd = 0;
        annotation.image_id = 1;
        annotation.bbox = [cords.x, cords.y, size.x, size.y];
        annotation.area = size.x * size.y;
        annotation.category_id = CocoFormatService.getLabelId(
            coco,
            supercategory,
            category,
            labelName,
        );
        annotation.category_name = category;
        annotation.color = color;
        annotation.label_name = labelName;
        coco.annotations.push(annotation);
        return annotation;
    }

    addPolygonAnnotationFirstImage(
        coco: CocoFormat,
        points,
        supercategory,
        category,
        labelName,
    ): Annotation {
        const annotation = new Annotation();
        annotation.id = coco.annotations.length + 1;
        annotation.iscrowd = 0;
        annotation.image_id = 1;
        annotation.segmentation = [points];
        annotation.category_id = CocoFormatService.getLabelId(
            coco,
            supercategory,
            category,
            labelName,
        );
        annotation.category_name = category;
        coco.annotations.push(annotation);
        return annotation;
    }

    addBrushAnnotationFirstImage(
        coco: CocoFormat,
        points,
        brushSize,
        supercategory,
        category,
        labelName,
    ): Annotation {
        const annotation = new Annotation();
        annotation.id = coco.annotations.length + 1;
        annotation.iscrowd = 0;
        annotation.image_id = 1;
        annotation.segmentation = [points];
        annotation.brushSize = brushSize;
        annotation.category_id = this.getLabelId(
            coco,
            supercategory,
            category,
            labelName,
        );
        annotation.category_name = category;
        coco.annotations.push(annotation);
        return annotation;
    }

    removeAnnotation(coco: CocoFormat, id) {
        coco.annotations = coco.annotations.filter(anno => anno.id !== id);
    }

    isBoxAnnonation(annotation: Annotation) {
        return annotation.bbox !== undefined;
    }

    isPolygonAnnonation(annotation: Annotation) {
        return (
            annotation.segmentation !== undefined &&
            annotation.brushSize === undefined
        );
    }

    isBrushAnnonation(annotation: Annotation) {
        return annotation.brushSize !== undefined;
    }
}
