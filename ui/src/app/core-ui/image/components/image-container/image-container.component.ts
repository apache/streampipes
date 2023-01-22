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

import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';

@Component({
    selector: 'sp-image-container',
    templateUrl: './image-container.component.html',
    styleUrls: ['./image-container.component.css'],
})
export class ImageContainerComponent implements OnInit {
    imagePath: SafeUrl;
    showImage = false;

    @Input()
    set imageSrc(src: Observable<Blob>) {
        src.subscribe(url => {
            this.imagePath = url;
            this.showImage = true;
        });
    }

    @ViewChild('mainImg') imageRef: ElementRef;

    @Input()
    public canvasHeight = 500;

    @Input()
    public canvasWidth = 800;

    private scale: number;

    public isDrawingVar: boolean;

    constructor() {}

    ngOnInit(): void {
        this.scale = 1;
    }

    onImageLoaded() {
        const naturalImgWidth = (
            this.imageRef.nativeElement as HTMLImageElement
        ).naturalWidth;
        const naturalImageHeight = (
            this.imageRef.nativeElement as HTMLImageElement
        ).naturalHeight;
        this.scale = Math.min(
            1,
            this.canvasWidth / naturalImgWidth,
            this.canvasHeight / naturalImageHeight,
        );
    }
}
