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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Annotation } from '../../../../core-model/coco/Annotation';
import { Label } from '@streampipes/platform-services';

@Component({
    selector: 'sp-image-annotations',
    templateUrl: './image-annotations.component.html',
    styleUrls: ['./image-annotations.component.css'],
})
export class ImageAnnotationsComponent {
    @Input() annotations: Annotation[];

    @Input()
    public labels: Label[];

    @Output() changeAnnotationLabel: EventEmitter<[Annotation, Label]> =
        new EventEmitter<[Annotation, Label]>();
    @Output() deleteAnnotation: EventEmitter<Annotation> =
        new EventEmitter<Annotation>();

    constructor() {}

    changeLabel(change: [Annotation, Label]) {
        this.changeAnnotationLabel.emit([change[0], change[1]]);
    }

    delete(annotation) {
        this.deleteAnnotation.emit(annotation);
    }

    enterAnnotation(annotation) {
        annotation.isHovered = true;
    }

    leaveAnnotation(annotation) {
        annotation.isHovered = false;
    }
}
