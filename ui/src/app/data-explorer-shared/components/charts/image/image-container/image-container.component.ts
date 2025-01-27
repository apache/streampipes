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

import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';

@Component({
    selector: 'sp-image-container',
    templateUrl: './image-container.component.html',
    styleUrls: ['./image-container.component.scss'],
})
export class SpImageContainerComponent {
    imagePath: SafeUrl;
    showImage = false;

    @ViewChild('mainImg') imageRef: ElementRef;

    @Input()
    public canvasHeight = 500;

    @Input()
    public canvasWidth = 800;

    imageWidth = 0;
    imageHeight = 0;

    constructor() {}

    onImageLoaded() {
        const naturalImageWidth = (
            this.imageRef.nativeElement as HTMLImageElement
        ).naturalWidth;
        const naturalImageHeight = (
            this.imageRef.nativeElement as HTMLImageElement
        ).naturalHeight;
        this.calculateImageDimensions(naturalImageWidth, naturalImageHeight);
    }

    calculateImageDimensions(imageWidth: number, imageHeight: number): void {
        const canvasAspectRatio = this.canvasWidth / this.canvasHeight;
        const imageAspectRatio = imageWidth / imageHeight;

        let finalWidth: number;
        let finalHeight: number;

        if (imageAspectRatio > canvasAspectRatio) {
            finalWidth = this.canvasWidth;
            finalHeight = this.canvasWidth / imageAspectRatio;
        } else {
            finalHeight = this.canvasHeight;
            finalWidth = this.canvasHeight * imageAspectRatio;
        }

        this.imageWidth = finalWidth;
        this.imageHeight = finalHeight;
    }

    @Input()
    set imageSrc(src: Observable<Blob>) {
        src.subscribe(url => {
            this.imagePath = url;
            this.showImage = true;
        });
    }
}
