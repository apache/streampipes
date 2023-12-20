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

import { Component, Input } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';

@Component({
    selector: 'sp-image-viewer',
    templateUrl: './image-viewer.component.html',
    styleUrls: ['./image-viewer.component.css'],
})
export class ImageViewerComponent {
    @Input()
    public imagesRoutes: Observable<SafeUrl>[];

    @Input()
    public canvasHeight = 500;

    @Input()
    public canvasWidth = 800;

    @Input()
    public imagePreviewHeight = 65;

    public imagesIndex = 0;

    constructor() {}

    handleImageIndexChange(index) {
        this.imagesIndex = index;
    }
}
