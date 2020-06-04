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

import {
    Component, Input,
    OnInit,
} from "@angular/core";
import * as angular from "angular";
import {RestApi} from "../../../services/rest-api.service";
import {ElementIconText} from "../../../services/get-element-icon-text.service";
import {ImageChecker} from "../../../services/image-checker.service";


@Component({
    selector: 'pipeline-element',
    templateUrl: './pipeline-element.component.html',
    styleUrls: ['./pipeline-element.component.css']
})
export class PipelineElementComponent implements OnInit {

    showImage: any;
    iconText: any;

    @Input()
    pipelineElement: any;

    @Input()
    preview: any;

    @Input()
    iconSize: any;

    @Input()
    iconStandSize: any;

    iconUrl: any;

    constructor(private ImageChecker: ImageChecker,
                private RestApi: RestApi,
                private ElementIconText: ElementIconText) {

    }

    ngOnInit(): void {
        console.log(this.pipelineElement);
        this.iconText =  this.ElementIconText.getElementIconText(this.pipelineElement.name);
        this.checkImageAvailable();
    }

    checkImageAvailable() {
        if (this.pipelineElement.includesAssets) {
            this.fetchImage(this.makeAssetIconUrl())
        } else {
            this.fetchImage(this.pipelineElement.iconUrl);
        }
    }

    fetchImage(imageUrl) {
        this.ImageChecker.imageExists(imageUrl, (exists) => {
            this.iconUrl = imageUrl;
            this.showImage = exists;
        })
    }

    makeAssetIconUrl() {
        return this.RestApi.getAssetUrl(this.pipelineElement.appId) +"/icon";
    }

    iconSizeCss() {
        if (this.iconSize) {
            return 'width:35px;height:35px;';
        }
        else if (this.preview) {
            return 'width:50px;height:50px;';
        } else if (this.iconStandSize) {
            return 'width:50px;height:50px;margin-top:-5px;'
        } else {
            return 'width:70px;height:70px;';
        }
    }


}