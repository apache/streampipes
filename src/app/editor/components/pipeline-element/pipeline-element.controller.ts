/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

export class PipelineElementController {

    ImageChecker: any;
    ElementIconText: any;
    showImage: any;
    iconText: any;
    pipelineElement: any;
    preview: any;
    iconSize: any;
    iconStandSize: any;
    iconUrl: any;
    RestApi: any;

    constructor(ImageChecker, ElementIconText, RestApi) {
        this.ImageChecker = ImageChecker;
        this.ElementIconText = ElementIconText;
        this.RestApi = RestApi;
        this.showImage = false;
    }

    $onInit() {
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

PipelineElementController.$inject=['ImageChecker', 'ElementIconText', 'RestApi'];