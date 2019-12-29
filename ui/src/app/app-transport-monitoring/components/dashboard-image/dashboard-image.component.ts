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

import {Component, EventEmitter, Input, Output, SecurityContext} from '@angular/core';
import {ParcelInfoEventModel} from "../../model/parcel-info-event.model";
import {DomSanitizer, SafeStyle, SafeValue} from "@angular/platform-browser";

@Component({
    selector: 'dashboard-image',
    templateUrl: './dashboard-image.component.html',
    styleUrls: ['./dashboard-image.component.scss']
})
export class DashboardImageComponent {

    //@Input() parcelInfoEventModel: ParcelInfoEventModel[];
    imageData: any[];

    currentIndex: number = 0;

    sanitizer: DomSanitizer;

    constructor(sanitizer: DomSanitizer) {
        this.sanitizer = sanitizer;
        this.imageData = [];
    }

    ngOnInit() {

    }

    @Input()
    set parcelInfoEventModel(parcelInfoEventModel: ParcelInfoEventModel[]) {
        this.imageData = [];
        let index = this.getIndex(parcelInfoEventModel)
        //parcelInfoEventModel.forEach(parcelInfo => {
            this.imageData.push('data:image/jpeg;base64,' + parcelInfoEventModel[index].segmentationImage);
        //});
    }

    getIndex(parcelInfoEventModel: ParcelInfoEventModel[]) {
        return parcelInfoEventModel.length > 1 ? 1 : 0;
    }

    getSanitizedImageUrl(imageUrl) {
        this.sanitizer.sanitize(SecurityContext.STYLE, `url(${imageUrl})`);
    }

    sanitize(image: string): any {
        return this.sanitizer.bypassSecurityTrustStyle(`url(${image})`);
    }
}