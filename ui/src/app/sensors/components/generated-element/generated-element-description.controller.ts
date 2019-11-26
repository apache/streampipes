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

export class GeneratedElementDescriptionController {

    element: any;
    jsonld: any;

    constructor() {

    }

    downloadJsonLd() {
        this.openSaveAsDialog(this.element.name + ".jsonld", this.jsonld, "application/json");
    }

    downloadJava() {
        this.openSaveAsDialog(this.element.name + ".java", this.jsonld, "application/java");
    }

    openSaveAsDialog(filename, content, mediaType) {
        var blob = new Blob([content], {type: mediaType});
        // TODO: saveAs not implemented
        //this.saveAs(blob, filename);
    }
}